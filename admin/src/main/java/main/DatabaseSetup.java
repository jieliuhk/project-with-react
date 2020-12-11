package main;

import appdata.threadsafe.ThreadSafeHotelData;
import builder.HotelDataBuilder;
import dataclass.Hotel;
import dataclass.Review;
import helper.ArgumentHelper;

import java.math.BigInteger;
import java.nio.file.Paths;
import java.sql.*;
import java.time.ZoneId;
import java.util.Properties;
import java.util.Random;

public class DatabaseSetup {

    private static final String TGSId = "default";

    private static final String CREATE_HOTELS_TABLE = "CREATE TABLE `webdb`.`hotels` (\n" +
            "  `id` INT NOT NULL,\n" +
            "  `name` TEXT NULL,\n" +
            "  `address` TEXT NULL,\n" +
            "  `city` VARCHAR(60) NULL,\n" +
            "  `state` VARCHAR(60) NULL,\n" +
            "  `latitude` DOUBLE NULL,\n" +
            "  `longitude` DOUBLE NULL,\n" +
            "        PRIMARY KEY (`id`));";

    private static final String CREATE_REVIEWS_TABLE = "CREATE TABLE `webdb`.`reviews` (\n" +
            "  `id` VARCHAR(45) NOT NULL,\n" +
            "  `idhotel` INT NULL,\n" +
            "  `iduser` VARCHAR(45) NULL,\n" +
            "  `nameuser` TEXT NULL,\n" +
            "  `title` TEXT NULL,\n" +
            "  `text` TEXT NULL,\n" +
            "  `rating` INT NULL,\n" +
            "  `posttime` DATETIME NULL,\n" +
            "  PRIMARY KEY (`id`));";

    private static final String CREATE_USERS_TABLE = "CREATE TABLE `webdb`.`users` (\n" +
            "  `idusers` VARCHAR(45) NOT NULL,\n" +
            "  `name` TEXT NULL,\n" +
            "  `lastlogin` DATETIME NULL,\n" +
            "  PRIMARY KEY (`iduser`));";

    private static final String CREATE_SAVED_HOTELS_TABLE = "CREATE TABLE `webdb`.`saved_hotels` (\n" +
            "  `hotelid` VARCHAR(45) NOT NULL,\n" +
            "  `userid` TEXT NULL);";

    private static final String CREATE_SECURITY_TABLE = "CREATE TABLE `webdb`.`security` (\n" +
            "  `key` VARCHAR(45) NOT NULL,\n" +
            "  `value` VARCHAR(64) NULL,\n" +
            "  PRIMARY KEY (`key`));\n";

    private static final String CREATE_ID_USERS_TABLE = "CREATE TABLE `iddb`.`users` (\n" +
            "  `idusers` VARCHAR(45) NOT NULL,\n" +
            "  `username` TEXT NOT NULL,\n" +
            "  `passkey` VARCHAR(64) NOT NULL,\n" +
            "  `salt` VARCHAR(64) NOT NULL,\n" +
            "  `userkey` VARCHAR(64) NOT NULL,\n" +
            "  PRIMARY KEY (`idusers`));";

    private static final String CRATE_ID_TGS_TABLE = "CREATE TABLE `iddb`.`tgs` (\n" +
            "  `idtgs` VARCHAR(45) NOT NULL,\n" +
            "  `tgskey` VARCHAR(64) NOT NULL,\n" +
            "  PRIMARY KEY (`idtgs`));";

    private static final String CRATE_ID_KDC_TABLE = "CREATE TABLE `iddb`.`kdc` (\n" +
            "  `servicename` VARCHAR(45) NOT NULL,\n" +
            "  `spkey` VARCHAR(64) NOT NULL,\n" +
            "  PRIMARY KEY (`servicename`));";

    private static final String ADD_TGS_KEY = "INSERT INTO tgs (idtgs, tgskey)" +
            " VALUES (?, ?);";

    private static final String ADD_SP_KEY_ID = "INSERT INTO kdc (servicename, spkey)" +
            " VALUES (?, ?);";

