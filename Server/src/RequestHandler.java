import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestHandler {
    private static final String version = "HTTP/1.1";
    private static HashMap<String, Boolean> availableMethods = new HashMap<String, Boolean>();
    private static HashSet<String> availableVersions = new HashSet<>();
    static {
        availableMethods.put("GET", false);

        availableVersions.add("HTTP/1.0");
        availableVersions.add("HTTP/1.1");
    }
    public static Response handle(String request) {
        boolean check = checkRequest(request);
        System.out.println(check + " request checked (" + request + ")");
        if (!check) {
            String body = "<h1>Bad request</h1>";
            int contentLength = body.getBytes().length;
            return new Response(version, 400, "Bad request",
                    "text/html", contentLength, body);
        }

        return new Response(version, 200, "OK", "text/html",
                "<h1>OK</h1>".getBytes().length, "<h1>OK</h1>");
    }

    private static boolean checkRequest(String request){
        request = request.replace("\\n", "\n");
        String[] lines = request.split("\n");
        if (!checkFirstLine(lines[0])) return false;
        else if (!checkHostHeading(lines[1])) return false;
        if (lines.length > 4) {
            for (int i = 2; i < lines.length - 2; i++) {
                if (!checkOtherHeading(lines[i])) return false;
            }
        }
        return true;
    }
    private static boolean checkFirstLine(String head) {
        String[] lines = head.split(" ");
        if (lines.length != 3) return false;
        else if (!availableMethods.containsKey(lines[0])) return false;
        else if (!lines[1].startsWith("/")) return false;
        else if (!availableVersions.contains(lines[2])) return false;
        else return true;
    }

    private static boolean checkHostHeading(String heading) {
        String regex = "Host: .+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(heading);
        return matcher.matches();
    }
    private static boolean checkOtherHeading(String heading) {
        String regex = "^[a-zA-Z0-9-]+: .+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(heading);
        return matcher.matches();
    }
}
