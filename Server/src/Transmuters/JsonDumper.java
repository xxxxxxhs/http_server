package Transmuters;

import java.io.*;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.lang.reflect.Type;
import java.nio.file.NoSuchFileException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import CollectionClasses.Movie;

/*
 * class Dumper works with json-file, saves and loads collection
 */

public class JsonDumper {
    
    final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDateTime.class, new DateSerializer())
        .create();
    public String getJsonString(LinkedList<?> collection) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(gson.toJson(collection).getBytes(StandardCharsets.UTF_8));
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {e.printStackTrace();
        return null;}
    }

    public LinkedList<Movie> getListFromJson(String jsonData) {
        try (BufferedReader reader = new BufferedReader(new StringReader(jsonData))) {
            Type filmsType = new TypeToken<LinkedList<Movie>>(){}.getType();
            String line;
            String jsonLine = "";
            while ((line = reader.readLine()) != null) {
                jsonLine += line;
            }
            LinkedList<Movie> films = gson.fromJson(jsonLine, filmsType);
            reader.close();
            return films;
        } catch (Exception e) {
            System.out.println("smth went wrong");
        }
        System.out.println("collection hasn't been upload");
        return null;
    }
}
