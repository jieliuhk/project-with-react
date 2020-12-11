package sevlet;

import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class UserInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession();
        PrintWriter out  = response.getWriter();

        if (isVerified(session)) {
            JsonObject res = new JsonObject();
            JsonObject userInfo = new JsonObject();
            userInfo.addProperty("name", (String) session.getAttribute("name"));
            userInfo.addProperty("lastLogin", session.getAttribute("lastLogin").toString());
            res.addProperty("success", true);
            res.add("userInfo", userInfo);
            out.println(res.toString());
        } else {
            out.println("{\"success\":false}");
        }
    }

    private boolean isVerified(HttpSession session) {
        return session != null && session.getAttribute("verified") != null
                && (Boolean) (session.getAttribute(("verified")))
                && session.getAttribute("userId") != null;
    }
}
