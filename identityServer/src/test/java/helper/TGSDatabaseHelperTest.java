package helper;

import org.junit.Test;

import static org.junit.Assert.*;

public class TGSDatabaseHelperTest {
    private String dburi = "jdbc:mysql://" + System.getenv("RDS_ID_HOSTNAME") + "/iddb?serverTimezone=UTC";
    private String dbuser = System.getenv("RDS_USERNAME");
    private String dbpassword = System.getenv("RDS_PASSWORD");

    @Test
    public void prepareTicket() {
        TGSDatabaseHelper db = new TGSDatabaseHelper(dburi, dbuser, dbpassword);
        System.out.println(db.getServiceKey("Travel Helper"));
    }
}