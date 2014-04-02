package cz.muni.fi.pv168.calendar;

import cz.muni.fi.pv168.calendar.entity.Event;
import cz.muni.fi.pv168.calendar.entity.Reminder;
import cz.muni.fi.pv168.calendar.common.DBUtils;
import cz.muni.fi.pv168.calendar.service.EventManager;
import cz.muni.fi.pv168.calendar.service.ReminderManager;
import cz.muni.fi.pv168.calendar.service.impl.EventManagerImpl;
import cz.muni.fi.pv168.calendar.service.impl.ReminderManagerImpl;
import cz.muni.fi.pv168.calendar.service.impl.RemindersManagerImpl;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by Pavol Loffay, p.loffay@gmail.com on 4/1/14.
 */
public class RemindersManagerTest {
    Logger log = LoggerFactory.getLogger(EventManagerImpl.class);

    private static RemindersManagerImpl remindersManager;
    private static DataSource dataSource;
    private static EventManager eventManager;
    private static ReminderManager reminderManager;

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
        remindersManager = new RemindersManagerImpl(dataSource);
        eventManager = new EventManagerImpl(dataSource);
        reminderManager = new ReminderManagerImpl(dataSource);
    }

    /**
     * Called before each test method
     */
    @Before
    public void setUp() throws Exception {
        try {
            DBUtils.executeSqlScript(dataSource, Main.class.getResource(Main.DB_CREATE));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
    public void testFindEventsByReminder() throws Exception {
        Event event = newEvent("Kupit si auto", "pekne", DateTime.now(), DateTime.now());
        Reminder reminder1 = new Reminder(new DateTime(2014, 1, 1, 15, 30));
        Reminder reminder2 = new Reminder(new DateTime(2015, 6, 6, 15, 30));

        eventManager.createEvent(event);
        reminderManager.createReminder(reminder1);
        reminderManager.createReminder(reminder2);
        remindersManager.setReminderToEvent(reminder1, event);
        remindersManager.setReminderToEvent(reminder2, event);

        assertNotNull(remindersManager.findEventsByReminder(reminder1).get(0));
        assertThat(remindersManager.findEventsByReminder(reminder1).get(0),
                is(equalTo(event)));

        assertNotNull(event.getId());
        assertThat(event.getId(), notNullValue());

    }

    @Test
    public void testFindRemindersByEvent() throws Exception {
        Event event1 = newEvent("title", "description", DateTime.now(), DateTime.now());
        Event event2 = newEvent("title 2", "description2", DateTime.now(), DateTime.now());
        Reminder reminder1 = new Reminder(new DateTime(2015, 1, 1, 14, 2));
        Reminder reminder2 = new Reminder(new DateTime(1010, 1, 6, 15, 15));

        eventManager.createEvent(event1);
        eventManager.createEvent(event2);
        reminderManager.createReminder(reminder1);
        reminderManager.createReminder(reminder2);

        remindersManager.setReminderToEvent(reminder1, event1);
        remindersManager.setReminderToEvent(reminder2, event2);

        assertThat(reminder1, is(equalTo(remindersManager.findRemindersByEvent(event1)
                .get(0))));
    }

    @Test
    public void testUnSetReminderToEvent() throws Exception {
        log.debug("testUnSetReminderToEvent");
        Event event1 = newEvent("title", "description", DateTime.now(), DateTime.now());
        Event event2 = newEvent("title 2", "description2", DateTime.now(), DateTime.now());
        Reminder reminder1 = new Reminder(new DateTime(2015, 1, 1, 14, 2));
        Reminder reminder2 = new Reminder(new DateTime(1010, 1, 6, 15, 15));

        eventManager.createEvent(event1);
        eventManager.createEvent(event2);
        reminderManager.createReminder(reminder1);
        reminderManager.createReminder(reminder2);

        remindersManager.setReminderToEvent(reminder1, event1);
        remindersManager.setReminderToEvent(reminder2, event2);

        assertThat(reminder1, is(equalTo(remindersManager.findRemindersByEvent(event1)
                .get(0))));

        remindersManager.unSetReminderFromEvent(reminder1, event1);
        assertThat(remindersManager.findRemindersByEvent(event1),
                is(Collections.EMPTY_LIST));
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
