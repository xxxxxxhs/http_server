package RequestHandlers;

import java.util.HashMap;
import java.util.Map;

/*
Класс-обертка клиентского запроса
 */
public class ClientRequest {
    private String path;
    private String method;
    private String version;
    private HashMap<String, String> headers, queryParams;
    private String body;
    public ClientRequest(String request) {
        String[] lines = request.split("\n");
        String firstLine = lines[0];
        this.method = firstLine.split(" ")[0];
        this.path = "Server/Resources" + firstLine.split(" ")[1];
        this.version = firstLine.split(" ")[2].trim();
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();

        if (path.split("\\?").length == 2) {
            //getting query params from URL
            String[] params = path.split("\\?")[1].split("&");
            path = path.split("\\?")[0];

            for (String j : params) {
                queryParams.put(j.split("=")[0], j.split("=")[1]);
            }
        }

        int i = 1;
        while (i < lines.length && !lines[i].isEmpty()) {
            String[] header = lines[i].split(": ", 2);
            if (header.length == 2) {
                headers.put(header[0].trim(), header[1].trim());
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
    public HashMap getQueryParams() {return queryParams;}
    public String getHeader(String key) {
        return (headers.containsKey(key)) ? headers.get(key) : null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(method).append(" ").append(path).append(" ").append(version).append("\n");

        sb.append("\n");
        sb.append("----headers:\n");
        sb.append("\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        sb.append("\n");
        sb.append("----body:\n");
        sb.append("\n");
        if (body != null) {
            sb.append(body).append("\n");
        }

        return sb.toString();
    }
}
