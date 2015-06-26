package cz.muni.fi.pv168.calendar.service;

import cz.muni.fi.pv168.calendar.common.ServiceFailureException;
import cz.muni.fi.pv168.calendar.entity.Event;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by xloffay on 5.3.14.
 */
public interface EventManager {

    public void createEvent(Event event) throws ServiceFailureException;
    public void updateEvent(Event event) throws ServiceFailureException;
    public void deleteEvent(Event event) throws ServiceFailureException;

    public Event findEventById(Long id) throws ServiceFailureException;
    public List<Event> findAllEvents() throws ServiceFailureException;
    public List<Event> findEventByStartDate(DateTime date) throws
            ServiceFailureException;
    public List<Event> findEventByStartDateAndUser(DateTime date, Long userId)
            throws ServiceFailureException;
}


