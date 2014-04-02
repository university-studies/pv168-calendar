package cz.muni.fi.pv168.calendar;

import cz.muni.fi.pv168.calendar.entity.Event;
import cz.muni.fi.pv168.calendar.common.DBUtils;
import cz.muni.fi.pv168.calendar.service.impl.EventManagerImpl;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by Pavol Loffay, p.loffay@gmail.com on 3/7/14.
 */
public class EventManagerImplTest {
    Logger log = LoggerFactory.getLogger(EventManagerImpl.class);

    @Autowired
    private static EventManagerImpl manager;
    private static DataSource dataSource;

    /**
     * Called once on startup
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        Properties conf = new Properties();
        conf.load(Main.class.getResourceAsStream(Main.DB_PROPERTIES));

        PoolProperties p = new PoolProperties();
        p.setPassword(conf.getProperty("db.password"));
        p.setUsername(conf.getProperty("db.user"));
        p.setUrl(conf.getProperty("db.url.embedded"));
        p.setDriverClassName(conf.getProperty("db.driver.embedded"));

        dataSource = new DataSource(p);
        manager = new EventManagerImpl(dataSource);
    }

    /**
     * Called before each test method
     */
    @Before
    public void setUp() throws Exception {
        DBUtils.executeSqlScript(dataSource, Main.class.getResource(Main.DB_CREATE));
    }

    /**
     * Called after each test method
     */
    @After
    public void tearDown() throws Exception {
        DBUtils.executeSqlScript(dataSource,
                EventManagerImplTest.class.getResource(Main.DB_DROP));
    }

    @Test
    public void testCreateEvent() throws Exception {
        Event event = newEvent("Kupit si auto", "pekne", DateTime.now(), DateTime.now());

        manager.createEvent(event);

        assertNotNull(event.getId());
        assertThat(event.getId(), notNullValue());

        assertEquals(event, manager.findEventById(event.getId()).get(0));
        assertThat(event, equalTo(manager.findEventById(event.getId()).get(0)));
        log.debug(manager.findEventById(event.getId()).get(0).getStartDate().toString());
    }

    @Test
    public void testUpdateEvent() throws Exception {
        Event event1 = newEvent("Kupit si auto", "pekne", DateTime.now(), DateTime.now());
        Event event2 = newEvent("Kupit si motorku", "skaredu", DateTime.now(), DateTime.now());

        manager.createEvent(event1);
        manager.createEvent(event2);

        Long id = event1.getId();
        event1 = manager.findEventById(id).get(0);
        event1.setTitle("Ponorka");
        manager.updateEvent(event1);
        assertEquals("Ponorka", event1.getTitle());
        assertEquals("Ponorka", manager.findEventById(id).get(0).getTitle());
        assertEquals("pekne", manager.findEventById(id).get(0).getDescription());
        assertThat("hocico", is(not(equalTo(manager.findEventById(id).get(0)
                .getDescription()
        ))));
    }

    @Test
    public void testDeleteEvent() throws Exception {
        Event event1 = newEvent("Kupit si auto", "pekne", DateTime.now(), DateTime.now());

        manager.createEvent(event1);
        Long id = event1.getId();

        /**
         * Check if is event stored id DB
         */
        assertThat(id, is(notNullValue()));
        Event event2 = manager.findEventById(id).get(0);
        assertThat(event1, is(equalTo(event2)));
        assertThat("Kupit si auto", is(equalTo(manager.findEventById(id).get(0)
                .getTitle())
        ));

        /**
         * Check delete
         */
        manager.deleteEvent(event1);
        assertThat(manager.findEventById(id), is(Collections.EMPTY_LIST));
    }

    @Test
    public void testFindEventByStartDate() throws Exception {
        DateTime date = new DateTime(2012, 1, 1, 12, 0);
        Event event = newEvent("titulok", "opis", date, date);

        manager.createEvent(event);
        assertThat(event, is(equalTo(manager.findEventByStartDate(date).get(0))));
    }

    private Event newEvent(String title, String desc, DateTime start, DateTime end) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(desc);
        event.setStartDate(start);
        event.setEndDate(end);
        return event;
    }
}
