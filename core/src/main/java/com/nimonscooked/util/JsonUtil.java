package com.nimonscooked.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.function.Supplier;

public class JsonUtil {

    private static final Json json = new Json();

    static {
        json.setOutputType(JsonWriter.OutputType.json);
        json.setIgnoreUnknownFields(true);
        json.setUsePrototypes(false);
        json.setQuoteLongValues(true);
    }

    public static void toJson(Object object, FileHandle file) {
        if (file == null) {
            Gdx.app.error("JsonUtil", "Cannot write to null file");
            return;
        }
        
        try {
            String jsonString = json.prettyPrint(object, 120);
            file.writeString(jsonString, false, "UTF-8");
            Gdx.app.log("JsonUtil", "Successfully wrote JSON to: " + file.path());
        } catch (Exception ex) {
            Gdx.app.error("JsonUtil", "Error writing JSON to " + file.path(), ex);
        }
    }

    public static <T> T fromJson(Class<T> type, FileHandle file) {
        if (file == null) {
            Gdx.app.error("JsonUtil", "Cannot read from null file");
            return null;
        }
        
        if (!file.exists()) {
            Gdx.app.error("JsonUtil", "File does not exist: " + file.path());
            return null;
        }
        
        try {
            T result = json.fromJson(type, file);
            Gdx.app.log("JsonUtil", "Successfully loaded JSON from: " + file.path());
            return result;
        } catch (Exception ex) {
            Gdx.app.error("JsonUtil", "Error reading JSON from " + file.path(), ex);
            return null;
        }
    }

    public static <T> T fromJson(Class<T> type, FileHandle file, Supplier<T> defaultSupplier) {
        T result = fromJson(type, file);
        if (result == null && defaultSupplier != null) {
            Gdx.app.log("JsonUtil", "Using default value for " + type.getSimpleName());
            return defaultSupplier.get();
        }
        return result;
    }

    public static <T> T fromJsonString(Class<T> type, String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            Gdx.app.error("JsonUtil", "Cannot parse null or empty JSON string");
            return null;
        }
        
        try {
            return json.fromJson(type, jsonString);
        } catch (Exception ex) {
            Gdx.app.error("JsonUtil", "Error parsing JSON string", ex);
            return null;
        }
    }

    public static String toJsonString(Object object) {
        if (object == null) {
            return "null";
        }
        
        try {
            return json.prettyPrint(object);
        } catch (Exception ex) {
            Gdx.app.error("JsonUtil", "Error converting object to JSON string", ex);
            return "{}";
        }
    }

    public static void setPrettyPrint(boolean pretty) {
        if (pretty) {
            json.setOutputType(JsonWriter.OutputType.json);
        } else {
            json.setOutputType(JsonWriter.OutputType.minimal);
        }
    }

    public static Json getJson() {
        return json;
    }
}