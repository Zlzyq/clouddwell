package com.cloud.gateway.archive;

import com.cloud.common.exception.GlobalException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class ArchiveService {

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ArchiveProperties properties;
    private final Path rootDir;
    private final Path filesDir;
    private final ArchiveMetaStore metaStore;

    public ArchiveService(ArchiveProperties properties) throws IOException {
        this.properties = properties;
        this.rootDir = Path.of(properties.getUploadDir()).toAbsolutePath().normalize();
        this.filesDir = rootDir.resolve("files");
        Files.createDirectories(filesDir);
        this.metaStore = new ArchiveMetaStore(rootDir);
    }

    public Mono<List<ArchiveRecord>> list() {
        return Mono.fromCallable(metaStore::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .map(list -> list.stream()
                        .sorted(Comparator.comparing(ArchiveRecord::getId).reversed())
                        .toList());
    }

    public Mono<ArchiveRecord> get(Long id) {
        return Mono.fromCallable(() -> metaStore.findById(id)
                        .orElseThrow(() -> new GlobalException("档案不存在")))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<ArchiveRecord> create(ArchiveRequest request) {
        return Mono.fromCallable(() -> {
                    validateMeta(request.getTitle(), request.getCategory());
                    String now = now();
                    ArchiveRecord record = new ArchiveRecord();
                    record.setTitle(request.getTitle().trim());
                    record.setCategory(StringUtils.hasText(request.getCategory()) ? request.getCategory().trim() : "未分类");
                    record.setDescription(trimToNull(request.getDescription()));
                    record.setCreateTime(now);
                    record.setUpdateTime(now);
                    return metaStore.insert(record);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<ArchiveRecord> update(Long id, ArchiveRequest request) {
        return Mono.fromCallable(() -> {
                    ArchiveRecord record = metaStore.findById(id)
                            .orElseThrow(() -> new GlobalException("档案不存在"));
                    validateMeta(request.getTitle(), request.getCategory());
                    record.setTitle(request.getTitle().trim());
                    record.setCategory(StringUtils.hasText(request.getCategory()) ? request.getCategory().trim() : "未分类");
                    record.setDescription(trimToNull(request.getDescription()));
                    record.setUpdateTime(now());
                    return metaStore.update(record);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> delete(Long id) {
        return Mono.fromCallable(() -> {
                    ArchiveRecord record = metaStore.findById(id)
                            .orElseThrow(() -> new GlobalException("档案不存在"));
                    deleteFileDir(id);
                    metaStore.deleteById(id);
                    return record;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<ArchiveRecord> uploadFile(Long id, FilePart filePart) {
        return Mono.fromCallable(() -> metaStore.findById(id)
                        .orElseThrow(() -> new GlobalException("档案不存在")))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(record -> {
                    String originalName = filePart.filename();
                    if (!StringUtils.hasText(originalName)) {
                        return Mono.error(new GlobalException("文件名为空"));
                    }
                    String contentType = guessContentType(
                            filePart.headers().getContentType() != null
                                    ? filePart.headers().getContentType().toString() : null,
                            originalName);
                    String storedName = UUID.randomUUID().toString().replace("-", "") + extOf(originalName);
                    Path target = filePath(id, storedName);
                    return Mono.fromCallable(() -> {
                                deleteExistingFile(record);
                                Files.createDirectories(target.getParent());
                                return target;
                            })
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(path -> filePart.transferTo(path).then(Mono.fromCallable(() -> {
                                long size = Files.size(path);
                                if (size > properties.getMaxFileSize()) {
                                    Files.deleteIfExists(path);
                                    throw new GlobalException("文件超过大小限制 "
                                            + (properties.getMaxFileSize() / 1024 / 1024) + "MB");
                                }
                                record.setOriginalFileName(originalName);
                                record.setStoredFileName(storedName);
                                record.setContentType(contentType);
                                record.setFileSize(size);
                                record.setFileType(contentType.startsWith("image/") ? "IMAGE" : "FILE");
                                record.setUpdateTime(now());
                                return metaStore.update(record);
                            })).subscribeOn(Schedulers.boundedElastic()));
                });
    }

    public Mono<Resource> loadFileResource(Long id) {
        return Mono.fromCallable(() -> {
                    ArchiveRecord record = metaStore.findById(id)
                            .orElseThrow(() -> new GlobalException("档案不存在"));
                    if (!StringUtils.hasText(record.getStoredFileName())) {
                        throw new GlobalException("尚未上传文件");
                    }
                    Path path = filePath(id, record.getStoredFileName());
                    if (!Files.exists(path)) {
                        throw new GlobalException("文件不存在或已被删除");
                    }
                    Resource resource = new FileSystemResource(path);
                    return resource;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public String rootDirPath() {
        return rootDir.toString();
    }

    private Path filePath(Long id, String storedName) {
        return filesDir.resolve(String.valueOf(id)).resolve(storedName);
    }

    private void deleteFileDir(Long id) throws IOException {
        Path dir = filesDir.resolve(String.valueOf(id));
        if (Files.exists(dir)) {
            try (Stream<Path> walk = Files.walk(dir)) {
                walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException ignored) {
                    }
                });
            }
        }
    }

    private void deleteExistingFile(ArchiveRecord record) throws IOException {
        if (StringUtils.hasText(record.getStoredFileName())) {
            Files.deleteIfExists(filePath(record.getId(), record.getStoredFileName()));
        }
    }

    private static void validateMeta(String title, String category) {
        if (!StringUtils.hasText(title)) {
            throw new GlobalException("档案标题不能为空");
        }
    }

    private static String trimToNull(String s) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        return s.trim();
    }

    private static String now() {
        return LocalDateTime.now().format(DT);
    }

    private static String extOf(String filename) {
        int i = filename.lastIndexOf('.');
        if (i < 0) {
            return "";
        }
        return filename.substring(i);
    }

    private static String guessContentType(String headerType, String filename) {
        if (StringUtils.hasText(headerType)) {
            return headerType;
        }
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lower.endsWith(".gif")) {
            return "image/gif";
        }
        if (lower.endsWith(".webp")) {
            return "image/webp";
        }
        if (lower.endsWith(".pdf")) {
            return "application/pdf";
        }
        return "application/octet-stream";
    }

}
