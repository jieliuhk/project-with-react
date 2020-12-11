package sevlet.as;

import com.google.gson.JsonObject;
import dao.Status;
import helper.ASDatabaseHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class TGTServlet extends HttpServlet {
    private ASDatabaseHelper db;
    private String TGSId = "default";

    public TGTServlet() {
        String dburi = "jdbc:mysql://" + System.getenv("RDS_HOSTNAME") + "/iddb?serverTimezone=UTC";
        String dbuser = System.getenv("RDS_USERNAME");
        String dbpassword = System.getenv("RDS_PASSWORD");
        this.db = new ASDatabaseHelper(dburi, dbuser, dbpassword);
        if(System.getenv("TGS_ID") != null) {
            TGSId = System.getenv("TGS_ID");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out  = response.getWriter();
        String userName = request.getParameter("name");
        String password = request.getParameter("password");

        if(db.authenticateUser(userName, password) == Status.OK) {
            prepareResponse(out, userName, db, TGSId);
        }
        else {
            out.println("{\"success\":false}");
        }
    }

    static void prepareResponse(PrintWriter out, String userName, ASDatabaseHelper db, String tgsId) {
        JsonObject res = new JsonObject();
        String tgsSessionKey = db.generateRandomKey(32);
        res.addProperty("success", true);
        res.addProperty("TGSSessionKey", db.prepareTGSSessionKey(userName, tgsSessionKey));
        res.addProperty("TGT", db.prepareTGT(userName, tgsId, tgsSessionKey));
        out.println(res.toString());
    }

}
