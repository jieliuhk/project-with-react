package sevlet.as;

import dao.Status;
import helper.ASDatabaseHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class RegisterServlet extends HttpServlet {
    private ASDatabaseHelper db;
    private String TGSId = "default";

    public RegisterServlet() {
        super();
        String dburi = "jdbc:mysql://" + System.getenv("RDS_HOSTNAME") + "/iddb?serverTimezone=UTC";
        String dbuser = System.getenv("RDS_USERNAME");
        String dbpassword = System.getenv("RDS_PASSWORD");
        this.db = new ASDatabaseHelper(dburi, dbuser, dbpassword);
        if(System.getenv("TGS_ID") != null) {
            TGSId = System.getenv("TGS_ID");
        }
    }

    /**User Registration
     *
     * */
    @Override
    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out  = response.getWriter();
        String userName = request.getParameter("name");
        String userKey = request.getParameter("password");
        if(db.registerUser(userName, userKey) == Status.OK) {
            TGTServlet.prepareResponse(out, userName, db, TGSId);
        }
        else {
            out.println("{\"success\":false}");
        }
    }

}