    private static final String ADD_SP_KEY_WEB = "INSERT INTO security (`key`, `value`)" +
            " VALUES (?, ?);";

    private static final ThreadSafeHotelData hdata = new ThreadSafeHotelData();


    public static void main(String[] args) {

        HotelDataBuilder builder = new HotelDataBuilder(hdata);
        ArgumentHelper argumentHelper = new ArgumentHelper(args);
        builder.loadHotelInfo(argumentHelper.hotelPath());
        builder.loadReviews(Paths.get(argumentHelper.reviewsPath()));
        builder.shutDown();

        //createTablesID();
        //setupTGS();
        //setupKDC();

        createTablesWeb();
        //setupHotels();
        //setupReviews();
    }

    private static void setupTGS() {
        Random random = new Random();
        byte[] saltBytes = new byte[32];
        random.nextBytes(saltBytes);
        String tgskey = encodeHex(saltBytes, 32);

        String dburi = "jdbc:mysql://" + System.getenv("RDS_ID_HOSTNAME") + "/iddb?serverTimezone=UTC";
        String dbuser = System.getenv("RDS_USERNAME");
        String dbpassword = System.getenv("RDS_PASSWORD");

        Properties login = new Properties();
        login.put("user", dbuser);
        login.put("password", dbpassword);
        try (Connection dbConnection = DriverManager.getConnection(dburi, login);) {
            PreparedStatement stmt = dbConnection.prepareStatement(ADD_TGS_KEY);
            stmt.setString(1, TGSId);
            stmt.setString(2, tgskey);
            stmt.executeUpdate();
        }
        catch(Exception e) {
            System.err.println("Unable to connect properly to database.");
            System.err.println(e.getMessage());
        }
    }

    private static void setupKDC() {
        Random random = new Random();
        byte[] saltBytes = new byte[32];
        random.nextBytes(saltBytes);
        String spkey = encodeHex(saltBytes, 32);

        String dburi = "jdbc:mysql://" + System.getenv("RDS_ID_HOSTNAME") + "/iddb?serverTimezone=UTC";
        String dbuser = System.getenv("RDS_USERNAME");
        String dbpassword = System.getenv("RDS_PASSWORD");

        Properties login = new Properties();
        login.put("user", dbuser);
        login.put("password", dbpassword);
        try (Connection dbConnection = DriverManager.getConnection(dburi, login);) {
            PreparedStatement stmt = dbConnection.prepareStatement(ADD_SP_KEY_ID);
            stmt.setString(1, "Travel Helper");
            stmt.setString(2, spkey);
            stmt.executeUpdate();
        }
        catch(Exception e) {
            System.err.println("Unable to connect properly to database.");
            System.err.println(e.getMessage());
        }

        dburi = "jdbc:mysql://" + System.getenv("RDS_HOSTNAME") + "/webdb?serverTimezone=UTC";
        login = new Properties();
        login.put("user", dbuser);
        login.put("password", dbpassword);
        try (Connection dbConnection = DriverManager.getConnection(dburi, login);) {
            PreparedStatement stmt = dbConnection.prepareStatement(ADD_SP_KEY_WEB);
            stmt.setString(1, "spkey");
            stmt.setString(2, spkey);
            stmt.executeUpdate();
        }
        catch(Exception e) {
            System.err.println("Unable to connect properly to database.");
            System.err.println(e.getMessage());
        }
    }

    private static void createTablesID() {
        String dburi = "jdbc:mysql://" + System.getenv("RDS_ID_HOSTNAME") + "/iddb?serverTimezone=UTC";
        String dbuser = System.getenv("RDS_USERNAME");
        String dbpassword = System.getenv("RDS_PASSWORD");

        Properties login = new Properties();
        login.put("user", dbuser);
        login.put("password", dbpassword);
        try (Connection dbConnection = DriverManager.getConnection(dburi, login);) {
            String[] sqls = { CREATE_ID_USERS_TABLE, CRATE_ID_TGS_TABLE, CRATE_ID_KDC_TABLE };
            for(String sql : sqls) {
                PreparedStatement stmt = dbConnection.prepareStatement(sql);
                stmt.executeUpdate();
            }
        }
        catch(Exception e) {
            System.err.println("Unable to connect properly to database.");
            System.err.println(e.getMessage());
        }
    }

