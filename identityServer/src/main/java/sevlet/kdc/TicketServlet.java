package sevlet.kdc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.Status;
import helper.ASDatabaseHelper;
import helper.TGSDatabaseHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class TicketServlet extends HttpServlet {
    private TGSDatabaseHelper db;
    private final static Logger log = LogManager.getRootLogger();

    public TicketServlet() {
        String dburi = "jdbc:mysql://" + System.getenv("RDS_HOSTNAME") + "/iddb?serverTimezone=UTC";
        String dbuser = System.getenv("RDS_USERNAME");
        String dbpassword = System.getenv("RDS_PASSWORD");
        this.db = new TGSDatabaseHelper(dburi, dbuser, dbpassword);
    }

    private static class ServiceAuthenCert {
        private String userId;
        private String userName;
        private String TGSSessionKey;
        private String serviceKey;

        ServiceAuthenCert(String userId, String username, String TGSSessionKey, String serviceKey) {
            this.userId = userId;
            this.userName = username;
            this.TGSSessionKey = TGSSessionKey;
            this.serviceKey = serviceKey;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out  = response.getWriter();
        String serviceName = request.getParameter("service");
        String authenticator = request.getParameter("authenticator");
        String TGT = request.getParameter("TGT");
        ServiceAuthenCert cert = verifyRequest(serviceName, authenticator, TGT);

        if(cert != null) {
            JsonObject res = new JsonObject();
            String ssSessionKey = generateRandomKey(32);
            res.addProperty("success", true);
            res.addProperty("SSSessionKey", prepareSSSessionKey(cert, ssSessionKey));
            res.addProperty("ticket", prepareTicket(cert, ssSessionKey));
            out.println(res.toString());
        }
        else {
            out.println("{\"success\":false}");
        }
    }


    /**
     *Check if request has valid
     *
     * @return SSSessionKey for the specified user or null if user does not exist
     */
    private ServiceAuthenCert verifyRequest(String serviceName, String authenticator, String TGT) {
        try {
            JsonObject plaintextTGT = decodeTGT(TGT);
            JsonObject plaintextAuthentication = decodeAuthenticator(authenticator,
                    plaintextTGT.get("TGSSessionKey").getAsString());

            String authenticatorName = plaintextAuthentication.get("userName").getAsString();
            String TGTName = plaintextTGT.get("userName").getAsString();
            String TGTId = plaintextTGT.get("userId").getAsString();
            LocalDateTime expireTime = LocalDateTime.parse(plaintextTGT.get("expireTime").getAsString(),
                    DateTimeFormatter.ISO_DATE_TIME);

            if( authenticatorName.equals(TGTName) && expireTime.isAfter(LocalDateTime.now())) {
                String spKey = db.getServiceKey(serviceName);
                String TGSSessionKey = plaintextTGT.get("TGSSessionKey").getAsString();
                return new ServiceAuthenCert(TGTId, authenticatorName, TGSSessionKey, spKey);
            }
            else {
                return null;
            }
        } catch (Exception e) {
            log.info("decode fail" + e);
            return null;
        }
    }

    /**
     * Gets the SSSessionKey for a specific user.
     *
     * @return SSSessionKey for the specified user or null if user does not exist
     */
    private JsonObject decodeTGT(String TGT) {
        //TODO: decode TGT use AES by TGS secret key
        Gson gson = new Gson();
        return gson.fromJson(TGT, JsonObject.class);
    }

    /**
     * Gets the SSSessionKey for a specific user.
     *
     * @return SSSessionKey for the specified user or null if user does not exist
     */
    private JsonObject decodeAuthenticator(String authenticator, String TGSSessionKey) {
        //TODO: decode TGT use AES by TGS session Key
        Gson gson = new Gson();
        return gson.fromJson(authenticator, JsonObject.class);
    }


    /**
     * Gets the SSSessionKey for a specific user.
     *
     * @return SSSessionKey for the specified user or null if user does not exist
     */
    private String prepareSSSessionKey(ServiceAuthenCert cert, String plaintextSessionKey) {
        String TGSSSessionKey = cert.TGSSessionKey;
        //TODO: protect session key use AES by TGSSessionKey
        return  plaintextSessionKey;
    }

    /**
     * Gets the Ticket for a specific service.
     *
     * @param cert - cert for service authentication
     * @return TGT for the specified user or null if user does not exist
     */
    private String prepareTicket(ServiceAuthenCert cert, String plaintextSessionKey) {
        String ticket = null;

        //TODO: protect TGT use AES by spKey
        String spKey = cert.serviceKey;
        if(spKey != null) {
            int validTime = 10; //minus
            JsonObject res = new JsonObject();
            res.addProperty("userId", cert.userId);
            res.addProperty("userName", cert.userName);
            res.addProperty("SSSessionKey", plaintextSessionKey);
            res.addProperty("expireTime", LocalDateTime.now().plusMinutes(validTime).toString());

            ticket = res.toString();
        }

        return  ticket;
    }

    /**
     * Generate a random key
     *
     * @return random key
     */
    private String generateRandomKey(int length) {
        Random random = new Random();
        byte[] randBytes = new byte[16];
        random.nextBytes(randBytes);
        return encodeHex(randBytes, length);
    }

    /**
     * Returns the hex encoding of a byte array.
     *
     * @param bytes - byte array to encode
     * @param length - desired length of encoding
     * @return hex encoded byte array
     */
    private String encodeHex(byte[] bytes, int length) {
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);

        assert hex.length() == length;
        return hex;
    }
}
