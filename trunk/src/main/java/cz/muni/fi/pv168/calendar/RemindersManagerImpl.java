package cz.muni.fi.pv168.calendar;

import java.util.List;

/**
 * Created by Pavol Loffay, p.loffay@gmail.com on 3/7/14.
 */
public class RemindersManagerImpl implements RemindersManager {
    @Override
    public void setReminderToEvent(Reminder reminder, Event event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void unSetReminderToEvent(Reminder reminder, Event event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Event> findEventsByReminder(Reminder reminder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Reminder> findRemindersByEvent(Event event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
