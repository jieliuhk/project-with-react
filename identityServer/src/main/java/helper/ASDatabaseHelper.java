package helper;

import com.google.gson.JsonObject;
import dao.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Pattern;

public class ASDatabaseHelper {

    private Connection dbConnection;
    private final static Logger log = LogManager.getRootLogger();

    /** Used to insert a new user into the database with credential. */
    private static final String REGISTER_SQL =
            "INSERT INTO users (idusers, username, passkey, salt, userkey) " +
                    "VALUES (uuid(), ?, ?, ?, ?);";

    /** Used to determine if a username already exists. */
    private static final String USER_SQL =
            "SELECT username FROM users WHERE username = ?";

    /** Used to retrieve the user id associated with a specific user. */
    private static final String USERID_SQL =
            "SELECT idusers FROM users WHERE username = ?";

    /** Used to retrieve the user key associated with a specific user. */
    private static final String USERKEY_SQL =
            "SELECT userkey FROM users WHERE username = ?";

    /** Used to retrieve the salt associated with a specific user. */
    private static final String SALT_SQL =
            "SELECT salt FROM users WHERE username = ?";

    /** Used to retrieve the TGS key associated with a specific TG server. */
    private static final String TGSKEY_SQL =
            "SELECT tgskey FROM tgs WHERE idtgs = ?";

    /** Used to authenticate a user. */
    private static final String AUTH_SQL =
            "SELECT username FROM users " +
                    "WHERE username = ? AND passkey = ?";


    /** Used to generate password hash salt for user. */
    private static final Random random = new Random();

    public ASDatabaseHelper(String uri, String user, String password) {
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
     * Registers a new user, placing the username, password hash, and
     * salt into the database if the username does not already exist.
     *
     * @param newuser - username of new user
     * @param newpass - password of new user
     * @return status.ok if registration successful
     */
    public Status registerUser(String newuser, String newpass) {
        Status status = Status.ERROR;
        try {
            log.debug("Registering " + newuser + ".");

            // make sure we have non-null and non-emtpy values for login
            if (isBlank(newuser) || isBlank(newpass)) {
                status = Status.INVALID_LOGIN;
                log.debug(status);
                return status;
            }

            status = duplicateUser(newuser);

            // if okay so far, try to insert new user
            if (status == Status.OK)  {
                if(isValidPassword(newpass)) {
                    status = addUser(newuser, newpass);
                }
                else {
                    status = Status.INVALID_PASSWORD;
                }
            }

            return status;
        }
        catch (Exception e) {
            return Status.ERROR;
        }
    }

    /**
     * Checks to see if a String is null or empty.
     * @param text - String to check
     * @return true if non-null and non-empty
     */
    private static boolean isBlank(String text) {
        return (text == null) || text.trim().isEmpty();
    }

    /**
     * Checks to see if a password is valid.
     * @param password - password to check
     * @return true if non-null and valid
     */
    private boolean isValidPassword(String password) {
        Pattern p = Pattern.compile("(?=.*\\d)(?=.*[A-Za-z])(?=.*[@#$%./]).{5,10}");
        return p.matcher(password).find();
    }

    /**
     * Tests if a user already exists in the database. Requires an active
     * database connection.
     *
     * @param user - username to check
     * @return Status.OK if user does not exist in database
     */
    private Status duplicateUser(String user) {
        assert user != null;

        Status status = Status.ERROR;

        try (
                PreparedStatement statement = dbConnection.prepareStatement(USER_SQL);
        ) {
            statement.setString(1, user);

            ResultSet results = statement.executeQuery();
            status = results.next() ? Status.DUPLICATE_USER : Status.OK;
            return status;
        }
        catch (Exception e) {
            log.debug(e.getMessage(), e);
            status = Status.SQL_EXCEPTION;
            return status;
        }
    }

    /**
     * Registers a new user, placing the username, password hash, and
     * salt into the database if the username does not already exist.
     *
     * @param newuser - username of new user
     * @param newpass - password of new user
     * @return status ok if registration successful
     */
    private Status addUser(String newuser, String newpass) {

        Status status = Status.ERROR;

        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);

        String userSalt = encodeHex(saltBytes, 32);
        String passHash = getHash(newpass, userSalt);
        String userKey = getHash(newpass, newuser);

        try (
                PreparedStatement statement = dbConnection.prepareStatement(REGISTER_SQL);
        ) {
            statement.setString(1, newuser);
            statement.setString(2, passHash);
            statement.setString(3, userSalt);
            statement.setString(4, userKey);
            statement.executeUpdate();

            status = Status.OK;
        }
        catch (SQLException ex) {
            status = Status.SQL_EXCEPTION;
            log.debug(ex.getMessage(), ex);
        }

        return status;
    }

    /**
     * Checks if the provided username and password match what is stored
     * in the database.
     *
     * @param username - username to authenticate
     * @param password - password to authenticate
     * @return status.ok if authentication successful
     */
    public Status authenticateUser(String username,
                                    String password) {

        Status status;

        try (
                PreparedStatement statement = dbConnection.prepareStatement(AUTH_SQL);
        ) {
            String usersalt = getSalt(username);
            String passhash = getHash(password, usersalt);

            statement.setString(1, username);
            statement.setString(2, passhash);

            ResultSet results = statement.executeQuery();
            status = results.next() ? status = Status.OK : Status.INVALID_LOGIN;
            return status;
        }
        catch (Exception e) {
            log.debug(e.getMessage(), e);
            status = Status.SQL_EXCEPTION;
            return status;
        }
    }

