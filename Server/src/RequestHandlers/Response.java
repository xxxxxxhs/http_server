package RequestHandlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Response {

    private int code;
    private String codeDescription;
    private final String version = "HTTP/1.1";
    private String contentType;
    private int contentLength;
    private byte[] body;
    private HashMap<Integer, String> codeDescriptions;
    private HashMap<String, String> contentTypes;
    public static final int OK_CODE = 200;
    public static final int BAD_REQUEST_CODE = 400;
    public static final int NOT_FOUND_CODE = 404;
    public static final int CONFLICT_CODE = 409;
    public static final int NO_CONTENT_CODE = 204;
    public static final int NO_RIGHTS_CODE = 403;

    {
        codeDescriptions = new HashMap<>();
        codeDescriptions.put(400, "Bad request");
        codeDescriptions.put(404, "Not found");
        codeDescriptions.put(200, "OK");
        codeDescriptions.put(204, "No suitable content");
        codeDescriptions.put(409, "Conflict");
        codeDescriptions.put(403, "Not enough rights");

        contentTypes = new HashMap<>();
        contentTypes.put("html", "text/html");
        contentTypes.put("json", "application/json");
        contentTypes.put("txt", "text/plain");
    }
    public Response(int code, String contentType, byte[] body){
        this.body = body;
        this.code = code;
        this.codeDescription = codeDescriptions.get(code);
        this.contentLength = body.length;
        this.contentType = contentType;
    }
    @Override
    public String toString() {
        return version + " " + code + " " + codeDescription + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + contentLength + "\r\n" + "\r\n" +
                body;
    }
    public String gerResponseHeader() {
        return version + " " + code + " " + codeDescription + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + contentLength + "\r\n" + "\r\n";
    }
    public byte[] getResponseBody() {return body;}
    public static Response getBadRequestResponse() {
        return new Response(BAD_REQUEST_CODE, "text/html",
                "<h1>Bad request</h1>".getBytes(StandardCharsets.UTF_8));
    }
    public static Response getNotFoundResponse() {
        return new Response(NOT_FOUND_CODE, "text/html",
                "<h1>Not found</h1>".getBytes(StandardCharsets.UTF_8));
    }
    public static Response getConflictResponse() {
        return new Response(CONFLICT_CODE, "text/html",
                "<h1>Conflict</h1>".getBytes(StandardCharsets.UTF_8));
    }
    public static Response getNoContentResponse() {
        return new Response(NO_CONTENT_CODE, "text/html",
                "<h1>No suitable content</h1>".getBytes(StandardCharsets.UTF_8));
    }
    public static Response getNotEnoughRightsResponse() {
        return new Response(NO_RIGHTS_CODE, "text/html",
                "<h1>Not enough rights to access</h1>".getBytes(StandardCharsets.UTF_8));
    }

    public static String getContentTypeByFileType(String filePath) {
        try {
            return Files.probeContentType(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
