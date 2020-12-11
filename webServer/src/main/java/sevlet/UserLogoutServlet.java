package sevlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class UserLogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        if (isLogout(session)) {
            out.println("{\"success\":true}");
        } else {
            handleLogout(request, response);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();
            PrintWriter out = response.getWriter();
            session.invalidate();
            out.println("{\"success\":true}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isLogout(HttpSession session) {
        return session == null;
    }
}
