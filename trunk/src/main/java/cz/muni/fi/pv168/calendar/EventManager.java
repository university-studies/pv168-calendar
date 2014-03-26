package cz.muni.fi.pv168.calendar;

import cz.muni.fi.pv168.calendar.common.ServiceFailureException;

import java.util.Date;
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
    public List<Event> findEventByStartDate(Date date) throws ServiceFailureException;
}


