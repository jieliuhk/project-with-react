package sevlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import helper.DatabaseHelper;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ReviewInfoServlet extends HttpServlet {
    private DatabaseHelper db;

    public ReviewInfoServlet() {
        super();
        String dburi = "jdbc:mysql://" + System.getenv("RDS_HOSTNAME") + "/webdb?serverTimezone=UTC";
        String dbuser = System.getenv("RDS_USERNAME");
        String dbpassword = System.getenv("RDS_PASSWORD");
        this.db = new DatabaseHelper(dburi, dbuser, dbpassword);
    }

    /**get review of a hotel,  use format:
     *\/reviews?hotelId=12345
     *
     * */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession();

        if(isVerified(session)) {
            handleGetReviewsRequest(request, response);
        }
        else {
            handleUnauthorizedRequest(response);
        }
    }

    /**Add a new review,  use format:
     *
     * {
     *  "hotelId": 12345,
     *  "review": {
     *      title: "my title",
     *      text: "text here",
     *      rating: 4,
     *      posttime: cur.toISOString()
     *  }
     * }
     * */
    @Override
    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        HttpSession session = request.getSession();

        if(isVerified(session)) {
            handleAddReviewRequest(request, response);
        }
        else {
            handleUnauthorizedRequest(response);
        }
    }

    /**Update review submit review use format:
     *
     * {
         id: "review id",
         title: "my title",
         text: "text here",
         rating: 4,
         posttime: cur.toISOString()
       }
     * */
    @Override
    protected void doPut(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        HttpSession session = request.getSession();

        if(isVerified(session)) {
            handleUpdateReviewRequest(request, response);
        }
        else {
            handleUnauthorizedRequest(response);
        }
    }

    /**Update review submit review use format:
     *
     * {
         id: "review id",
     }
     * */
    @Override
    protected void doDelete(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        HttpSession session = request.getSession();

        if(isVerified(session)) {
            handleDeleteReviewRequest(request, response);
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

    private void handleGetReviewsRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            String hotelId = request.getParameter("hotelId");
            hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            Integer id = parseInt(hotelId);
            String userName = request.getParameter("userName");

            if(id != null) {
                responseReviews(id, out);
            }
            else if(userName != null) {
                HttpSession session = request.getSession();
                String userId = session.getAttribute("userId").toString();
                String userNameInSession = session.getAttribute("name").toString();
                if(userNameInSession.equals(userName) &&  userId!= null) {
                    responseReviews(userId, out);
                }
                else {
                    out.println("{\"success\":false}");
                }
            }
            else {
                out.println("{\"success\":false}");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void responseReviews(int hotelId, PrintWriter out) {
        JsonObject res = db.findReviews(hotelId);
        if(res == null) {
            out.println("{\"success\":false}");
        }
        else {
            res.addProperty("success", true);
            out.println(res);
        }
    }

    private void responseReviews(String userId, PrintWriter out) {
        JsonObject res = db.findReviews(userId);
        if(res == null) {
            out.println("{\"success\":false}");
        }
        else {
            res.addProperty("success", true);
            out.println(res);
        }
    }

    private void handleAddReviewRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            if(addReviews(sb.toString(), request.getSession())) {
                out.println("{\"success\":true}");
            }
            else {
                out.println("{\"success\":false}");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean addReviews(String postData, HttpSession session) {
        try {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(postData, JsonObject.class);
            int hotelId = data.get("hotelId").getAsInt();
            JsonObject reviewDetail = data.get("review").getAsJsonObject();
            String userId = (String) session.getAttribute("userId");
            JsonObject userDetail = gson.fromJson(db.findUserDetail(userId), JsonObject.class);
            db.addReview(hotelId, userDetail, reviewDetail);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void handleUpdateReviewRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            if(sb.length() != 0 && isReviewOwner(sb.toString(), request)) {
                updateReviews(sb.toString());
                out.println("{\"success\":true}");
            }
            else {
                out.println("{\"success\":false}");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void updateReviews(String newReview) {
        Gson gson = new Gson();
        JsonObject newReviewObj = gson.fromJson(newReview, JsonObject.class);
        db.updateReview(newReviewObj);
    }

    private void handleDeleteReviewRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader;
            reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            if (sb.length() != 0 && isReviewOwner(sb.toString(), request)) {
                deleteReviews(sb.toString());
                out.println("{\"success\":true}");
            } else {
                out.println("{\"success\":false}");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void deleteReviews(String reviews) {
        Gson gson = new Gson();
        JsonObject newReviewObj = gson.fromJson(reviews, JsonObject.class);
        db.deleteReview(newReviewObj.get("id").getAsString());
    }

    private boolean isReviewOwner(String reviews, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Gson gson = new Gson();
        JsonObject newReview = gson.fromJson(reviews, JsonObject.class);
        String reviewId = newReview.get("id").getAsString();
        String userId = (String) session.getAttribute("userId");

        JsonObject oldReview = gson.fromJson(db.findReview(reviewId), JsonObject.class);
        String owner = oldReview.get("iduser").getAsString();

        return userId.equals(owner);
    }

    private Integer parseInt(String s) {
        try{
            return Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
    }
}
