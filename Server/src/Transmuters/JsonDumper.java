package Transmuters;

import java.io.*;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.lang.reflect.Type;
import java.nio.file.NoSuchFileException;

import CollectionClasses.Validatable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import CollectionClasses.Movie;

/*
 * class Dumper works with json-file, saves and loads collection
 */

public class JsonDumper {
    
    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new DateSerializer())
            .registerTypeAdapterFactory(new StrictTypeAdapterFactory())
            .create();
    public static String getJsonString(LinkedList<?> collection) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(gson.toJson(collection).getBytes(StandardCharsets.UTF_8));
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {e.printStackTrace();
        return null;}
    }

    public static <T> String getJsonString(T item) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(gson.toJson(item).getBytes(StandardCharsets.UTF_8));
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T extends Validatable> T getObjectFromJson(String jsonData, Class<T> type) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(jsonData))) {
            Type listedType = TypeToken.get(type).getType();
            String line;
            StringBuilder jsonLine = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                jsonLine.append(line);
            }
            T item = gson.fromJson(jsonLine.toString(), listedType);
            return item;
        }
    }
}
