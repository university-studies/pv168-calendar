package cz.muni.fi.pv168.calendar;

import java.util.Date;
import java.util.List;

/**
 * Created by Pavol Loffay, p.loffay@gmail.com on 3/7/14.
 */
public interface ReminderManager {

    public void createReminder(Reminder reminder);
    public void updateReminder(Reminder reminder);
    public void deleteReminder(Reminder reminder);

    public Reminder findReminderById(Long id);
    public List<Reminder> findAllReminders();
    public List<Reminder> findRemindersByDate(Date date);
}
