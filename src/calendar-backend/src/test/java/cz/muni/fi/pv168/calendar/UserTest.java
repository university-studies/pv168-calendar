package cz.muni.fi.pv168.calendar;

import cz.muni.fi.pv168.calendar.common.PasswordHash;
import cz.muni.fi.pv168.calendar.entity.User;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Mario Kudolani on 3.4.2014.
 */
public class UserTest {

    public static final String PASSWORD = "password";

    @Test
    public void validateEmailTest() throws Exception{
        User user;
        try {
            user = new User("test_login", "password", "INVALID_MAIL");
            fail("Nevyhodila sa vynimka");
        }catch(Exception e) {}//ok

        user = new User("login","pass","email@email.com");
        //ok
    }

    @Test
    public void validatePasswordHash() throws Exception{
        User user = new User("login", PASSWORD,"email@email.com");

        assertTrue(PasswordHash.validatePassword(PASSWORD,user.getPassword()));
    }
}
