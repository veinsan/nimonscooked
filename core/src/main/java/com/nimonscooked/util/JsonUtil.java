package com.nimonscooked.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class JsonUtil {

    private static final Json json = new Json();

    static {
        json.setOutputType(JsonWriter.OutputType.json);
        json.setIgnoreUnknownFields(true);
    }

    public static void toJson(Object object, FileHandle file) {
        if (file == null) return;
        try {
            file.writeString(json.prettyPrint(object), false, "UTF-8");
        } catch (Exception ex) {
            Gdx.app.error("JsonUtil", "Error writing JSON: " + ex.getMessage());
        }
    }

    public static <T> T fromJson(Class<T> type, FileHandle file) {
        if (file == null || !file.exists()) return null;
        try {
            return json.fromJson(type, file);
        } catch (Exception ex) {
            Gdx.app.error("JsonUtil", "Error reading JSON: " + ex.getMessage());
            return null;
        }
    }
}