    private static void createTablesWeb() {
        String dburi = "jdbc:mysql://" + System.getenv("RDS_HOSTNAME") + "/webdb?serverTimezone=UTC";
        String dbuser = System.getenv("RDS_USERNAME");
        String dbpassword = System.getenv("RDS_PASSWORD");

        Properties login = new Properties();
        login.put("user", dbuser);
        login.put("password", dbpassword);
        try (Connection dbConnection = DriverManager.getConnection(dburi, login);) {
            String[] sqls = {CREATE_SAVED_HOTELS_TABLE, CREATE_HOTELS_TABLE, CREATE_REVIEWS_TABLE,
                    CREATE_USERS_TABLE, CREATE_SECURITY_TABLE };
            for(String sql : sqls) {
                PreparedStatement stmt = dbConnection.prepareStatement(sql);
                stmt.executeUpdate();
            }
        }
        catch(Exception e) {
            System.err.println("Unable to connect properly to database.");
            System.err.println(e.getMessage());
        }
    }

    private static void setupHotels() {
        try {
            // Create database URI in proper format
            String dburi = "jdbc:mysql://" + System.getenv("RDS_HOSTNAME")+ "/webdb?serverTimezone=UTC";
            String dbuser = System.getenv("RDS_USERNAME");
            String dbpassword = System.getenv("RDS_PASSWORD");

            Properties login = new Properties();
            login.put("user", dbuser);
            login.put("password", dbpassword);
            try (Connection dbConnection = DriverManager.getConnection(dburi, login);) {
                String insertSql = "INSERT INTO hotels VALUES (?, ?, ?, ?, ?, ?, ?);";
                CallableStatement stmt = dbConnection.prepareCall(insertSql);
                for(Hotel h : hdata.findHotels()){
                    System.out.println(h);
                    stmt.setInt(1, Integer.parseInt(h.getHotelId()));
                    stmt.setString(2, h.getName());
                    stmt.setString(3, h.getAddress());
                    stmt.setString(4, h.getCity());
                    stmt.setString(5, h.getState());
                    stmt.setDouble(6, h.getLatitude());
                    stmt.setDouble(7, h.getLongitude());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        } catch (Exception e) {
            System.err.println("Unable to connect properly to database.");
            System.err.println(e.getMessage());
        }
    }

    private static void setupReviews() {
        try {
            // Create database URI in proper format
            String dburi = "jdbc:mysql://" + System.getenv("RDS_HOSTNAME")+ "/webdb?serverTimezone=UTC";
            String dbuser = System.getenv("RDS_USERNAME");
            String dbpassword = System.getenv("RDS_PASSWORD");

            Properties login = new Properties();
            login.put("user", dbuser);
            login.put("password", dbpassword);
            try (Connection dbConnection = DriverManager.getConnection(dburi, login);) {
                String insertSql = "INSERT INTO reviews VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
                CallableStatement stmt = dbConnection.prepareCall(insertSql);
                for(Review r : hdata.findReviews()){
                    stmt.setString(1, r.getReviewId());
                    stmt.setInt(2, Integer.parseInt(r.getHotelId()));
                    stmt.setNull(3, Types.NULL); //no user id for user not login
                    stmt.setString(4, r.getNickname());
                    stmt.setString(5, r.getTitle());
                    stmt.setString(6, r.getText());
                    stmt.setInt(7, r.getAvgRating());
                    stmt.setObject(8, r.getPostTime().toInstant()
                            .atZone(ZoneId.of("America/Los_Angeles")).toLocalDate());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        } catch (Exception e) {
            System.err.println("Unable to connect properly to database.");
            System.err.println(e.getMessage());
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
}
