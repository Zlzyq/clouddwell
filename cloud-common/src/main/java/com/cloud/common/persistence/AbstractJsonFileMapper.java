package com.cloud.common.persistence;

import com.cloud.common.domain.Identifiable;
import com.cloud.common.exception.GlobalException;
import com.cloud.common.persistence.JsonFileRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractJsonFileMapper<T extends Identifiable> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final JsonFileRepository<T> repository;
    private final TypeReference<List<T>> typeRef;
    private final String seedClasspath;
    private final Consumer<T> onInsert;

    protected AbstractJsonFileMapper(Path dataFile,
                                     TypeReference<List<T>> typeRef,
                                     String seedClasspath,
                                     Consumer<T> onInsert) throws IOException {
        this.typeRef = typeRef;
        this.repository = new JsonFileRepository<>(dataFile, typeRef);
        this.seedClasspath = seedClasspath;
        this.onInsert = onInsert != null ? onInsert : (r) -> { };
        initSeed();
    }

    private void initSeed() throws IOException {
        ClassPathResource resource = new ClassPathResource(seedClasspath);
        if (!resource.exists()) return;
        try (InputStream in = resource.getInputStream()) {
            repository.initFromSeedIfEmpty(in);
        }
    }

    public String dataFilePath() { return repository.dataFilePath().toString(); }

    public List<T> selectAll() {
        return run(repository::findAll).stream()
                .sorted(Comparator.comparing(Identifiable::getId).reversed())
                .toList();
    }

    public T selectById(Long id) {
        return run(() -> repository.findById(id)
                .orElseThrow(() -> new GlobalException("记录不存在")));
    }

    public T insert(T entity) {
        return run(() -> { onInsert.accept(entity); return repository.insert(entity); });
    }

    public T updateById(Long id, T entity) {
        return run(() -> {
            if (repository.findById(id).isEmpty()) throw new GlobalException("记录不存在");
            entity.setId(id);
            T updated = repository.update(entity);
            if (updated == null) throw new GlobalException("更新失败");
            return updated;
        });
    }

    public void deleteById(Long id) {
        run(() -> { if (!repository.deleteById(id)) throw new GlobalException("记录不存在"); return null; });
    }

    public List<T> resetFromSeed() {
        return run(() -> {
            ClassPathResource resource = new ClassPathResource(seedClasspath);
            if (!resource.exists()) { repository.replaceAll(List.of()); return List.of(); }
            try (InputStream in = resource.getInputStream()) {
                List<T> seed = MAPPER.readValue(in, typeRef);
                for (int i = 0; i < seed.size(); i++) {
                    T item = seed.get(i);
                    if (item.getId() == null) item.setId((long) (i + 1));
                }
                repository.replaceAll(seed);
                return seed;
            }
        });
    }

    protected static boolean isBlank(String s) { return !StringUtils.hasText(s); }

    private <R> R run(IoSupplier<R> supplier) {
        try { return supplier.get(); }
        catch (GlobalException ex) { throw ex; }
        catch (IOException ex) { throw new GlobalException("读写业务数据失败：" + ex.getMessage()); }
    }

    @FunctionalInterface
    private interface IoSupplier<R> { R get() throws IOException; }
}
