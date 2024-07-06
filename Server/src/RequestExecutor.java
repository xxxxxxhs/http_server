import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

public class RequestExecutor {

    public static Response execute(ClientRequest request) {
        switch (request.getMethod()) {
            case "GET":
                return executeGetMethod(request.getPath());
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

    private static Response executeGetMethod(String path) {
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
