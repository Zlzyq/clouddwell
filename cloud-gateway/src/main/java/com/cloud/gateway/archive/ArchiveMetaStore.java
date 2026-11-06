package com.cloud.gateway.archive;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 档案元数据 JSON 持久化（位于 uploadDir/meta.json）。
 */
public class ArchiveMetaStore {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private final Path metaFile;

    public ArchiveMetaStore(Path rootDir) throws IOException {
        Files.createDirectories(rootDir);
        this.metaFile = rootDir.resolve("meta.json");
        if (!Files.exists(metaFile)) {
            saveAll(new ArrayList<>());
        }
    }

    public synchronized List<ArchiveRecord> findAll() throws IOException {
        return new ArrayList<>(loadAll());
    }

    public synchronized Optional<ArchiveRecord> findById(Long id) throws IOException {
        return loadAll().stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    public synchronized ArchiveRecord insert(ArchiveRecord record) throws IOException {
        List<ArchiveRecord> list = loadAll();
        long nextId = list.stream().map(ArchiveRecord::getId).max(Comparator.naturalOrder()).orElse(0L) + 1;
        record.setId(nextId);
        list.add(record);
        saveAll(list);
        return record;
    }

    public synchronized ArchiveRecord update(ArchiveRecord record) throws IOException {
        List<ArchiveRecord> list = loadAll();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(record.getId())) {
                list.set(i, record);
                saveAll(list);
                return record;
            }
        }
        return null;
    }

    public synchronized boolean deleteById(Long id) throws IOException {
        List<ArchiveRecord> list = loadAll();
        boolean removed = list.removeIf(r -> r.getId().equals(id));
        if (removed) {
            saveAll(list);
        }
        return removed;
    }

    private List<ArchiveRecord> loadAll() throws IOException {
        if (!Files.exists(metaFile) || Files.size(metaFile) == 0) {
            return new ArrayList<>();
        }
        return MAPPER.readValue(metaFile.toFile(), new TypeReference<List<ArchiveRecord>>() {
        });
    }

    private void saveAll(List<ArchiveRecord> list) throws IOException {
        MAPPER.writeValue(metaFile.toFile(), list);
    }
}
