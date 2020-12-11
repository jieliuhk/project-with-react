package helper;

import org.junit.Test;

import static org.junit.Assert.*;

public class ASDatabaseHelperTest {

    private String dburi = "jdbc:mysql://" + System.getenv("RDS_ID_HOSTNAME") + "/iddb?serverTimezone=UTC";
    private String dbuser = System.getenv("RDS_USERNAME");
    private String dbpassword = System.getenv("RDS_PASSWORD");

    @Test
    public void registerUser() {
        ASDatabaseHelper db = new ASDatabaseHelper(dburi, dbuser, dbpassword);
        db.registerUser("jie", "123abc@11");
    }

    @Test
    public void authenticateUser() {
        ASDatabaseHelper db = new ASDatabaseHelper(dburi, dbuser, dbpassword);
        System.out.println(db.authenticateUser("jie", "123abc@11"));
    }

    @Test
    public void prepareTGSSessionKey() {
        ASDatabaseHelper db = new ASDatabaseHelper(dburi, dbuser, dbpassword);
        System.out.println(db.prepareTGSSessionKey("jie", "123abc@11"));
    }

    @Test
    public void prepareTGT() {
        ASDatabaseHelper db = new ASDatabaseHelper(dburi, dbuser, dbpassword);
        System.out.println(db.prepareTGT("jie", "default", "123abc@11"));
    }
}