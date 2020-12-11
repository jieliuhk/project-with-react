package sevlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import helper.DatabaseHelper;
import helper.IdentityVerifyHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class UserRegisterServlet extends HttpServlet {

    private DatabaseHelper db;

    public UserRegisterServlet() {
        String dburi = "jdbc:mysql://" + System.getenv("RDS_HOSTNAME") + "/webdb?serverTimezone=UTC";
        String dbuser = System.getenv("RDS_USERNAME");
        String dbpassword = System.getenv("RDS_PASSWORD");
        this.db = new DatabaseHelper(dburi, dbuser, dbpassword);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response) {
        try {
            response.setContentType("application/json");
            String ticket = request.getParameter("ticket");
            String authenticator = request.getParameter("authenticator");
            String[] userInfo = verifyUser(ticket, authenticator);

            if (userInfo != null && userInfo[0] != null) {
                registerUser(request, response, userInfo);
            } else {
                PrintWriter out = response.getWriter();
                out.println("{\"success\":false}");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerUser(HttpServletRequest request, HttpServletResponse response, String[] user) throws IOException {
        try {
            PrintWriter out = response.getWriter();
            String userId = user[0];
            String userName = user[1];

            db.addUser(userId, userName);
            responseWithCredential(request, out, userId, db);
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
        db.updateUserLoginTime(userId, now.toString());

        out.println("{\"success\":true}");
    }

    private String[] verifyUser(String ticket, String authenticator) {
        IdentityVerifyHelper idv = new IdentityVerifyHelper();
        return idv.checkAndGetUserId(ticket, authenticator);
    }
}
