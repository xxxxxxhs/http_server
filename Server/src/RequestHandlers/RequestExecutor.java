package RequestHandlers;

import CollectionClasses.Movie;
import Transmuters.JsonDumper;
import Transmuters.SqlDumper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;

public class RequestExecutor {
    private static HashMap<String, Method> availablePathsForGetDB = new HashMap();
    private static SqlDumper sqlDumper = new SqlDumper("jdbc:postgresql://localhost:5432/postgres",
            "postgres", "123");
    private static JsonDumper jsonDumper = new JsonDumper();

    static {
        try {
            availablePathsForGetDB.put("Server/Resources/api/movie",
                    sqlDumper.getClass().getDeclaredMethod("loadMovie", HashMap.class));
            availablePathsForGetDB.put("Server/Resources/api/person",
                    sqlDumper.getClass().getDeclaredMethod("loadPerson", HashMap.class));
            availablePathsForGetDB.put("Server/Resources/api/location",
                    sqlDumper.getClass().getDeclaredMethod("loadLocation", HashMap.class));
            availablePathsForGetDB.put("Server/Resources/api/coordinates",
                    sqlDumper.getClass().getDeclaredMethod("loadCoordinates", HashMap.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Response execute(ClientRequest request) {
        switch (request.getMethod()) {
            case "GET":
                return executeGetMethod(request.getPath(), request.getHeaders());
            case "POST":
                return Response.getBadRequestResponse();
            case "PUT":
                return Response.getBadRequestResponse();
            case "DELETE":
                return Response.getBadRequestResponse();
            default:
                return Response.getBadRequestResponse();
        }
    }

    private static Response executeGetMethod(String path, HashMap<String, String> headers) {
        if (availablePathsForGetDB.containsKey(path)) {
            try {
                LinkedList<?> collection = (LinkedList<?>) availablePathsForGetDB.get(path).invoke(sqlDumper, headers);
                if (collection.isEmpty()) return Response.getNoContentResponse();
                return new Response(200, "application/json",
                        jsonDumper.getJsonString(collection).getBytes());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
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
}
