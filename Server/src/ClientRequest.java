import java.util.HashMap;

/*
Класс-обертка клиентского запроса
 */
public class ClientRequest {
    private String path;
    private String method;
    private String version;
    private HashMap<String, String> headers;
    private String body;
    public ClientRequest(String request) {
        System.out.println(request);
        String[] lines = request.split("\n");
        String firstLine = lines[0];
        this.method = firstLine.split(" ")[0];
        this.path = "Server/Resources" + firstLine.split(" ")[1];
        this.version = firstLine.split(" ")[2];
        this.headers = new HashMap<>();

        int i = 1;
        while (i < lines.length && !lines[i].isEmpty()) {
            String[] header = lines[i].split(": ", 2);
            if (header.length == 2) {
                headers.put(header[0], header[1]);
            }
            i++;
        }
        i++;
        StringBuilder sb = new StringBuilder();
        while (i < lines.length) {
            sb.append(lines[i]).append("\n");
            i++;
        }
        this.body = sb.length() > 0 ? sb.toString().trim() : null;
    }
    public String getMethod() {return method;}
    public String getPath() {return path;}
    public String getVersion() {return version;}
    public boolean isHasBody() {return (body == null) ? false : true;}
    public String getBody() {return body;}
    public HashMap getHeaders() {return headers;}
    public String getHeader(String key) {
        return (headers.containsKey(key)) ? headers.get(key) : null;
    }
}
