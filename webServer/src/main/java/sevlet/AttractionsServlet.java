package sevlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import helper.DatabaseHelper;
import helper.WebApiHelper;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class AttractionsServlet extends HttpServlet {

    private static String apiKey  = System.getenv("GOOGLE_KEY");
    private static 	String host  = "maps.googleapis.com";
    private static 	String path = "/maps/api/place/textsearch/json";
    private DatabaseHelper db;

    public AttractionsServlet() {
        String dburi = "jdbc:mysql://" + System.getenv("RDS_HOSTNAME")+ "/webdb?serverTimezone=UTC";
        String dbuser = System.getenv("RDS_USERNAME");
        String dbpassword = System.getenv("RDS_PASSWORD");
        this.db = new DatabaseHelper(dburi, dbuser, dbpassword);
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession();

        if(isVerified(session)) {
            handleNormalRequest(request, response);
        }
        else {
            handleUnauthorizedRequest(response);
        }
    }

    private void handleNormalRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            String hotelId = request.getParameter("hotelId");
            hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            Integer id = parseInt(hotelId);
            Double radius = parseDouble(request.getParameter("radius"));

            if(id == null || radius == null) {
                out.println("{\"success\":false}");
            }
            else {
                out.println(fetchAttractions(id, radius));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleUnauthorizedRequest(HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            out.println("{\"success\":false, \"error\": \"unauthorized\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String fetchAttractions(Integer id, Double radius) {
        WebApiHelper api = new WebApiHelper(host, path, apiKey);
        Gson gson = new Gson();
        JsonObject hotel = gson.fromJson(db.getHotel(id), JsonObject.class);
        String city = hotel.get("city").toString();
        double latitude = hotel.get("latitude").getAsDouble();
        double longitude = hotel.get("longitude").getAsDouble();
        return api.getGoogleRespond(city, latitude, longitude, radius);
    }

    private boolean isVerified(HttpSession session) {
        return session != null && session.getAttribute("verified") != null
                && (Boolean)(session.getAttribute(("verified")))
                && session.getAttribute("userId") != null;
    }

    private Double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInt(String s) {
        try{
            return Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
    }
}
