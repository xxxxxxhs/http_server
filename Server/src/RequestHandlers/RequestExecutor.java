package RequestHandlers;

import CollectionClasses.*;
import Transmuters.JsonDumper;
import Transmuters.SqlDumper;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;

/*
class which commutes request by method,
checks that path is correct,
and executes request regarding the method and params
 */

public class RequestExecutor {
    private static HashMap<String, Method> availablePathsForGetDB = new HashMap();
    private static HashMap<String, Class> availablePathsForChangeDB = new HashMap<>();
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

            availablePathsForChangeDB.put("Server/Resources/api/movie", Movie.class);
            availablePathsForChangeDB.put("Server/Resources/api/person", Person.class);
            availablePathsForChangeDB.put("Server/Resources/api/location", Location.class);
            availablePathsForChangeDB.put("Server/Resources/api/coordinates", Coordinates.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Response execute(ClientRequest request) {
        switch (request.getMethod()) {
            case "GET":
                return executeGetMethod(request);
            case "POST":
                return executePostMethod(request);
            case "PUT":
                return executePutMethod(request);
            case "DELETE":
                return executeDeleteMethod(request);
            default:
                return Response.getBadRequestResponse();
        }
    }

    private static Response executeGetMethod(ClientRequest request) {
        String path = request.getPath();
        HashMap<String, String> queryParams = request.getQueryParams();
        if (availablePathsForGetDB.containsKey(path)) {
            try {
                LinkedList<?> collection = (LinkedList<?>) availablePathsForGetDB.get(path).invoke(sqlDumper, queryParams);
                if (collection.isEmpty()) return Response.getNoContentResponse();
                return new Response(Response.OK_CODE, "application/json",
                        jsonDumper.getJsonString(collection).getBytes());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else if (availablePathsForGetDB.containsKey(PathAnalyzer.getBaseSegment(path))) {
            if (!PathAnalyzer.isLastSegmentParseable(path)) {
                return Response.getBadRequestResponse();
            } else {
                queryParams.put("id", PathAnalyzer.getLastSegment(path));
                path = PathAnalyzer.getBaseSegment(path);
            }
            try {
                LinkedList<?> collection = (LinkedList<?>) availablePathsForGetDB.get(path).invoke(sqlDumper, queryParams);
                if (collection.isEmpty()) return Response.getNoContentResponse();
                return new Response(Response.OK_CODE, "application/json",
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

    private static Response executePostMethod(ClientRequest request) {
        String path = request.getPath();
        if (availablePathsForChangeDB.containsKey(path)) {
            try {
                Validatable item = JsonDumper.getObjectFromJson(request.getBody(),
                        availablePathsForChangeDB.get(path));
                if (item.validate()) {
                    Method addMethod = sqlDumper.getClass().getDeclaredMethod("add", item.getClass());
                    int addedId = (int) addMethod.invoke(sqlDumper, item);
                    return new Response(Response.OK_CODE, "text/html",
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
    private static Response executePutMethod(ClientRequest request) {
        String path = request.getPath();
        if (availablePathsForChangeDB.containsKey(path)) {
            Response response = Response.getBadRequestResponse();
            response.setBody("<h1>Should set id of the changeable object</h1>");
            return response;
        } else if (availablePathsForChangeDB.containsKey(PathAnalyzer.getBaseSegment(path))) {
            if (!PathAnalyzer.isLastSegmentParseable(path)) {return Response.getBadRequestResponse();}
            long id = Long.parseLong(PathAnalyzer.getLastSegment(path));
            try {
                Validatable item = JsonDumper.getObjectFromJson(request.getBody(),
                        availablePathsForChangeDB.get(PathAnalyzer.getBaseSegment(path)));
                if (!sqlDumper.existsById(item.getClass().getSimpleName(), id)) {return Response.getNoContentResponse();}
                if (item.validate()) {
                    sqlDumper.getClass().getDeclaredMethod("update", item.getClass(), long.class)
                            .invoke(sqlDumper, item, id);
                    return new Response(Response.OK_CODE, "text/html",
                            "<h1>item updated</h1>".getBytes(StandardCharsets.UTF_8));
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
                e.printStackTrace();
                Response response = Response.getBadRequestResponse();
                response.setBody("<h1>" + e.getMessage() + "</h1>");
                return response;
            }
        } else {
            return Response.getNotFoundResponse();
        }
    }
    private static Response executeDeleteMethod(ClientRequest request) {
        String path = request.getPath();
        if (availablePathsForChangeDB.containsKey(path)) {
            Response response = Response.getBadRequestResponse();
            response.setBody("<h1>Should specify an id of object to delete</h1>");
            return response;
        } else if (availablePathsForChangeDB.containsKey(PathAnalyzer.getBaseSegment(path))) {
            if (!PathAnalyzer.isLastSegmentParseable(path)) {return Response.getBadRequestResponse();}
            Class itemClass = availablePathsForChangeDB.get(PathAnalyzer.getBaseSegment(path));
            long id = Long.parseLong(PathAnalyzer.getLastSegment(path));
            if (sqlDumper.existsById(itemClass.getSimpleName(), id)) {
                sqlDumper.removeById(itemClass.getSimpleName(), id);
                Response response = new Response(Response.OK_CODE, "text/html",
                        ("<h1>item w/ id " + id + " deleted</h1>").getBytes(StandardCharsets.UTF_8));
                return response;
            } else {
                return Response.getNoContentResponse();
            }
        } else {
            return Response.getNotFoundResponse();
        }
    }
}
