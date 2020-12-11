package sevlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import helper.DatabaseHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class HotelInfoServlet extends HttpServlet {
    private DatabaseHelper db;

    public HotelInfoServlet() {
        String dburi = "jdbc:mysql://" + System.getenv("RDS_HOSTNAME") + "/webdb?serverTimezone=UTC";
        String dbuser = System.getenv("RDS_USERNAME");
        String dbpassword = System.getenv("RDS_PASSWORD");
        this.db = new DatabaseHelper(dburi, dbuser, dbpassword);
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) {
        response.setContentType("application/json");
        HttpSession session = request.getSession();

        if (isVerified(session)) {
            handleNormalRequest(request, response);
        } else {
            handleUnauthorizedRequest(response);
        }
    }

    private void handleNormalRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            String city = request.getParameter("city");
            String keyword = request.getParameter("keyword");
            String hotelCities = request.getParameter("hotelCities");

            JsonArray resArray;
            String resName;
            if (hotelCities == null || hotelCities.equals("")) {
                resArray = getHotels(city, keyword);
                resName = "hotels";
            } else {
                resArray = getCities();
                resName = "cities";
            }

            if (resArray == null) {
                out.println("{\"success\":false}");
            } else {
                JsonObject res = new JsonObject();
                res.addProperty("success", true);
                res.add(resName, resArray);
                out.println(res);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleUnauthorizedRequest(HttpServletResponse response) {
        try {
            PrintWriter out = null;
            out = response.getWriter();
            out.println("{\"success\":false, \"error\": \"unauthorized\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isVerified(HttpSession session) {
        return session != null && session.getAttribute("verified") != null
                && (Boolean) (session.getAttribute(("verified")))
                && session.getAttribute("userId") != null;
    }

    private JsonArray getHotels(String city, String keyword) {
        if ((city == null || city.equals("")) && (keyword == null || keyword.equals(""))) {
            return db.getHotels();
        } else if (city == null || city.equals("")) {
            return db.getHotelsByKeyword(keyword);
        } else if (keyword == null || keyword.equals("")) {
            return db.getHotelsByCity(city);
        } else {
            return db.getHotelsByCityAndKeyword(city, keyword);
        }
    }

    private JsonArray getCities() {
        return db.getCities();
    }
}
