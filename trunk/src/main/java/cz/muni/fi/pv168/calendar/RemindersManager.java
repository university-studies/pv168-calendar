package cz.muni.fi.pv168.calendar;

import cz.muni.fi.pv168.calendar.common.ServiceFailureException;

import java.util.List;

/**
 * Created by Pavol Loffay, p.loffay@gmail.com on 3/7/14.
 */
public interface RemindersManager {

    public void setReminderToEvent(Reminder reminder, Event event) throws ServiceFailureException;
    public void unSetReminderFromEvent(Reminder reminder, Event event) throws ServiceFailureException;

    public List<Event> findEventsByReminder(Reminder reminder) throws ServiceFailureException;
    public List<Reminder> findRemindersByEvent(Event event) throws ServiceFailureException;

}