    /**
     * Gets the TGSSessionKey for a specific user.
     *
     * @param user - which user to retrieve TGSSessionKey for
     * @return TGS Session Key encrypted by user key
     */
    public String prepareTGSSessionKey(String user, String plainTextSessionKey) {
        String encryptedTAGSSessionKey = "";

        try {
            //TODO: protect session key use AES by userKey
            String userKey = getUserKey(user);
            encryptedTAGSSessionKey = plainTextSessionKey;
        }
        catch (SQLException e) {
            log.debug(e.getMessage(), e);
            encryptedTAGSSessionKey = "";
        }

        return  encryptedTAGSSessionKey;
    }

    /**
     * Gets the TGT for a specific user.
     *
     * @param user - which user to retrieve TGT for
     * @return TGT for the specified user or null if user does not exist
     */
    public String prepareTGT(String user, String TGSId, String tgsSessionKey) {
        String TGT = "";

        try {
            //TODO: protect TGT use AES by tgsKey
            String userId = getUserId(user);
            String tgsKey = getTGSKey(TGSId);
            int validTime = 10; //minus
            JsonObject res = new JsonObject();
            res.addProperty("userId", userId);
            res.addProperty("userName", user);
            res.addProperty("TGSSessionKey", tgsSessionKey);
            res.addProperty("expireTime", LocalDateTime.now().plusMinutes(validTime).toString());

            TGT = res.toString();
            return  TGT;
        }
        catch (Exception e) {
            log.debug(e.getMessage(), e);
            e.printStackTrace();
            TGT = "";
            return  TGT;
        }
    }

    /**
     * Generate one random Key.
     *
     * @param length - length of the key
     * @return random key with length o empty string if generate fail
     */
    public String generateRandomKey(int length) {
        byte[] randBytes = new byte[16];
        random.nextBytes(randBytes);
        return encodeHex(randBytes, length);
    }


    /**
     * Gets the salt for a specific user.
     *
     * @param user - which user to retrieve salt for
     * @return salt for the specified user or null if user does not exist
     * @throws SQLException if any issues with database connection
     */
    private String getSalt(String user) throws SQLException {
        assert user != null;

        String salt = null;

        try (
                PreparedStatement statement = dbConnection.prepareStatement(SALT_SQL);
        ) {
            statement.setString(1, user);

            ResultSet results = statement.executeQuery();

            if (results.next()) {
                salt = results.getString("salt");
            }
        }

        return salt;
    }

    /**
     * Gets the TGS secret key for a specific TGS.
     *
     * @param tgsId - which TGS to retrieve salt for
     * @return TGS secret key for the specified user or null if user does not exist
     * @throws SQLException if any issues with database connection
     */
    private String getTGSKey(String tgsId) throws SQLException {
        assert tgsId != null;

        String key = null;

        try (
                PreparedStatement statement = dbConnection.prepareStatement(TGSKEY_SQL);
        ) {
            statement.setString(1, tgsId);

            ResultSet results = statement.executeQuery();

            if (results.next()) {
                key = results.getString("tgskey");
            }
        }

        return key;
    }

    /**
     * Gets the User id for a specific User.
     *
     * @param user - which user to retrieve salt for
     * @return user id for the specified user or null if user does not exist
     * @throws SQLException if any issues with database connection
     */
    private String getUserId(String user) throws SQLException {
        assert user != null;

        String key = null;

        try (
                PreparedStatement statement = dbConnection.prepareStatement(USERID_SQL);
        ) {
            statement.setString(1, user);

            ResultSet results = statement.executeQuery();

            if (results.next()) {
                key = results.getString("idusers");
            }
        }

        return key;
    }

    /**
     * Gets the User secret key for a specific User.
     *
     * @param user - which user to retrieve salt for
     * @return user secret key for the specified user or null if user does not exist
     * @throws SQLException if any issues with database connection
     */
    private String getUserKey(String user) throws SQLException {
        assert user != null;

        String key = null;

        try (
                PreparedStatement statement = dbConnection.prepareStatement(USERKEY_SQL);
        ) {
            statement.setString(1, user);

            ResultSet results = statement.executeQuery();

            if (results.next()) {
                key = results.getString("userkey");
            }
            return key;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the hex encoding of a byte array.
     *
     * @param bytes - byte array to encode
     * @param length - desired length of encoding
     * @return hex encoded byte array
     */
    private static String encodeHex(byte[] bytes, int length) {
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);

        assert hex.length() == length;
        return hex;
    }

    /**
     * Calculates the hash of a password and salt using SHA-256.
     *
     * @param password - password to hash
     * @param salt - salt associated with user
     * @return hashed password
     */
    private String getHash(String password, String salt) {
        String salted = salt + password;
        String hashed = salted;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salted.getBytes());
            hashed = encodeHex(md.digest(), 64);
        }
        catch (Exception ex) {
            log.debug("Unable to properly hash password.", ex);
        }

        return hashed;
    }

}
