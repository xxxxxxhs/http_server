package RequestHandlers;

import CollectionClasses.*;
import Transmuters.JsonDumper;
import Transmuters.SqlDumper;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class RequestExecutor {
    private static HashMap<String, Method> availablePathsForGetDB = new HashMap();
    private static HashMap<String, Class> availablePathsForPostDB = new HashMap<>();
    private static SqlDumper sqlDumper = new SqlDumper("jdbc:postgresql://localhost:5432/postgres",
            "postgres", "123");
    private static JsonDumper jsonDumper = new JsonDumper();

    static {
        try {
            availablePathsForGetDB.put("Server/Resources/api/movies",
                    sqlDumper.getClass().getDeclaredMethod("loadMovie", HashMap.class));
            availablePathsForGetDB.put("Server/Resources/api/persons",
                    sqlDumper.getClass().getDeclaredMethod("loadPerson", HashMap.class));
            availablePathsForGetDB.put("Server/Resources/api/locations",
                    sqlDumper.getClass().getDeclaredMethod("loadLocation", HashMap.class));
            availablePathsForGetDB.put("Server/Resources/api/coordinatess",
                    sqlDumper.getClass().getDeclaredMethod("loadCoordinates", HashMap.class));

            availablePathsForPostDB.put("Server/Resources/api/movie", Movie.class);
            availablePathsForPostDB.put("Server/Resources/api/person", Person.class);
            availablePathsForPostDB.put("Server/Resources/api/location", Location.class);
            availablePathsForPostDB.put("Server/Resources/api/coordinates", Coordinates.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Response execute(ClientRequest request) {
        switch (request.getMethod()) {
            case "GET":
                return executeGetMethod(request.getPath(), request.getHeaders());
            case "POST":
                return executePostMethod(request);
            case "PUT":
                return Response.getBadRequestResponse();
            case "DELETE":
                return Response.getBadRequestResponse();
            default:
                return Response.getBadRequestResponse();
        }
    }

    private static Response executeGetMethod(String path, HashMap<String, String> headers) {
        System.out.println(path);
        System.out.println(path.concat("s"));
        if (availablePathsForGetDB.containsKey(path)) {
            try {
                LinkedList<?> collection = (LinkedList<?>) availablePathsForGetDB.get(path).invoke(sqlDumper, headers);
                if (collection.isEmpty()) return Response.getNoContentResponse();
                return new Response(Response.OK_CODE, "application/json",
                        jsonDumper.getJsonString(collection).getBytes());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else if (availablePathsForGetDB.containsKey(path.concat("s"))) {
            if (!headers.containsKey("id")) {
                Response response = Response.getBadRequestResponse();
                response.setBody("<h1>When you try to get concrete object you should specify ID</h1>");
                return response;
            }
            try {
                LinkedList<?> collection = (LinkedList<?>) availablePathsForGetDB
                        .get(path.concat("s")).invoke(sqlDumper, headers);
                if (collection.isEmpty()) return Response.getNoContentResponse();
                return new Response(Response.OK_CODE, "application/json",
                        jsonDumper.getJsonString(collection.getFirst()).getBytes());
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        if (!isResourceExists(path)) {
            return Response.getNotFoundResponse();
        }
        try {
            return new Response(Response.OK_CODE, Response.getContentTypeByFileType(path),
                    Files.readAllBytes(Path.of(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static boolean isResourceExists(String path) {
        return Files.exists(Path.of(path));
    }

    private static Response executePostMethod(ClientRequest request) {
        String path = request.getPath();
        if (availablePathsForPostDB.containsKey(path)) {
            try {
                Validatable item = JsonDumper.getObjectFromJson(request.getBody(), availablePathsForPostDB.get(path));
                if (item.validate()) {
                    Method addMethod = sqlDumper.getClass().getDeclaredMethod("add", item.getClass());
                    int addedId = (int) addMethod.invoke(sqlDumper, item);
                    return new Response(200, "text/html",
                            String.format("<h1>Item added with id: %d</h1>", addedId).getBytes(StandardCharsets.UTF_8));
                } else {
                    Response response = Response.getBadRequestResponse();
                    response.setBody("<h1>Incorrect data, object wasn't been validated</h1>");
                    return response;
                }
            } catch (IOException | JsonParseException e) {
                Response response = Response.getBadRequestResponse();
                response.setBody("<h1>Got incorrect data, check json-file</h1>");
                return response;
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                Response response = Response.getBadRequestResponse();
                response.setBody("<h1>" + e.getMessage() + "</h1>");
                return response;
            }
        } else {return Response.getNotFoundResponse();}
    }
}
