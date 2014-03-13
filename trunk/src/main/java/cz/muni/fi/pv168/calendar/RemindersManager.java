package cz.muni.fi.pv168.calendar;

import java.util.List;

/**
 * Created by Pavol Loffay, p.loffay@gmail.com on 3/7/14.
 */
public interface RemindersManager {

    public void setReminderToEvent(Reminder reminder, Event event);
    public void unSetReminderToEvent(Reminder reminder, Event event);

    public List<Event> findEventsByReminder(Reminder reminder);
    public List<Reminder> findRemindersByEvent(Event event);

}
