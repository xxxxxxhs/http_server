package RequestHandlers;

public class PathAnalyzer {

    public static String getLastSegment(String path) {
        if (!path.contains("/")) return path;
        return path.substring(path.lastIndexOf("/") + 1);
    }
    public static String getBaseSegment(String path) {
        if (!path.contains("/")) return path;
        return path.substring(0, path.lastIndexOf("/"));
    }
    public static boolean isLastSegmentParseable(String path) {
        try {Long.parseLong(getLastSegment(path));
            return true;}
        catch (NumberFormatException e) {return false;}
    }
}
