package helper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class DatabaseHelper {

    /** find all  cities*/
    private static final String FIND_CITIES =
            "SELECT city FROM hotels GROUP BY city";

    /** find all hotels */
    private static final String FIND_HOTELS =
            "SELECT * FROM hotels\n" +
                    "LEFT JOIN (SELECT idhotel, avg(reviews.rating) as averageRating from reviews group by idhotel) tmp\n" +
                    "on tmp.idhotel=hotels.id";

    /** find all hotels by cities */
    private static final String FIND_HOTEL_BY_CITY_SQL =
            "SELECT * FROM hotels\n" +
                    "LEFT JOIN (SELECT idhotel, avg(reviews.rating) as averageRating from reviews group by idhotel) tmp\n" +
                    "on tmp.idhotel=hotels.id WHERE city=?";

    /** find all hotels by keyword */
    private static final String FIND_HOTEL_BY_KEYWORD =
            "SELECT * FROM hotels\n" +
                    "LEFT JOIN (SELECT idhotel, avg(reviews.rating) as averageRating from reviews group by idhotel) tmp\n" +
                    "on tmp.idhotel=hotels.id WHERE name LIKE ?;";

    /** find all hotels by cities and keyword */
    private static final String FIND_HOTEL_Y_CITY_AND_KEYWORD =
            "SELECT * FROM hotels\n" +
                    "LEFT JOIN (SELECT idhotel, avg(reviews.rating) as averageRating from reviews group by idhotel) tmp\n" +
                    "on tmp.idhotel=hotels.id WHERE city=? AND name LIKE ?";

    /**FIND_HOTEL_BY_ID. */
    private static final String FIND_HOTEL_BY_ID =
            "SELECT * FROM hotels WHERE id=?";

    /** FIND_REVIEWS_BY_USER */
    private static final String FIND_REVIEWS_BY_USER =
            "SELECT * FROM reviews WHERE iduser=?";

    /**FIND_REVIEWS_BY_ID */
    private static final String FIND_REVIEWS_BY_ID =
            "SELECT * FROM reviews WHERE id=?";

    /** UFIND_REVIEWS_BY_HOTEL */
    private static final String FIND_REVIEWS_BY_HOTEL =
            "SELECT * FROM reviews WHERE idhotel=? ORDER BY posttime DESC";


    /** DELETE_REVIEW by ID */
    private static final String DELETE_REVIEW =
            "DELETE FROM reviews WHERE (id = ?);";

    /** FIND_USER by user id */
    private static final String FIND_USER =
            "SELECT * FROM users WHERE idusers=?";

    /** find service provider's key */
    private static final String SPKEY_SQL =
            "SELECT `value` FROM security WHERE `key`=?";

    /** add a user to db */
    private static final String ADD_USER =
            "INSERT INTO users (idusers, name) VALUES (?,?);";

    /** Used to add a new user. */
    private static final String SAVE_HOTEL =
            "INSERT INTO saved_hotels (hotelid, userid) VALUES (?,?);";

    /** find user saved hotels */
    private static final String FIND_SAVED_HOTEL =
            "SELECT * FROM saved_hotels " +
                    "INNER JOIN hotels ON saved_hotels.hotelid=hotels.id " +
                    "WHERE userid=?";

    /**delete user saved hotels */
    private static final String DELETE_SAVED_HOTEL =
            "DELETE FROM saved_hotels WHERE hotelid=? AND userid=?;";

    /** delete all user saved hotels  */
    private static final String DELETE_SAVED_HOTELS =
            "DELETE FROM saved_hotels WHERE userid=?;";



    private Connection dbConnection;
    private final static Logger log = LogManager.getRootLogger();

    public DatabaseHelper(String uri, String user, String password) {
        try {
            Properties login = new Properties();
            login.put("user", user);
            login.put("password", password);
            dbConnection = DriverManager.getConnection(uri, login);
        } catch (SQLException e) {
            log.error("db connection error: \n" + e);
        }
    }

    /**
     * Get all hotels from database
     * @return json Array of the hotels, {"hotels":[]}
     */
    public JsonArray getHotels() {
        try {
            PreparedStatement sql;
            sql = dbConnection.prepareStatement(FIND_HOTELS);
            ResultSet results = sql.executeQuery();
            // check the number of columns
            ResultSetMetaData rsmd = results.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            JsonArray hotels = new JsonArray();
            while (results.next()) {
                hotels.add(getJsonObject(results, rsmd, columnsNumber));
            }
            return hotels;
        } catch (Exception e) {
            log.error("Unable to process db operation");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all cities from database
     * @return json Array of the hotels, {"hotels":[]}
     */
    public JsonArray getCities() {
        try {
            PreparedStatement sql;
            sql = dbConnection.prepareStatement(FIND_CITIES);
            ResultSet results = sql.executeQuery();
            // check the number of columns
            ResultSetMetaData rsmd = results.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            JsonArray cities = new JsonArray();
            while (results.next()) {
                cities.add(getJsonObject(results, rsmd, columnsNumber));
            }
            return cities;
        } catch (Exception e) {
            log.error("Unable to process db operation");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all hotels from database by city
     * @return json Arrar of the hotels, {"hotels":[]}
     */
    public JsonArray getHotelsByCity(String city) {
        try {
            PreparedStatement sql;
            sql = dbConnection.prepareStatement(FIND_HOTEL_BY_CITY_SQL);
            sql.setString(1, city);
            ResultSet results = sql.executeQuery();

            // check the number of columns
            ResultSetMetaData rsmd = results.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            JsonArray hotels = new JsonArray();
            while (results.next()) {
                hotels.add(getJsonObject(results, rsmd, columnsNumber));
            }
            return hotels;
        } catch (Exception e) {
            log.error("Unable to process db operation");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all hotels from database by keyword
     * @return json Array of the hotels, {"hotels":[]}
     */
    public JsonArray getHotelsByKeyword(String keyword) {
        try {
            PreparedStatement sql;
            sql = dbConnection.prepareStatement(FIND_HOTEL_BY_KEYWORD);
            sql.setString(1, "%" + keyword + "%");
            ResultSet results = sql.executeQuery();

            // check the number of columns
            ResultSetMetaData rsmd = results.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            JsonArray hotels = new JsonArray();
            while (results.next()) {
                hotels.add(getJsonObject(results, rsmd, columnsNumber));
            }
            return hotels;
        } catch (Exception e) {
            log.error("Unable to process db operation");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all hotels from database by keyword and city
     * @return json Array of the hotels, []
     */
    public JsonArray getHotelsByCityAndKeyword(String city, String keyword) {
        try {
            PreparedStatement sql;
            sql = dbConnection.prepareStatement(FIND_HOTEL_Y_CITY_AND_KEYWORD);
            sql.setString(1, city);
            sql.setString(2, "%" + keyword + "%");
            ResultSet results = sql.executeQuery();

            // check the number of columns
            ResultSetMetaData rsmd = results.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            JsonArray hotels = new JsonArray();
            while (results.next()) {
                hotels.add(getJsonObject(results, rsmd, columnsNumber));
            }
            return hotels;
        } catch (Exception e) {
            log.error("Unable to process db operation");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get a hotels from database by id
     * @return json string of the hotel
     */
    public JsonObject getHotel(int id) {
        try {
            PreparedStatement sql;
            sql = dbConnection.prepareStatement(FIND_HOTEL_BY_ID);
            sql.setInt(1, id);
            ResultSet results = sql.executeQuery();
            ResultSetMetaData rsmd = results.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            if (results.next()) {
                return getJsonObject(results, rsmd, columnsNumber);
            }
            else {
                return null;
            }
        } catch (Exception e) {
            log.error("Unable to process db operation");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Find reviews of the hotel
     * @return json string of the hotels, {"hotels":[]}
     */
    public JsonObject findReviews(int hotelId) {
        try {
            PreparedStatement sql;
            sql = dbConnection.prepareStatement(FIND_REVIEWS_BY_HOTEL);
            sql.setInt(1, hotelId);
            ResultSet results = sql.executeQuery();
            // check the number of columns
            ResultSetMetaData rsmd = results.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            JsonObject resJson = new JsonObject();
            JsonArray reviews = new JsonArray();
            while (results.next()) {
                reviews.add(getJsonObject(results, rsmd, columnsNumber));
            }
            resJson.addProperty("hotelId", hotelId);
            resJson.add("reviews", reviews);
            return resJson;
        } catch (Exception e) {
            log.error("Unable to process db operation" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all reviews from database of certain user
     * @param userId user id of the review owner
     * @return json string of the review, { }
     */
    public JsonObject findReviews(String userId) {
        try {
            PreparedStatement sql;
            sql = dbConnection.prepareStatement(FIND_REVIEWS_BY_USER);
            sql.setString(1, userId);
            ResultSet results = sql.executeQuery();

            // check the number of columns
            ResultSetMetaData rsmd = results.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            JsonArray reviews = new JsonArray();
            JsonObject res = new JsonObject();

            while (results.next()) {
                reviews.add(getJsonObject(results, rsmd, columnsNumber));
            }
            res.add("reviews", reviews);

            return res;
        } catch (Exception e) {
            log.error("Unable to process db operation" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get review by id
     * @return json string of the review, { }
     */
    public String findReview(String reviewId) {
        try {
            PreparedStatement sql;
            sql = dbConnection.prepareStatement(FIND_REVIEWS_BY_ID);
            sql.setString(1, reviewId);
            ResultSet results = sql.executeQuery();

            // check the number of columns
            ResultSetMetaData rsmd = results.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            JsonObject resJson;

            if (results.next()) {
                resJson = getJsonObject(results, rsmd, columnsNumber);
            }
            else {
                resJson = new JsonObject();
            }

            return resJson.toString();
        } catch (Exception e) {
            log.error("Unable to process db operation" + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Insert one review of the user
     * @param hotelId review for this hotel
     * @param user Json String of user information
     * @param review Json String of review information
     */
    public void addReview(int hotelId, JsonObject user, JsonObject review) {
        try {
            PreparedStatement sql = prepareStatementAddReviews(hotelId, user, review);
            System.out.println(sql);
            sql.executeUpdate();
        } catch (Exception e) {
            log.error("Unable to process db operation" );
            e.printStackTrace();
        }
    }

    private PreparedStatement prepareStatementAddReviews(int hotelId, JsonObject user, JsonObject review)
            throws Exception {
        PreparedStatement sql;
        LocalDateTime postTime = LocalDateTime.parse(review.get("posttime").getAsString(),
                DateTimeFormatter.ISO_DATE_TIME);

        sql = dbConnection.prepareStatement("INSERT INTO reviews " +
                "(id, idhotel, iduser, nameuser, title, text, rating, posttime) " +
                "VALUES (uuid(),?,?,?,?,?,?,?);");
        sql.setInt(1, hotelId);
        sql.setString(2, user.get("idusers").getAsString());
        sql.setString(3, user.get("name").getAsString());
        sql.setString(4, review.get("title").getAsString());
        sql.setString(5, review.get("text").getAsString());
        sql.setInt(6, review.get("rating").getAsInt());
        sql.setString(7, postTime.toString());
        return sql;
    }

    /**
     * Add a new user
     * @param userId uuid of user
     * @param userName user display name
     */
    public void addUser(String userId, String userName) {
        try {
            PreparedStatement sql;
            sql = dbConnection.prepareStatement(ADD_USER);
            sql.setString(1, userId);
            sql.setString(2, userName);
            System.out.println(sql);
            sql.executeUpdate();
        } catch (Exception e) {
            log.error("Unable to process db operation" );
            e.printStackTrace();
        }
    }

    /**
     * Save Hotel for user
     * @param userId uuid of user
     * @param hotelId hotel id
     */
    public void saveHotel(int hotelId, String userId) {
        try {
            PreparedStatement sql;
            sql = dbConnection.prepareStatement(SAVE_HOTEL);
            sql.setInt(1, hotelId);
            sql.setString(2, userId);
            System.out.println(sql);
            sql.executeUpdate();
        } catch (Exception e) {
            log.error("Unable to process db operation" );
            e.printStackTrace();
        }
    }

    /**
     * Delete one savedHotel of the user
     * @param userId hotel ID of the saved hotel to be delete
     */
    public JsonArray getSavedHotels(String userId) {
        try {
            PreparedStatement sql;
            sql = dbConnection.prepareStatement(FIND_SAVED_HOTEL);
            sql.setString(1, userId);
            System.out.println(sql);
            sql.executeQuery();

            ResultSet results = sql.executeQuery();
            // check the number of columns
            ResultSetMetaData rsmd = results.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            JsonArray hotels = new JsonArray();
            while (results.next()) {
                hotels.add(getJsonObject(results, rsmd, columnsNumber));
            }
            return hotels;
        } catch (Exception e) {
            log.error("Unable to process db operation" );
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Delete one savedHotel of the user
     * @param hotelId hotel ID of the saved hotel to be delete
     * @param userId hotel ID of the saved hotel to be delete
     */
    public void deleteSavedHotel(int hotelId, String userId) {
        try {
            PreparedStatement sql;
            sql = dbConnection.prepareStatement(DELETE_SAVED_HOTEL);
            sql.setInt(1, hotelId);
            sql.setString(2, userId);
            System.out.println(sql);
            sql.executeUpdate();
        } catch (Exception e) {
            log.error("Unable to process db operation" );
            e.printStackTrace();
        }
    }

    /**
     * Delete all savedHotels of the user
     * @param userId hotel ID of the saved hotel to be delete
     */
    public void deleteSavedHotels(String userId) {
        try {
            PreparedStatement sql;
            sql = dbConnection.prepareStatement(DELETE_SAVED_HOTELS);
            sql.setString(1, userId);
            System.out.println(sql);
            sql.executeUpdate();
        } catch (Exception e) {
            log.error("Unable to process db operation" );
            e.printStackTrace();
        }
    }

    /**
     * Insert one review of the user
     * @param review Json String of review information
     */
    public void updateReview(JsonObject review) {
        try {
            PreparedStatement sql = prepareStatementUpdateReviews(review);
            System.out.println(sql);
            sql.executeUpdate();
        } catch (Exception e) {
            log.error("Unable to process db operation" );
            e.printStackTrace();
        }
    }

    private PreparedStatement prepareStatementUpdateReviews(JsonObject review)
            throws Exception {
        PreparedStatement sql;
        LocalDateTime postTime = LocalDateTime.parse(review.get("posttime").getAsString(),
                DateTimeFormatter.ISO_DATE_TIME);

        sql = dbConnection.prepareStatement("UPDATE reviews " +
                "SET title = ?, text = ?, rating = ?, posttime = ?" +
                "WHERE (id = ?);");

        sql.setString(1, review.get("title").getAsString());
        sql.setString(2, review.get("text").getAsString());
        sql.setInt(3, review.get("rating").getAsInt());
        sql.setString(4, postTime.toString());
        sql.setString(5, review.get("id").getAsString());
        return sql;
    }

    /**
     * Delete one review of the user
     * @param reviewId reviewId of the review to be delete
     */
    public void deleteReview(String reviewId) {
        try {
            PreparedStatement sql;
            sql = dbConnection.prepareStatement(DELETE_REVIEW);
            sql.setString(1, reviewId);
            System.out.println(sql);
            sql.executeUpdate();
        } catch (Exception e) {
            log.error("Unable to process db operation" );
            e.printStackTrace();
        }
    }


    /**
     * Get user's information
     * @return json string of the user
     */
    public String findUserDetail(String userId) {
        try {
            PreparedStatement sql;
            sql = dbConnection.prepareStatement(FIND_USER);
            sql.setString(1, userId);
            ResultSet results = sql.executeQuery();
            // check the number of columns
            ResultSetMetaData rsmd = results.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            JsonObject resJson;
            if (results.next()) {
                resJson = getJsonObject(results, rsmd, columnsNumber);
            }
            else {
                resJson = new JsonObject();
            }

            return resJson.toString();
        } catch (Exception e) {
            log.error("Unable to process db operation" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * update user last login time
     */
    public void updateUserLoginTime(String userId, String newLoginTime) {
        try {
            PreparedStatement sql = prepareStatementUpdateUserLoginTime(userId, newLoginTime);
            System.out.println(sql);
            sql.executeUpdate();
        } catch (Exception e) {
            log.error("Unable to process db operation" );
            e.printStackTrace();
        }
    }

    private PreparedStatement prepareStatementUpdateUserLoginTime(String id, String newLoginTime) throws SQLException {
        PreparedStatement sql;
        sql = dbConnection.prepareStatement("UPDATE users SET lastlogin = ? WHERE (idusers = ?)");
        sql.setString(1, newLoginTime);
        sql.setString(2, id);
        return sql;
    }

    /**
     * Get service provider;s key from database
     *
     * @param keyName - service client required
     * @return service key if service exist null otherwise
     */
    public String getServiceKey(String keyName) {
        try (
                PreparedStatement statement = dbConnection.prepareStatement(SPKEY_SQL);
        ) {

            statement.setString(1, keyName);
            ResultSet results = statement.executeQuery();
            if(results.next()) {
                return results.getString("value");
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }

    private JsonObject getJsonObject(ResultSet results, ResultSetMetaData rsmd, int total) throws SQLException {
        JsonObject res = new JsonObject();
        for (int i = 0; i < total; i++) {
            String colName = rsmd.getColumnName(i + 1);
            String value = results.getString(i + 1) + "";
            res.addProperty(colName, value);
        }
        return res;
    }
}
