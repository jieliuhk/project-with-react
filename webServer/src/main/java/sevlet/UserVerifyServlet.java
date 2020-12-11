package sevlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import helper.DatabaseHelper;
import helper.IdentityVerifyHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class UserVerifyServlet extends HttpServlet {
    private DatabaseHelper db;

    private final static Logger log = LogManager.getRootLogger();

    public UserVerifyServlet() {
        String dburi = "jdbc:mysql://" + System.getenv("RDS_HOSTNAME") + "/webdb?serverTimezone=UTC";
        String dbuser = System.getenv("RDS_USERNAME");
        String dbpassword = System.getenv("RDS_PASSWORD");
        this.db = new DatabaseHelper(dburi, dbuser, dbpassword);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        HttpSession session = request.getSession();

        if (isVerified(session)) {
            PrintWriter out  = response.getWriter();
            out.println("{\"success\":true}");
        } else {
            handleNewSessionRequest(request, response);
        }
    }

    private void handleNewSessionRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.debug("in handleNewSessionRequest");
            String ticket = request.getParameter("ticket");
            String authenticator = request.getParameter("authenticator");
            String[] user = verifyUser(ticket, authenticator);

            if (user != null) {
                authorizeUser(request, response, user[0]);
            } else {
                PrintWriter out = response.getWriter();
                out.println("{\"success\":false}");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authorizeUser(HttpServletRequest request, HttpServletResponse response, String id) throws IOException {
        try {
            PrintWriter out = response.getWriter();
            responseWithCredential(request, out, id, db);
        } catch(Exception e) {
            PrintWriter out = response.getWriter();
            out.println("{\"success\":false}");
        }
    }

    private void responseWithCredential(HttpServletRequest request, PrintWriter out, String userId, DatabaseHelper db) {
        LocalDateTime now = LocalDateTime.now();
        HttpSession session = request.getSession();
        String userDetail = db.findUserDetail(userId);
        Gson gson = new Gson();
        JsonObject userInfo = gson.fromJson(userDetail, JsonObject.class);

        session.setAttribute("verified", true);
        session.setAttribute("userId", userId);
        session.setAttribute("name", userInfo.get("name").getAsString());
        session.setAttribute("lastLogin", userInfo.get("lastlogin").getAsString());
        log.debug("current Session:");
        log.debug(session.getCreationTime());
        db.updateUserLoginTime(userId, now.toString());

        out.println("{\"success\":true}");
    }

    private String[] verifyUser(String ticket, String authenticator) {
        log.debug("in verifyUser");
        log.debug(ticket);
        log.debug(authenticator);
        IdentityVerifyHelper idv = new IdentityVerifyHelper();
        return idv.checkAndGetUserId(ticket, authenticator);
    }

    private boolean isVerified(HttpSession session) {
        return session != null && session.getAttribute("verified") != null
                && (Boolean) (session.getAttribute(("verified")))
                && session.getAttribute("userId") != null;
    }

    /**
     * Checks to see if a String is null or empty.
     * @param text - String to check
     * @return true if non-null and non-empty
     */
    private static boolean isBlank(String text) {
        return (text == null) || text.trim().isEmpty();
    }
}
