package com.nagarro.si.pba.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nagarro.si.pba.exceptions.InvalidPathException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public abstract class TestUtils {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static String asJsonString(final Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static String readFromFile(String filePath) {
        try {
            InputStream inputStream = TestUtils.class.getClassLoader().getResourceAsStream(filePath);

            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        } catch (NullPointerException e) {
            throw new InvalidPathException(filePath);
        }
    }
}