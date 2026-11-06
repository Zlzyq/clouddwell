package com.cloud.common.persistence;

import com.cloud.common.domain.Identifiable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class JsonFileRepository<T extends Identifiable> {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private final Path dataFile;
    private final TypeReference<List<T>> listType;

    public JsonFileRepository(Path dataFile, TypeReference<List<T>> listType) throws IOException {
        this.dataFile = dataFile;
        this.listType = listType;
        Files.createDirectories(dataFile.getParent());
        if (!Files.exists(dataFile)) {
            saveAll(new ArrayList<>());
        }
    }

    public Path dataFilePath() { return dataFile; }

    public synchronized List<T> findAll() throws IOException {
        return new ArrayList<>(loadAll());
    }

    public synchronized Optional<T> findById(Long id) throws IOException {
        return loadAll().stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    public synchronized T insert(T record) throws IOException {
        List<T> list = loadAll();
        long nextId = list.stream().map(Identifiable::getId).max(Comparator.naturalOrder()).orElse(0L) + 1;
        record.setId(nextId);
        list.add(record);
        saveAll(list);
        return record;
    }

    public synchronized T update(T record) throws IOException {
        List<T> list = loadAll();
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
        List<T> list = loadAll();
        boolean removed = list.removeIf(r -> r.getId().equals(id));
        if (removed) saveAll(list);
        return removed;
    }

    public synchronized void replaceAll(List<T> records) throws IOException {
        saveAll(new ArrayList<>(records));
    }

    public synchronized void initFromSeedIfEmpty(InputStream seedStream) throws IOException {
        List<T> current = loadAll();
        if (!current.isEmpty() || seedStream == null) return;
        List<T> seed = MAPPER.readValue(seedStream, listType);
        for (int i = 0; i < seed.size(); i++) {
            T item = seed.get(i);
            if (item.getId() == null) item.setId((long) (i + 1));
        }
        saveAll(seed);
    }

    private List<T> loadAll() throws IOException {
        if (!Files.exists(dataFile) || Files.size(dataFile) == 0) return new ArrayList<>();
        return MAPPER.readValue(dataFile.toFile(), listType);
    }

    private void saveAll(List<T> list) throws IOException {
        MAPPER.writeValue(dataFile.toFile(), list);
    }
}
