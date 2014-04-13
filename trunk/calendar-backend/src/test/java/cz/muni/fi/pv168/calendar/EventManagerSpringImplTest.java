package cz.muni.fi.pv168.calendar;

import cz.muni.fi.pv168.calendar.entity.Event;
import cz.muni.fi.pv168.calendar.service.EventManager;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by Pavol Loffay, p.loffay@gmail.com on 4/2/14.
 */
//@ContextConfiguration(classes = {SpringTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring-test.xml"})
@Transactional
public class EventManagerSpringImplTest {
    Logger log = LoggerFactory.getLogger(EventManagerSpringImplTest.class);

    @Autowired
    @Qualifier("eventManagerTest")
    private EventManager manager;

    /**
     * Called before each test method
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Called after each test method
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreateEvent() throws Exception {
        log.debug("Test Spring create event");
        Event event = newEvent("Kupit si auto", "pekne", DateTime.now(), DateTime.now());

        manager.createEvent(event);

        assertNotNull(event.getId());
        assertThat(event.getId(), notNullValue());

        assertEquals(event, manager.findEventById(event.getId()));
        assertThat(event, equalTo(manager.findEventById(event.getId())));
        log.debug(manager.findEventById(event.getId()).getStartDate().toString());
        log.debug(manager.findAllEvents().toString());
    }

    @Test
    public void testUpdateEvent() throws Exception {
        log.debug("Test Spring update Event");
        Event event1 = newEvent("Kupit si auto", "pekne", DateTime.now(), DateTime.now());
        Event event2 = newEvent("Kupit si motorku", "skaredu", DateTime.now(), DateTime.now());

        manager.createEvent(event1);
        manager.createEvent(event2);

        Long id = event1.getId();
        event1 = manager.findEventById(id);
        event1.setTitle("Ponorka");
        manager.updateEvent(event1);
        assertEquals("Ponorka", event1.getTitle());
        assertEquals("Ponorka", manager.findEventById(id).getTitle());
        assertEquals("pekne", manager.findEventById(id).getDescription());
        assertThat("hocico", is(not(equalTo(manager.findEventById(id)
                .getDescription()))));

        log.debug(manager.findAllEvents().toString());
    }

    @Test
    public void testDeleteEvent() throws Exception {
        log.debug("Test spring delete event");
        Event event1 = newEvent("Kupit si auto", "pekne", DateTime.now(), DateTime.now());

        manager.createEvent(event1);
        Long id = event1.getId();

        /**
         * Check if is event stored id DB
         */
        assertThat(id, is(notNullValue()));
        Event event2 = manager.findEventById(id);
        assertThat(event1, is(equalTo(event2)));
        assertThat("Kupit si auto", is(equalTo(manager.findEventById(id)
                .getTitle())
        ));

        /**
         * Check delete
         */
        manager.deleteEvent(event1);
        assertThat(manager.findEventById(id), is(nullValue()));
        log.debug(manager.findAllEvents().toString());
    }

    @Test
    public void testFindEventByStartDate() throws Exception {
        log.debug("Test Spring Find all Events");
        DateTime date = new DateTime(2012, 1, 1, 12, 0);
        Event event = newEvent("titulok", "opis", date, date);

        manager.createEvent(event);
        assertThat(event, is(equalTo(manager.findEventByStartDate(date).get(0))));
        log.debug(manager.findAllEvents().toString());
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
