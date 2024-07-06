public class Response {

    private int code;
    private String codeDescription;
    private String version;
    private String contentType;
    private int contentLength;
    private String body;

    public Response(String version, int code, String codeDescription, String contentType, int contentLength, String body){
        this.body = body;
        this.code = code;
        this.codeDescription = codeDescription;
        this.contentLength = contentLength;
        this.contentType = contentType;
        this.version = version;
    }
    @Override
    public String toString() {
        return version + " " + code + " " + codeDescription + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + contentLength + "\r\n" + "\r\n" +
                body;
    }
}
