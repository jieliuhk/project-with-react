package helper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.time.LocalDateTime;

public class DatabaseHelperTest {

    private String dburi = "jdbc:mysql://" + System.getenv("RDS_HOSTNAME") + "/webdb?serverTimezone=UTC";
    private String dbuser = System.getenv("RDS_USERNAME");
    private String dbpassword = System.getenv("RDS_PASSWORD");

    @Test
    public void getHotelsTest() {
        DatabaseHelper dbhelper = new DatabaseHelper(dburi, dbuser, dbpassword);

        System.out.println(dbhelper.getHotels());
    }

    @Test
    public void getHotelTest() {
        DatabaseHelper dbhelper = new DatabaseHelper(dburi, dbuser, dbpassword);

        System.out.println(dbhelper.getHotel(1047));
    }

    @Test
    public void getHotelByCityTest() {
        DatabaseHelper dbhelper = new DatabaseHelper(dburi, dbuser, dbpassword);

        System.out.println(dbhelper.getHotelsByCity("San Francisco"));
    }

    @Test
    public void getHotelByKeywordTest() {
        DatabaseHelper dbhelper = new DatabaseHelper(dburi, dbuser, dbpassword);

        System.out.println(dbhelper.getHotelsByKeyword("San Francisco"));
    }

    @Test
    public void getReviewsTest() {
        DatabaseHelper dbhelper = new DatabaseHelper(dburi, dbuser, dbpassword);

        System.out.println(dbhelper.findReviews(519729));
    }

    @Test
    public void getReviewTest() {
        DatabaseHelper dbhelper = new DatabaseHelper(dburi, dbuser, dbpassword);

        System.out.println(dbhelper.findReviews("94b7eddc-199a-11ea-abc7-0200d1740be4"));
    }

    @Test
    public void getUserDetailTest() {
        DatabaseHelper dbhelper = new DatabaseHelper(dburi, dbuser, dbpassword);

        System.out.println(dbhelper.findUserDetail("id123"));
    }

    @Test
    public void updateUserLoginTimeTest() {
        DatabaseHelper dbhelper = new DatabaseHelper(dburi, dbuser, dbpassword);

        dbhelper.updateUserLoginTime("id123", LocalDateTime.now().toString());
        System.out.println(dbhelper.findUserDetail("id123"));
    }

    @Test
    public void addReviewTest() {
        DatabaseHelper dbhelper = new DatabaseHelper(dburi, dbuser, dbpassword);

        Gson gson = new Gson();
        JsonObject user = gson.fromJson("{\"id\": \"aaa\", \"name\": \"lalal\"}", JsonObject.class);
        JsonObject review = gson.fromJson(
                "{\"id\": \"333\", \"title\": \"ttt\", \"text\": \"xxx\", \"rating\": 3,\"posttime\":"
                        + "\"" + LocalDateTime.now()
                        + "\"}", JsonObject.class);

        dbhelper.addReview(519729, user, review);
    }

    @Test
    public void getSpKey() {
        DatabaseHelper dbhelper = new DatabaseHelper(dburi, dbuser, dbpassword);
        System.out.println(dbhelper.getServiceKey("spkey"));
    }

    @Test
    public void addUser() {
        DatabaseHelper dbhelper = new DatabaseHelper(dburi, dbuser, dbpassword);
        dbhelper.addUser("id111", "JieLiu");
    }

    @Test
    public void saveHotel() {
        DatabaseHelper dbhelper = new DatabaseHelper(dburi, dbuser, dbpassword);
        dbhelper.saveHotel(123, "hhh1");
        dbhelper.saveHotel(123, "hhh2");
        dbhelper.saveHotel(122, "hhh1");
    }

    @Test
    public void getSavedHotel() {
        DatabaseHelper dbhelper = new DatabaseHelper(dburi, dbuser, dbpassword);
        System.out.print(dbhelper.getSavedHotels("hhh1"));
    }

    @Test
    public void deleteHotel() {
        DatabaseHelper dbhelper = new DatabaseHelper(dburi, dbuser, dbpassword);
        dbhelper.deleteSavedHotel(123, "hhh1");
    }

    @Test
    public void deleteAllHotels() {
        DatabaseHelper dbhelper = new DatabaseHelper(dburi, dbuser, dbpassword);
        dbhelper.deleteSavedHotels("hhh1");
    }
}