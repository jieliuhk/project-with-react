package sevlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import helper.DatabaseHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class SavedHotelServlet extends HttpServlet {
    private DatabaseHelper db;

    public SavedHotelServlet() {
        super();
        String dburi = "jdbc:mysql://" + System.getenv("RDS_HOSTNAME") + "/webdb?serverTimezone=UTC";
        String dbuser = System.getenv("RDS_USERNAME");
        String dbpassword = System.getenv("RDS_PASSWORD");
        this.db = new DatabaseHelper(dburi, dbuser, dbpassword);
    }

    /**get user saved hotels
     *\/savedhotel
     *
     * */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) {
        response.setContentType("application/json");
        HttpSession session = request.getSession();

        if(isVerified(session)) {
            handleGetSavedHotelsRequest(request, response);
        }
        else {
            handleUnauthorizedRequest(response);
        }
    }

    /**Save a hotel,
     * {
     *  "hotelId": 12345,
     * }
     * */
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) {

        response.setContentType("application/json");
        HttpSession session = request.getSession();

        if(isVerified(session)) {
            handleSaveHotelRequest(request, response);
        }
        else {
            handleUnauthorizedRequest(response);
        }
    }

    /**Delete saved hotel or clear hotels
     *
     {
        hotelId: 123,
     }
     * */
    @Override
    protected void doDelete(HttpServletRequest request,
                            HttpServletResponse response) {

        response.setContentType("application/json");
        HttpSession session = request.getSession();

        if(isVerified(session)) {
            handleDeleteSavedHotelRequest(request, response);
        }
        else {
            handleUnauthorizedRequest(response);
        }
    }

    private boolean isVerified(HttpSession session) {
        return session != null && session.getAttribute("verified") != null
                && (Boolean)(session.getAttribute(("verified")))
                && session.getAttribute("userId") != null;
    }

    private void handleUnauthorizedRequest(HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.println("{\"success\":false, \"error\": \"unauthorized\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleGetSavedHotelsRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            HttpSession session = request.getSession();
            String userId = session.getAttribute("userId").toString();
            responseSavedHotels(userId, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void responseSavedHotels(String userId, PrintWriter out) {
        JsonObject res = new JsonObject();
        JsonArray hotels = db.getSavedHotels(userId);
        if(hotels == null) {
            out.println("{\"success\":false}");
        }
        else {
            res.addProperty("success", true);
            res.add("hotels", hotels);
            out.println(res);
        }
    }

    private void handleSaveHotelRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Gson gson = new Gson();
            JsonObject data = gson.fromJson(sb.toString(), JsonObject.class);

            if(data.get("hotelId") != null) {
                Integer hotelId = parseInt(data.get("hotelId").toString());
                if(hotelId != null && addSavedHotels(hotelId, request.getSession())) {
                    out.println("{\"success\":true}");
                }
                else {
                    out.println("{\"success\":false}");
                }
            }
            else {
                out.println("{\"success\":false}");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean addSavedHotels(int hotelId, HttpSession session) {
        try {
            if( db.getHotel(hotelId) != null) {
                String userId = session.getAttribute("userId").toString();
                db.saveHotel(hotelId, userId);
                return true;
            }
            else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }


    private void handleDeleteSavedHotelRequest(HttpServletRequest request, HttpServletResponse response) {
        try(PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader;
            reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Gson gson = new Gson();
            JsonObject res = gson.fromJson(sb.toString(), JsonObject.class);
            Integer hotelId;
            if(res == null) {
                hotelId = null;
            }
            else {
                hotelId = res.get("hotelId") == null ? null : res.get("hotelId").getAsInt();
            }

            String userId = session.getAttribute("userId").toString();

            if (sb.length() != 0) {
                if(hotelId == null) {
                    deleteSavedHotels(userId);
                }
                else {
                    deleteSavedHotel(hotelId, userId);
                }
                out.println("{\"success\":true}");
            } else {
                out.println("{\"success\":false}");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void deleteSavedHotel(int hotelId, String userId) {
        db.deleteSavedHotel(hotelId, userId);
    }

    private void deleteSavedHotels(String userId) {
        db.deleteSavedHotels(userId);
    }

    private Integer parseInt(String s) {
        try{
            return Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
    }
}
