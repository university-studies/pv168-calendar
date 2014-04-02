package cz.muni.fi.pv168.calendar.service;

import cz.muni.fi.pv168.calendar.entity.Reminder;
import cz.muni.fi.pv168.calendar.common.ServiceFailureException;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Pavol Loffay, p.loffay@gmail.com on 3/7/14.
 */
public interface ReminderManager {

    public void createReminder(Reminder reminder) throws ServiceFailureException;
    public void updateReminder(Reminder reminder) throws ServiceFailureException;
    public void deleteReminder(Reminder reminder) throws ServiceFailureException;

    public Reminder findReminderById(Long id) throws ServiceFailureException;
    public List<Reminder> findAllReminders() throws ServiceFailureException;
    public List<Reminder> findRemindersByDate(DateTime date) throws ServiceFailureException;
}
