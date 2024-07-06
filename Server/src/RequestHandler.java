import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestHandler {
    private static HashSet<String> availableMethods = new HashSet<String>();
    private static HashSet<String> availableVersions = new HashSet<String>();
    static {
        availableMethods.add("GET");

        availableVersions.add("HTTP/1.1");
        availableVersions.add("HTTP/1.0");
    }

    public static Response handleRequest(String request){
        try {
            ClientRequest clientRequest = new ClientRequest(request);
            if (!availableMethods.contains(clientRequest.getMethod())) return Response.getBadRequestResponse();
            if (!availableVersions.contains(clientRequest.getVersion())) return Response.getBadRequestResponse();
            if (clientRequest.getVersion().equals("HTTP/1.1") && !clientRequest.getHeaders().containsKey("Host")) {
                return Response.getBadRequestResponse();
            }
            return RequestExecutor.execute(clientRequest);
        } catch (ArrayIndexOutOfBoundsException e) {
            return Response.getBadRequestResponse();}
    }

}
