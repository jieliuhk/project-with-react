package helper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IdentityVerifyHelper {
    DatabaseHelper db;
    private final static Logger log = LogManager.getRootLogger();

    public IdentityVerifyHelper() {
        String dburi = "jdbc:mysql://" + System.getenv("RDS_HOSTNAME") + "/webdb?serverTimezone=UTC";
        String dbuser = System.getenv("RDS_USERNAME");
        String dbpassword = System.getenv("RDS_PASSWORD");
        this.db = new DatabaseHelper(dburi, dbuser, dbpassword);
    }

    /**Check uf user is valid
     * @return String[0] -> user id, String[1] -> user name
     * */
    public String[] checkAndGetUserId(String ticket, String authenticator) {
        try {
            log.debug("in checkAndGetUserId");
            JsonObject plaintextTicket = decodeTicket(ticket);
            JsonObject plaintextAuthenticator = decodeAuthenticator(authenticator,
                    plaintextTicket.get("SSSessionKey").getAsString());

            String authenticatorName = plaintextAuthenticator.get("userName").getAsString();
            String ticketName = plaintextTicket.get("userName").getAsString();
            String ticketId = plaintextTicket.get("userId").getAsString();

            LocalDateTime expireTime = LocalDateTime.parse(plaintextTicket.get("expireTime").getAsString(),
                    DateTimeFormatter.ISO_DATE_TIME);

            if( authenticatorName.equals(ticketName) && expireTime.isAfter(LocalDateTime.now())) {
                return new String[] {ticketId, ticketName};
            }
            else {
                return null;
            }
        } catch (Exception e) {
            log.error("decode fail");
            e.printStackTrace();
            return null;
        }
    }

    private JsonObject decodeTicket(String ticket) {
        String spkey = db.getServiceKey("spkey");
        //TODO: decode authenticator use ADE by spkey
        Gson gson = new Gson();
        JsonObject plaintext = gson.fromJson(ticket, JsonObject.class);
        return plaintext;
    }

    private JsonObject decodeAuthenticator(String authenticator, String SSSessionKey) {
        //TODO: decode authenticator use ADE by SSSessionKey
        Gson gson = new Gson();
        JsonObject plaintext = gson.fromJson(authenticator, JsonObject.class);
        return plaintext;
    }
}
