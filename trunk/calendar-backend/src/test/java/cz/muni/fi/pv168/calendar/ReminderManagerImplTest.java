package cz.muni.fi.pv168.calendar;

import cz.muni.fi.pv168.calendar.common.DBUtils;
import cz.muni.fi.pv168.calendar.entity.Reminder;
import cz.muni.fi.pv168.calendar.service.ReminderManager;
import cz.muni.fi.pv168.calendar.service.impl.EventManagerImpl;
import cz.muni.fi.pv168.calendar.service.impl.ReminderManagerImpl;
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
import java.util.Properties;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by Pavol Loffay, p.loffay@gmail.com on 4/1/14.
 */
public class ReminderManagerImplTest {
    Logger log = LoggerFactory.getLogger(EventManagerImpl.class);

    private static DataSource dataSource;
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
        catch (SQLException ex) {
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
    public void testCreateReminder() throws Exception {
        Reminder reminder1 = new Reminder(new DateTime(2014, 1, 1, 15, 30));
        Reminder reminder2 = new Reminder(new DateTime(2015, 6, 6, 15, 30));

        reminderManager.createReminder(reminder1);
        reminderManager.createReminder(reminder2);

        assertThat(reminder1.getId(), is(notNullValue()));
        assertThat(reminder1, is(equalTo(reminderManager.findReminderById(reminder1
                .getId()))));
        assertThat(reminder2.getId(), is(notNullValue()));
        assertThat(reminder2, is(equalTo(reminderManager.findReminderById(reminder2
                .getId()))));
    }

    @Test
    public void testUpdateReminder() throws Exception {
        Reminder reminder1 = new Reminder(new DateTime(2014, 1, 1, 15, 30));
        Reminder reminder2 = new Reminder(new DateTime(2015, 6, 6, 15, 30));

        reminderManager.createReminder(reminder1);
        reminderManager.createReminder(reminder2);

        Long id = reminder1.getId();
        assertThat(reminder1.getId(), is(notNullValue()));
        assertThat(reminder1, is(equalTo(reminderManager.findReminderById(reminder1.getId()))));
        assertThat(reminder2.getId(), is(notNullValue()));
        assertThat(reminder2, is(equalTo(reminderManager.findReminderById(reminder2
                .getId()))));

        DateTime date = new DateTime(2011, 1, 1, 15, 13);
        reminder1.setDate(date);
        reminderManager.updateReminder(reminder1);
        assertThat(date, is(equalTo(reminderManager.findReminderById(id).getDate())));
    }

    @Test
    public void testDeleteReminder() throws Exception {
        Reminder reminder1 = new Reminder(new DateTime(2014, 1, 1, 15, 30));
        Reminder reminder2 = new Reminder(new DateTime(2015, 6, 6, 15, 30));

        reminderManager.createReminder(reminder1);
        reminderManager.createReminder(reminder2);
        Long id = reminder1.getId();

        assertThat(reminder1.getId(), is(notNullValue()));
        assertThat(reminder1, is(equalTo(reminderManager.findReminderById(reminder1
                .getId()))));
        assertThat(reminder2.getId(), is(notNullValue()));
        assertThat(reminder2, is(equalTo(reminderManager.findReminderById(reminder2
                .getId()))));

        reminderManager.deleteReminder(reminder1);
        assertThat(reminderManager.findReminderById(id), is(nullValue()));
    }

    @Test
    public void testFindReminderByDate() throws Exception {
        Reminder reminder1 = new Reminder(new DateTime(2014, 1, 1, 15, 30));
        Reminder reminder2 = new Reminder(new DateTime(2015, 6, 6, 15, 30));

        reminderManager.createReminder(reminder1);
        reminderManager.createReminder(reminder2);
        Long id = reminder1.getId();

        assertThat(reminder1.getId(), is(notNullValue()));
        assertThat(reminder1, is(equalTo(reminderManager.findReminderById(reminder1
                .getId()))));
        assertThat(reminder2.getId(), is(notNullValue()));
        assertThat(reminder2, is(equalTo(reminderManager.findReminderById(reminder2
                .getId()))));


        assertThat(reminder1, is(equalTo(reminderManager.findRemindersByDate(reminder1
                .getDate()).get(0))));
        assertThat(reminder1, is(not(equalTo(reminderManager.findRemindersByDate
                (reminder2.getDate()).get(0)))));
        assertThat(new DateTime(2014, 1, 1, 15, 30), is(equalTo(reminderManager
                .findRemindersByDate(reminder1.getDate()).get(0).getDate())));
    }
}
