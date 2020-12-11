package helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Properties;

public class TGSDatabaseHelper {
    private Connection dbConnection;
    private final static Logger log = LogManager.getRootLogger();

    /** Used to authenticate a user. */
    private static final String SPKEY_SQL =
            "SELECT spkey FROM kdc WHERE servicename = ?";


    public TGSDatabaseHelper(String uri, String user, String password) {
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
     * Checks if the provided username and password match what is stored
     * in the database. Requires an active database connection.
     *
     * @param serverName - service client required
     * @return service key if service exist null otherwise
     */
    public String getServiceKey(String serverName) {
        try (
                PreparedStatement statement = dbConnection.prepareStatement(SPKEY_SQL);
        ) {

            statement.setString(1, serverName);
            ResultSet results = statement.executeQuery();
            if(results.next()) {
                return results.getString("spkey");
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
}
