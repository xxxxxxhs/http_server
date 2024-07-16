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
    public void addMovie(Movie movie, String username) throws SQLException {
        int coordinatesId = addCoordinates(movie.getCoordinates());
        int screenwriterId = addPerson(movie.getScreenWriter());
        String sql = "INSERT INTO movie (name, coordinates_id, oscars_count, golden_palm_count, total_box_office, mpaa_rating_id, person_id, creator) VALUES (?, ?, ?, ?, ?, (SELECT id FROM mpaa_rating WHERE value = ?), ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, movie.getName());
            pstmt.setInt(2, coordinatesId);
            pstmt.setInt(3, movie.getOscarsCount());
            pstmt.setLong(4, movie.getGoldenPalmCount());
            pstmt.setFloat(5, movie.getTotalBoxOffice());
            pstmt.setString(6, movie.getMpaaRating().name()); // Предполагается, что перечисление уже в БД
            pstmt.setInt(7, screenwriterId);
            pstmt.executeUpdate();
        }
    }
    private int addPerson(Person person) throws SQLException {
        // Сначала добавляем location, получаем её ID
        int locationId = addLocation(person.getLocation());

        String sql = "INSERT INTO person (name, passport_id, eyecolor_id, haircolor_id, location_id) VALUES (?, ?, (SELECT id FROM color WHERE value = ?), (SELECT id FROM color WHERE value = ?), ?) RETURNING id";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, person.getName());
            pstmt.setString(2, person.getPassportID());
            pstmt.setString(3, person.getEyeColor().name()); // Передача значения enum как строки
            pstmt.setString(4, person.getHairColor().name());
            pstmt.setInt(5, locationId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    private int addCoordinates(Coordinates coordinates) throws SQLException {
        String sql = "INSERT INTO coordinates (x, y) VALUES (?, ?) RETURNING id";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setFloat(1, coordinates.getX());
            pstmt.setDouble(2, coordinates.getY());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1; // Или выбросить исключение
    }
    private int addLocation(Location location) throws SQLException {
        String sql = "INSERT INTO location (x, y, z, name) VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, location.getX());
            pstmt.setFloat(2, location.getY());
            pstmt.setLong(3, location.getZ());
            pstmt.setString(4, location.getName());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1; // Или выбросить исключение
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
        } catch (IncorrectValueException e) {
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
                addCoordinates(movie.getCoordinates());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                addLocation(movie.getScreenWriter().getLocation());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                addPerson(movie.getScreenWriter());
            } catch (SQLException e) {e.printStackTrace();}
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
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
}
