package cz.muni.fi.pv168.calendar;

import cz.muni.fi.pv168.calendar.common.DBUtils;
import cz.muni.fi.pv168.calendar.entity.Event;
import cz.muni.fi.pv168.calendar.entity.User;
import cz.muni.fi.pv168.calendar.service.EventManager;
import cz.muni.fi.pv168.calendar.service.UserManager;
import cz.muni.fi.pv168.calendar.service.UsersEventManager;
import cz.muni.fi.pv168.calendar.service.impl.EventManagerSpringImpl;
import cz.muni.fi.pv168.calendar.service.impl.UserManagerImpl;
import cz.muni.fi.pv168.calendar.service.impl.UsersEventManagerImpl;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by Mario Kudolani on 8.4.2014.
 */
public class UsersEventManagerTest {

    private static Logger log = LoggerFactory.getLogger(UsersEventManagerTest.class);
    private static UsersEventManager usersEventManager;
    private static DataSource ds;

    @BeforeClass
    public static void setUpClass() throws Exception{
        Properties conf = new Properties();
        conf.load(Main.class.getResourceAsStream(Main.DB_PROPERTIES));

        PoolProperties p = new PoolProperties();
        p.setPassword(conf.getProperty("db.password"));
        p.setUsername(conf.getProperty("db.user"));
        p.setUrl(conf.getProperty("db.url.embedded"));
        p.setDriverClassName(conf.getProperty("db.driver.embedded"));

        log.info("UsersEventManager");
        ds = new DataSource(p);
        usersEventManager = new UsersEventManagerImpl(ds);
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

    @Test
    public void testFindEventsByUserId() throws Exception {
        UserManager userManager = new UserManagerImpl(ds);
        EventManager eventManager = new EventManagerSpringImpl(ds);

        User user = new User("login","password","email@email.com");
        userManager.createUser(user);

        Event event = new Event();
        event.setTitle("test");
        event.setDescription("test to find");
        event.setStartDate(DateTime.now());
        event.setEndDate(DateTime.now());

        eventManager.createEvent(event);
        Event event1 = new Event();
        event1.setTitle("test 1");
        event1.setStartDate(DateTime.now());
        event1.setEndDate(DateTime.now());
        eventManager.createEvent(event1);

        usersEventManager.addEventToUser(event,user);
        Collection<Event> events = usersEventManager.findEventsByUserId(user.getId());

        assertThat(1,is(equalTo(events.size())));

        assertThat(0,is(equalTo(usersEventManager.findEventsByUserId(2).size())));
        assertThat(1,is(equalTo(usersEventManager.findEventsByUserId(-1).size())));

    }

    @Test
    public void testAddEventToUser() throws Exception {
        UserManager userManager = new UserManagerImpl(ds);
        EventManager eventManager = new EventManagerSpringImpl(ds);

        User user = new User("login","password","email@email.com");
        userManager.createUser(user);

        Event event = new Event();
        event.setTitle("test");
        event.setDescription("test to find");
        event.setStartDate(DateTime.now());
        event.setEndDate(DateTime.now());

        eventManager.createEvent(event);
        Event event1 = new Event();
        event1.setTitle("test 1");
        event1.setStartDate(DateTime.now());
        event1.setEndDate(DateTime.now());
        eventManager.createEvent(event1);


        usersEventManager.addEventToUser(event,user);
        assertThat(event.getUserId(),is(equalTo(user.getId())));

        try{
            usersEventManager.addEventToUser(null,null);
            fail("Nevyhodila sa vynimka");
        }catch(Exception e){} //ok

    }

    @Test
    public void testRemoveEventFromUser() throws Exception {
        UserManager userManager = new UserManagerImpl(ds);
        EventManager eventManager = new EventManagerSpringImpl(ds);

        User user = new User("login","password","email@email.com");
        userManager.createUser(user);

        Event event = new Event();
        event.setTitle("test");
        event.setDescription("test to find");
        event.setStartDate(DateTime.now());
        event.setEndDate(DateTime.now());

        eventManager.createEvent(event);
        Event event1 = new Event();
        event1.setTitle("test 1");
        event1.setStartDate(DateTime.now());
        event1.setEndDate(DateTime.now());
        eventManager.createEvent(event1);

        usersEventManager.addEventToUser(event,user);
        usersEventManager.addEventToUser(event1,user);

        usersEventManager.removeEventFromUser(event);

        assertThat(1, is(equalTo(usersEventManager.findEventsByUserId(user.getId()).size())));

        for(Event e : usersEventManager.findEventsByUserId(user.getId())){
            assertThat(event1, is(equalTo(e)));
        }

        try{
            usersEventManager.removeEventFromUser(null);
            fail("Nevyhodila sa vynimka");
        }catch (Exception e) {} //ok
    }
}
