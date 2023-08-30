package com.nagarro.si.pba.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nagarro.si.pba.exceptions.ExceptionMessage;
import com.nagarro.si.pba.exceptions.InvalidInputException;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String extractFieldFromJson(String fieldName, String jsonString) throws InvalidInputException {
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw new InvalidInputException(ExceptionMessage.JSON_INVALID.format());
        }
        return jsonNode.get(fieldName).asText();
    }
}