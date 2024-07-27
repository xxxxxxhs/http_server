package RequestHandlers;

import RequestHandlers.RequestExecutor;

import java.util.HashMap;
import java.util.HashSet;

public class RequestHandler {
    private static final HashMap<String, Boolean> availableMethods = new HashMap<>();
    private static final HashSet<String> availableVersions = new HashSet<String>();
    private static final HashSet<String> availableContentTypes = new HashSet<>();
    static {
        availableMethods.put("GET", true); // true, если для http-метода не важен content-type
        availableMethods.put("POST", false); //false, если методу важен переданный в body тип контента

        availableVersions.add("HTTP/1.1");
        availableVersions.add("HTTP/1.0");

        availableContentTypes.add("application/json");
    }

    public static Response handleRequest(String request){
        try {
            ClientRequest clientRequest = new ClientRequest(request);
            System.out.println(clientRequest.toString());
            if (!availableMethods.containsKey(clientRequest.getMethod())) return Response.getBadRequestResponse();
            if (!availableMethods.get(clientRequest.getMethod())) {
                HashMap headers = clientRequest.getHeaders();
                if (!headers.containsKey("Content-Type")) {
                    Response response = Response.getBadRequestResponse();
                    response.setBody("<h1>Header 'Content-Type' hasn't been found</h1>");
                    return response;
                }
                if (!availableContentTypes.contains(headers.get("Content-Type"))) {
                    Response response = Response.getBadRequestResponse();
                    response.setBody("<h1>Such content type isn't available</h1>");
                    return response;
                }
            }
            if (!availableVersions.contains(clientRequest.getVersion())) return Response.getBadRequestResponse();
            if (clientRequest.getVersion().equals("HTTP/1.1") && !clientRequest.getHeaders().containsKey("Host")) {
                return Response.getBadRequestResponse();
            }
            return RequestExecutor.execute(clientRequest);
        } catch (ArrayIndexOutOfBoundsException e) {
            return Response.getBadRequestResponse();}
    }

}
