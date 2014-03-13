package cz.muni.fi.pv168.calendar;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.util.logging.resources.logging;

import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItem;

/**
 * Created by Mario on 5.3.2014.
 */
public class UserManagerTest {

    UserManager userManager;

    @Before
    public void setUp() throws Exception {
        userManager = new UserManagerImpl();

    }

    @Test
    public void  testCreateUser() throws Exception{
        //valid user create test
        User user = new User("Mario","test","mario@test.com");
        userManager.createUser(user);
        User user1 = userManager.getUserById(user.getId());
        assertThat(user1,is(not(nullValue()))); //test to return null-value
        assertEquals(user,user1);

        //IllegalArgumentException test
        try{
            userManager.createUser(null);
            fail("Nevyhodila sa vynimka IllegalArgumentException!");
        }catch(IllegalArgumentException ex){} //ok

        //Existing id error test
        user = new User(1,"Karol","test","test@test.com");
        try{
            userManager.createUser(user);
            fail("Nevyhodila sa vynimka!");
        }catch (Exception ex){} //ok -- Exception is temporary solution
    }

    @Test
    public void testRetrieveUser() throws Exception{
        User user = new User("Test User","test","test@test.com");
        userManager.createUser(user);

        User resultUser = userManager.getUserById(0);
        assertEquals(user,resultUser);

        assertThat(userManager.getUserById(3),is(nullValue())); //test to return null value if user doesn't exist

        Set<User> people = new HashSet<User>();
        people.add(user);

        User user1 = new User("Karol","test","test@test.com");
        people.add(user1);
        userManager.createUser(user1);

        user1 = new User("Pater","aka","test@test.com");
        people.add(user1);
        userManager.createUser(user1);

        user1 = new User("Pavol","134","test@test.com");
        people.add(user1);
        userManager.createUser(user1);

        user1 = new User("Jan","a.z.ma","test@test.com");
        people.add(user1);
        userManager.createUser(user1);

        Collection<User> users = userManager.getAllUsers();
        assertEquals(users.size(),5);
        for(User u : users){
            if(!people.contains(u))
                fail("Nespravne udaje!");
        }
    }

    @Test
    public void testRemoveUser() throws Exception{
        User user = new User("Remove","remo","test@test.com");
        userManager.createUser(user);
        User user1 = new User("Karol","test","test@test.com");
        userManager.createUser(user1);
        userManager.removeUser(userManager.getUserById(user.getId()));
        assertThat(userManager.getUserById(1),is(nullValue()));

        assertEquals(1,userManager.getAllUsers().size());
        assertTrue(userManager.getAllUsers().contains(user1));


        user = new User("Mario","test","test@test.com");
        userManager.createUser(user);
        assertThat(userManager.getUserById(1),is(nullValue())); // system can't use id which was used by an other user
                                                                // the user might have been removed
        assertEquals(user, userManager.getUserById(3));
    }
}
