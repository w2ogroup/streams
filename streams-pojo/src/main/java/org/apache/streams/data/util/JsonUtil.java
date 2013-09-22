package org.apache.streams.data.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * JSON utilities
 */
public class JsonUtil {

    private JsonUtil() {}

    public static JsonNode jsonToJsonNode(String json) {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();

        JsonNode node;
        try {
            JsonParser jp = factory.createJsonParser(json);
            node = mapper.readTree(jp);
        } catch (IOException e) {
            throw new RuntimeException("IO exception while reading JSON", e);
        }
        return node;
    }

    public static String jsonNodeToJson(JsonNode node) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("IO exception while writing JSON", e);
        }
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Could not map to object");
        }
    }

    public static <T> String objectToJson(T object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("Could not map to object");
        }
    }
}
