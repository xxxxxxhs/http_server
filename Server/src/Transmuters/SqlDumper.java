package Transmuters;

import CollectionClasses.*;
import Exceptions.IncorrectValueException;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;

public class SqlDumper{
    private final Connection connection;
    public SqlDumper(String url, String user, String password) {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public int add(Movie movie) throws SQLException {
        int id = -1;
        int coordinatesId = add(movie.getCoordinates());
        int screenwriterId = add(movie.getScreenWriter());

        String checkSql = "SELECT id FROM movie WHERE name = ? AND coordinates_id = ? AND oscars_count = ? " +
                "AND golden_palm_count = ? AND total_box_office = ? " +
                "AND mpaa_rating_id = (SELECT id FROM mpaa_rating WHERE value = ?) AND person_id = ?";
        String insertSql = "INSERT INTO movie (name, coordinates_id, oscars_count, golden_palm_count, " +
                "total_box_office, mpaa_rating_id, person_id) " +
                "VALUES (?, ?, ?, ?, ?, (SELECT id FROM mpaa_rating WHERE value = ?), ?) RETURNING id";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setString(1, movie.getName());
            checkStmt.setInt(2, coordinatesId);
            checkStmt.setInt(3, movie.getOscarsCount());
            checkStmt.setLong(4, movie.getGoldenPalmCount());
            checkStmt.setFloat(5, movie.getTotalBoxOffice());
            checkStmt.setString(6, movie.getMpaaRating().name());
            checkStmt.setInt(7, screenwriterId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                id = rs.getInt("id");
            } else {
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setString(1, movie.getName());
                    insertStmt.setInt(2, coordinatesId);
                    insertStmt.setInt(3, movie.getOscarsCount());
                    insertStmt.setLong(4, movie.getGoldenPalmCount());
                    insertStmt.setFloat(5, movie.getTotalBoxOffice());
                    insertStmt.setString(6, movie.getMpaaRating().name());
                    insertStmt.setInt(7, screenwriterId);
                    ResultSet insertRs = insertStmt.executeQuery();
                    if (insertRs.next()) {
                        id = insertRs.getInt("id");
                    }
                }
            }
        }
        return id;
    }
    public int add(Person person) throws SQLException {
        // Добавление или получение id для location
        int locationId = add(person.getLocation());

        String checkSql = "SELECT id FROM person WHERE name = ? AND passport_id = ? AND " +
                "eyecolor_id = (SELECT id FROM color WHERE value = ?) " +
                "AND haircolor_id = (SELECT id FROM color WHERE value = ?) AND location_id = ?";
        String insertSql = "INSERT INTO person (name, passport_id, eyecolor_id, haircolor_id, location_id) " +
                "VALUES (?, ?, (SELECT id FROM color WHERE value = ?), " +
                "(SELECT id FROM color WHERE value = ?), ?) RETURNING id";
        int id = -1;

        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setString(1, person.getName());
            checkStmt.setString(2, person.getPassportId());
            checkStmt.setString(3, person.getEyeColor().name());
            checkStmt.setString(4, person.getHairColor().name());
            checkStmt.setInt(5, locationId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                id = rs.getInt("id");
            } else {
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setString(1, person.getName());
                    insertStmt.setString(2, person.getPassportId());
                    insertStmt.setString(3, person.getEyeColor().name());
                    insertStmt.setString(4, person.getHairColor().name());
                    insertStmt.setInt(5, locationId);
                    ResultSet insertRs = insertStmt.executeQuery();
                    if (insertRs.next()) {
                        id = insertRs.getInt("id");
                    }
                }
            }
        }

        return id;
    }

    public int add(Coordinates coordinates) throws SQLException {
        String checkSql = "SELECT id FROM coordinates WHERE x = ? AND y = ?";
        String insertSql = "INSERT INTO coordinates (x, y) VALUES (?, ?) RETURNING id";
        int id = -1;

        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            // Проверяем, существуют ли такие координаты
            checkStmt.setFloat(1, coordinates.getX());
            checkStmt.setDouble(2, coordinates.getY());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Если координаты существуют, возвращаем их id
                id = rs.getInt("id");
            } else {
                // Если координаты не существуют, добавляем их
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setFloat(1, coordinates.getX());
                    insertStmt.setDouble(2, coordinates.getY());
                    ResultSet insertRs = insertStmt.executeQuery();
                    if (insertRs.next()) {
                        id = insertRs.getInt("id");
                    }
                }
            }
        }

        return id;
    }
    public int add(Location location) throws SQLException {
        String checkSql = "SELECT id FROM location WHERE x = ? AND y = ? AND z = ? AND name = ?";
        String insertSql = "INSERT INTO location (x, y, z, name) VALUES (?, ?, ?, ?) RETURNING id";
        int id = -1;

        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            // Проверяем, существует ли запись с такими же координатами
            checkStmt.setDouble(1, location.getX());
            checkStmt.setFloat(2, location.getY());
            checkStmt.setLong(3, location.getZ());
            checkStmt.setString(4, location.getName());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Если координаты существуют, возвращаем их id
                id = rs.getInt("id");
            } else {
                // Если координаты не существуют, добавляем их
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setDouble(1, location.getX());
                    insertStmt.setFloat(2, location.getY());
                    insertStmt.setLong(3, location.getZ());
                    insertStmt.setString(4, location.getName());
                    ResultSet insertRs = insertStmt.executeQuery();
                    if (insertRs.next()) {
                        id = insertRs.getInt("id");
                    }
                }
            }
        }

        return id;
    }

    public LinkedList<Movie> loadMovie(HashMap<String, String> headers) {
        LinkedList<Movie> movies = new LinkedList<>();

        String sql = SqlLoadConstructor.getSqlLoadMovie(headers);

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Coordinates coordinates = new Coordinates(rs.getFloat("coordinates_x"),
                        rs.getDouble("coordinates_y"));
                Location location = new Location(rs.getDouble("screenwriter_location_x"),
                        rs.getFloat("screenwriter_location_y"),
                        rs.getLong("screenwriter_location_z"),
                        rs.getString("screenwriter_location_name"));
                Person screenwriter = new Person(
                        rs.getString("screenwriter_name"),
                        rs.getString("screenwriter_passport_id"),
                        Color.valueOf(rs.getString("screenwriter_eye_color").toUpperCase()), // Assuming the Color enum corresponds directly
                        Color.valueOf(rs.getString("screenwriter_hair_color").toUpperCase()),
                        location
                );
                Movie movie = new Movie(
                        rs.getString("name"), coordinates,
                        rs.getInt("oscars_count"), rs.getLong("golden_palm_count"),
                        rs.getFloat("total_box_office"), MpaaRating.valueOf(rs.getString("mpaa_rating_value")),
                        screenwriter);
                movie.setId(rs.getLong("id"));
                if (movie.validate()) movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IncorrectValueException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public LinkedList<Person> loadPerson(HashMap<String, String> headers) {
        LinkedList<Person> persons = new LinkedList<>();
        String sql = SqlLoadConstructor.getSqlLoadPerson(headers);

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();) {

            while (rs.next()) {
                Person person = new Person(rs.getString("name"), rs.getString("passport_id"),
                        Color.valueOf(rs.getString("eye_color").toUpperCase()),
                        Color.valueOf(rs.getString("hair_color").toUpperCase()),
                        new Location(rs.getDouble("location_x"), rs.getFloat("location_y"),
                                rs.getLong("location_z"), rs.getString("location_name")));

                if (person.validate()) persons.add(person);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IncorrectValueException e) {
            e.printStackTrace();
        }
        return persons;
    }

    public LinkedList<Coordinates> loadCoordinates(HashMap<String, String> headers) {
        LinkedList<Coordinates> coordinates = new LinkedList<>();
        String sql = SqlLoadConstructor.getSqlLoadCoordinates(headers);

        try (PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Coordinates coor = new Coordinates(rs.getFloat("x"), rs.getDouble("y"));
                if (coor.validate()) coordinates.add(coor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coordinates;
    }

    public LinkedList<Location> loadLocation(HashMap<String, String> headers) {
        LinkedList<Location> locations = new LinkedList<>();
        String sql = SqlLoadConstructor.getSqlLoadLocation(headers);

        try (PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Location location = new Location(rs.getDouble("x"), rs.getFloat("y"),
                        rs.getLong("z"), rs.getString("name"));
                if (location.validate()) locations.add(location);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IncorrectValueException e) {
            e.printStackTrace();
        }
        return locations;
    }

    public void update(Movie movie, long id) {
        String sql = "UPDATE movie SET " +
                "name = ?, coordinates_id = (SELECT id FROM coordinates WHERE x = ? and y = ?), " +
                "creation_date = now(), oscars_count = ?, golden_palm_count = ?, " +
                "total_box_office = ?, mpaa_rating_id = (SELECT id FROM mpaa_rating WHERE value = ?), " +
                "person_id = (SELECT id FROM person WHERE name = ?) " +
                "WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);) {
            pstmt.setString(1, movie.getName());
            pstmt.setFloat(2, movie.getCoordinates().getX());
            pstmt.setDouble(3, movie.getCoordinates().getY());
            pstmt.setInt(4, movie.getOscarsCount());
            pstmt.setLong(5, movie.getGoldenPalmCount());
            pstmt.setFloat(6, movie.getTotalBoxOffice());
            pstmt.setString(7, movie.getMpaaRating().name());
            pstmt.setString(8, movie.getScreenWriter().getName());
            pstmt.setLong(9, id);
            try {
                add(movie.getCoordinates());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                add(movie.getScreenWriter().getLocation());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                add(movie.getScreenWriter());
            } catch (SQLException e) {e.printStackTrace();}
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /*
    public void clear(String username) {
        String sql = "DELETE FROM movie WHERE creator = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {e.printStackTrace();}
    }
    public void removeById(long id) {
        String sql = "DELETE FROM movie WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);) {
            pstmt.setLong(1, id);
            pstmt.executeQuery();
        } catch (SQLException e) {e.printStackTrace();}
    }
    public void removeFirst() {
        String sql = "DELETE FROM movie WHERE id = (SELECT MIN(id) FROM movie);";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);) {
            pstmt.executeUpdate();
        } catch (SQLException e) {e.printStackTrace();}
    }
     */
}
