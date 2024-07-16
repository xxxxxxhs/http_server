package Transmuters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SqlLoadConstructor {
    private static HashMap<String, Boolean> movieFields = new HashMap<>();
    private static HashSet<String> coordinatesFields = new HashSet<>();
    private static HashMap<String, Boolean> personFields = new HashMap<>();
    private static HashMap<String, Boolean> locationFields = new HashMap<>();

    static {
        movieFields.put("id", false);
        movieFields.put("coordinates-x", false);
        movieFields.put("coordinates-y", false);
        movieFields.put("creation-date", true);
        movieFields.put("oscars-count", false);
        movieFields.put("golden-palm-count", false);
        movieFields.put("total-box-office", false);
        movieFields.put("mpaa-rating-value", true);
        movieFields.put("screenwriter-name", true);
        movieFields.put("screenwriter-passport-id", true);
        movieFields.put("screenwriter-eye-color", true);
        movieFields.put("screenwriter-hair-color", true);
        movieFields.put("screenwriter-location-x", false);
        movieFields.put("screenwriter-location-y", false);
        movieFields.put("screenwriter-location-z", false);
        movieFields.put("screenwriter-location-name", true);

        coordinatesFields.add("id");
        coordinatesFields.add("x");
        coordinatesFields.add("y");

        personFields.put("id", false);
        personFields.put("name", true);
        personFields.put("passport-id", true);
        personFields.put("eye-color", true);
        personFields.put("hair-color", true);
        personFields.put("location-x", false);
        personFields.put("location-y", false);
        personFields.put("location-z", false);
        personFields.put("location-name", true);

        locationFields.put("id", false);
        locationFields.put("x", false);
        locationFields.put("y", false);
        locationFields.put("z", false);
        locationFields.put("name", true);
    }

    public static String getSqlLoadMovie(HashMap<String, String> headers) {
        // Добавить алиасы соответствующие полям из hashSeta
        StringBuilder sql = new StringBuilder("SELECT id, name, coordinates_x, coordinates_y, " +
                "creation_date, oscars_count, golden_palm_count, " +
                "total_box_office, mpaa_rating_value, screenwriter_name, " +
                "screenwriter_passport_id, screenwriter_hair_color, screenwriter_eye_color, " +
                "screenwriter_location_x, screenwriter_location_y, screenwriter_location_z, " +
                "screenwriter_location_name FROM (" +
                "SELECT m.*, c.x AS coordinates_x, c.y AS coordinates_y, mp.value AS mpaa_rating_value, " +
                "p.name AS screenwriter_name, p.passport_id as screenwriter_passport_id, ec.value AS screenwriter_eye_color, " +
                "hc.value AS screenwriter_hair_color, l.x AS screenwriter_location_x, " +
                "l.y AS screenwriter_location_y, l.z AS screenwriter_location_z, l.name AS screenwriter_location_name " +
                "FROM movie m JOIN coordinates c ON m.coordinates_id = c.id " +
                "JOIN mpaa_rating mp ON mpaa_rating_id = mp.id " +
                "JOIN person p ON p.id = m.person_id " +
                "JOIN color ec ON p.eyecolor_id = ec.id " +
                "JOIN color hc ON p.haircolor_id = hc.id " +
                "JOIN location l ON p.location_id = l.id" +
                ") WHERE 1=1 ");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();

            if (movieFields.containsKey(field)) {
                boolean isString = movieFields.get(field);
                if (isString) {
                    sql.append("AND ").append(field.replace("-", "_")).append(" = '").append(value).append("' ");
                } else {
                    sql.append("AND ").append(field.replace("-", "_")).append(" = ").append(value).append(" ");
                }
            }
        }

        sql.append(";");
        return sql.toString();
    }

    public static String getSqlLoadCoordinates(HashMap<String, String> headers) {
        StringBuilder sql = new StringBuilder("SELECT * FROM coordinates WHERE 1=1 ");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();

            if (coordinatesFields.contains(field)) {
                sql.append("AND ").append(field.replace("-", "_")).append(" = ").append(value).append(" ");
            }
        }

        sql.append(";");
        return sql.toString();
    }

    public static String getSqlLoadPerson(HashMap<String, String> headers) {
        StringBuilder sql = new StringBuilder("SELECT id, name, passport_id, eye_color, hair_color, " +
                "location_x, location_y, location_z, location_name FROM (" +
                "SELECT p.*, ec.value AS eye_color, hc.value AS hair_color, " +
                "l.x AS location_x, l.y AS location_y, l.z AS location_z, " +
                "l.name AS location_name FROM person p " +
                "JOIN color ec ON eyecolor_id = ec.id " +
                "JOIN color hc ON haircolor_id = hc.id " +
                "JOIN location l ON p.location_id = l.id) " +
                "WHERE 1=1 ");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();

            if (personFields.containsKey(field)) {
                boolean isString = personFields.get(field);
                if (isString) {
                    sql.append("AND ").append(field.replace("-", "_")).append(" = '").append(value).append("' ");
                } else {
                    sql.append("AND ").append(field.replace("-", "_")).append(" = ").append(value).append(" ");
                }
            }
        }

        sql.append(";");
        return sql.toString();
    }

    public static String getSqlLoadLocation(HashMap<String, String> headers) {
        StringBuilder sql = new StringBuilder("SELECT * FROM location WHERE 1=1 ");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();

            if (locationFields.containsKey(field)) {
                boolean isString = locationFields.get(field);
                if (isString) {
                    sql.append("AND ").append(field.replace("-", "_")).append(" = '").append(value).append("' ");
                } else {
                    sql.append("AND ").append(field.replace("-", "_")).append(" = ").append(value).append(" ");
                }
            }
        }

        sql.append(";");
        return sql.toString();
    }
}