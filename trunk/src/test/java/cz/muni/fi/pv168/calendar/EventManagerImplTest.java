package cz.muni.fi.pv168.calendar;

import org.apache.derby.jdbc.ClientDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by Pavol Loffay, p.loffay@gmail.com on 3/7/14.
 */
public class EventManagerImplTest {

    private EventManagerImpl manager;
    private ClientDataSource dataSource;

    @Before
    public void setUp() throws Exception {
        Properties properties = new Properties();
        properties.load(Main.class.getResourceAsStream(Main.DB_PROPERTIES));

        System.out.println(properties.getProperty("db.name"));

        dataSource = new ClientDataSource();
        dataSource.setDatabaseName(properties.getProperty("db.name"));
        dataSource.setUser(properties.getProperty("db.user"));
        dataSource.setPassword("db.password");

        manager = new EventManagerImpl(dataSource);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCreateEvent() throws Exception {
        Event event = newEvent("Kupit si auto","pekne");

        manager.createEvent(event);

        assertNotNull(event.getId());
        assertThat(event.getId(), notNullValue());

        assertEquals(event, manager.findEventById(event.getId()));
        assertThat(event, equalTo(manager.findEventById(event.getId())));
    }

    @Test
    public void testUpdateEvent() throws Exception {
        Event event1 = newEvent("Kupit si auto", "pekne");
        Event event2 = newEvent("Kupit si motorku", "skaredu");

        manager.createEvent(event1);
        manager.createEvent(event2);

        Long id = event1.getId();
        event1 = manager.findEventById(id);
        event1.setTitle("Ponorka");
        manager.updateEvent(event1);
        assertEquals("Ponorka", event1.getTitle());
        assertEquals("Ponorka", manager.findEventById(id).getTitle());
        assertEquals("pekne", manager.findEventById(id).getDescription());
        assertThat("hocico", is(not(equalTo(manager.findEventById(id).getDescription()))));
    }

    @Test
    public void testDeleteEvent() throws Exception {
        Event event1 = newEvent("Kupit si auto", "pekne");

        manager.createEvent(event1);
        Long id = event1.getId();

        /**
         * Check if is event stored id DB
         */
        assertThat(id, is(notNullValue()));
        Event event2 = manager.findEventById(id);
        assertThat(event1, is(equalTo(event2)));
        assertThat("Kupit si auto", is(equalTo(manager.findEventById(id).getTitle())));

        /**
         * Check delete
         */
        manager.deleteEvent(event1);
        assertThat(manager.findEventById(id), is(nullValue()));
    }

    @Test
    public void testFindAllEvents() throws Exception {

    }

    @Test
    public void testFindEventByStartDate() throws Exception {

    }

    private Event newEvent(String title, String desc) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(desc);

        return event;
    }
}
