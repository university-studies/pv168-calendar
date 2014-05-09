package cz.muni.fi.pv168.calendar;

import cz.muni.fi.pv168.calendar.common.DBUtils;
import cz.muni.fi.pv168.calendar.entity.User;
import cz.muni.fi.pv168.calendar.service.UserManager;
import cz.muni.fi.pv168.calendar.service.impl.UserManagerImpl;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by Mario on 5.3.2014.
 */
public class UserManagerTest {

    public static final String NEW_TEST_USER_LOGIN = "NewTestUserLogin";
    private static UserManager userManager;
    private static final Logger log = LoggerFactory.getLogger(UserManagerTest.class);
    private static DataSource ds;

    @BeforeClass
    public static void setClassUp() throws Exception{
        Properties conf = new Properties();
        conf.load(Main.class.getResourceAsStream(Main.DB_PROPERTIES));

        PoolProperties p = new PoolProperties();
        p.setPassword(conf.getProperty("db.password"));
        p.setUsername(conf.getProperty("db.user"));
        p.setUrl(conf.getProperty("db.url.embedded"));
        p.setDriverClassName(conf.getProperty("db.driver.embedded"));

        log.info("UserManagerTest");
        ds = new DataSource(p);
        userManager = new UserManagerImpl(ds);

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

    }

    @Test
    public void testRetrieveUser() throws Exception{
        User user = new User("Test User","test","test@test.com");
        userManager.createUser(user);

        User resultUser = userManager.getUserById(user.getId());
        assertEquals(user,resultUser);

        assertThat(userManager.getUserById(3),is(nullValue())); //test to return null value if user doesn't exist

        Set<User> people = new HashSet<User>();
        people.add(user);

        User user1 = new User("Karol","test","test@test.com");
        userManager.createUser(user1);
        people.add(user1);

        user1 = new User("Pater","aka","test@test.com");
        userManager.createUser(user1);
        people.add(user1);

        user1 = new User("Pavol","134","test@test.com");
        userManager.createUser(user1);
        people.add(user1);

        user1 = new User("Jan","a.z.ma","test@test.com");
        userManager.createUser(user1);
        people.add(user1);

        Collection<User> users = userManager.getAllUsers();
        assertEquals(users.size(),5);
        for(User u : users){
            if(!people.contains(u))
                fail("Nespravne udaje! " + u.toString());
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

    @Test
    public void testUpdateUser() throws Exception{
        User user = new User("TestUser","test","test@test.com");
        userManager.createUser(user);
        user.setLogin(NEW_TEST_USER_LOGIN);
        userManager.updateUser(user);

        assertEquals(NEW_TEST_USER_LOGIN,userManager.getUserById(user.getId()).getLogin());
    }

    @Before
    public void setUp() throws Exception {
        DBUtils.executeSqlScript(ds, Main.class.getResource(Main.DB_CREATE));
    }

    /**
     * Called after each test method
     */
    @After
    public void tearDown() throws Exception {
        DBUtils.executeSqlScript(ds,
                EventManagerImplTest.class.getResource(Main.DB_DROP));
    }

}
