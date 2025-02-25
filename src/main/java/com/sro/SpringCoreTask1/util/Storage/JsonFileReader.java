package com.sro.SpringCoreTask1.util.storage;

import java.io.InputStream;

import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sro.SpringCoreTask1.exceptions.StorageInitializationException;

public class JsonFileReader {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    public static <T> T readJsonFile(Resource resource, Class<T> valueType) {
        if (resource == null || !resource.exists()) {
            throw new StorageInitializationException("Resource not found: " + (resource != null ? resource.getFilename() : "null"));
        }
        
        try (InputStream inputStream = resource.getInputStream()) {
            return OBJECT_MAPPER.readValue(inputStream, valueType);
        } catch (Exception e) {
            throw new StorageInitializationException("Error reading JSON file: " + resource.getFilename(), e);
        }
    }
}
