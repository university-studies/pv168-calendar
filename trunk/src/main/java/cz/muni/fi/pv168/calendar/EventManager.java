package cz.muni.fi.pv168.calendar;

import java.util.Date;
import java.util.List;

/**
 * Created by xloffay on 5.3.14.
 */
public interface EventManager {

    public void createEvent(Event event);
    public void updateEvent(Event event);
    public void deleteEvent(Event event);

    public Event findEventById(Long id);
    public List<Event> findAllEvents();
    public List<Event> findEventByStartDate(Date date);
}


