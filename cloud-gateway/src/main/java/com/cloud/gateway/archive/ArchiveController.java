package com.cloud.gateway.archive;

import com.cloud.common.domain.Result;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 档案管理 API（文件落盘在网关应用目录 cloud.gateway.archive.upload-dir）。
 */
@RestController
@RequestMapping("/api/archives")
public class ArchiveController {

    private final ArchiveService archiveService;

    public ArchiveController(ArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    @GetMapping("/storage-path")
    public Mono<Result<String>> storagePath() {
        return Mono.just(Result.ok(archiveService.rootDirPath()));
    }

    @GetMapping
    public Mono<Result<List<ArchiveRecord>>> list() {
        return archiveService.list().map(Result::ok);
    }

    @GetMapping("/{id}")
    public Mono<Result<ArchiveRecord>> get(@PathVariable Long id) {
        return archiveService.get(id).map(Result::ok);
    }

    @PostMapping
    public Mono<Result<ArchiveRecord>> create(@RequestBody ArchiveRequest request) {
        return archiveService.create(request).map(Result::ok);
    }

    @PutMapping("/{id}")
    public Mono<Result<ArchiveRecord>> update(@PathVariable Long id, @RequestBody ArchiveRequest request) {
        return archiveService.update(id, request).map(Result::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<Result<Void>> delete(@PathVariable Long id) {
        return archiveService.delete(id).thenReturn(Result.ok());
    }

    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Result<ArchiveRecord>> upload(@PathVariable Long id, @RequestPart("file") FilePart file) {
        return archiveService.uploadFile(id, file).map(Result::ok);
    }

    @GetMapping("/{id}/file")
    public Mono<ResponseEntity<Resource>> download(@PathVariable Long id) {
        return archiveService.get(id)
                .flatMap(record -> archiveService.loadFileResource(id)
                        .map(resource -> {
                            String filename = record.getOriginalFileName() != null
                                    ? record.getOriginalFileName() : "download";
                            String contentType = record.getContentType() != null
                                    ? record.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
                            return ResponseEntity.ok()
                                    .header(HttpHeaders.CONTENT_DISPOSITION,
                                            "attachment; filename=\"" + filename + "\"")
                                    .contentType(MediaType.parseMediaType(contentType))
                                    .body(resource);
                        }));
    }
}
