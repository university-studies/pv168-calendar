package cz.muni.fi.pv168.calendar.service.impl;

import cz.muni.fi.pv168.calendar.entity.Event;
import cz.muni.fi.pv168.calendar.entity.Reminder;
import cz.muni.fi.pv168.calendar.common.DBUtils;
import cz.muni.fi.pv168.calendar.common.ServiceFailureException;
import cz.muni.fi.pv168.calendar.service.RemindersManager;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Pavol Loffay, p.loffay@gmail.com on 3/7/14.
 */
public class RemindersManagerImpl implements RemindersManager {
    Logger log = LoggerFactory.getLogger(ReminderManagerImpl.class);

    DataSource dataSource;

    public RemindersManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void setReminderToEvent(Reminder reminder, Event event) {
        log.debug("setReminderToEvent");
        checkDataSource();

        if (reminder == null) {
            throw new IllegalArgumentException("reminder is null");
        }
        if (reminder.getId() == null) {
            throw new IllegalArgumentException("reminder id is null");
        }
        if (event == null) {
            throw new IllegalArgumentException("event is null");
        }
        if (event.getId() == null) {
            throw new IllegalArgumentException("event id is null");
        }

        Connection connection = null;
        PreparedStatement updateSt = null;
        try {
            connection = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            connection.setAutoCommit(false);

            updateSt = connection.prepareStatement(
                    "UPDATE Reminder SET id_event = ? WHERE id = ? AND id_event IS " +
                            "NULL");
            updateSt.setLong(1, event.getId());
            updateSt.setLong(2, reminder.getId());
            int count = updateSt.executeUpdate();
            if (count == 0) {
                throw new IllegalStateException("Reminder not found or " +
                        "already in event");
            }
            DBUtils.checkUpdatesCount(count, event, false);
            connection.commit();
        } catch (SQLException ex) {
            String msg = "Error when setting reminder to event";
            log.debug(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, updateSt);
        }
    }

    @Override
    public void unSetReminderFromEvent(Reminder reminder, Event event) {
        log.debug("unSetReminderToEvent");
        checkDataSource();

        if (reminder == null) {
            throw new IllegalArgumentException("reminder is null");
        }
        if (reminder.getId() == null) {
            throw new IllegalArgumentException("reminder id is null");
        }
        if (event == null) {
            throw new IllegalArgumentException("event is null");
        }
        if (event.getId() == null) {
            throw new IllegalArgumentException("event id is null");
        }

        Connection connection = null;
        PreparedStatement updateSt = null;
        try {
            connection = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            connection.setAutoCommit(false);

            updateSt = connection.prepareStatement(
                    "UPDATE Reminder SET id_event = NULL WHERE id = ? AND id_event = ?");
            updateSt.setLong(1, reminder.getId());
            updateSt.setLong(2, event.getId());
            int count = updateSt.executeUpdate();
            if (count == 0) {
                throw new IllegalStateException("Reminder not found or " +
                        "already in event");
            }
            DBUtils.checkUpdatesCount(count, event, false);
            connection.commit();
        } catch (SQLException ex) {
            String msg = "Error when unSetting reminder to event";
            log.debug(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, updateSt);
        }
    }

    @Override
    public List<Event> findEventsByReminder(Reminder reminder) {
        log.debug("findEventsByReminder");
        checkDataSource();

        if (reminder == null) {
            throw new IllegalArgumentException("reminder is null");
        }
        if (reminder.getId() == null) {
            throw new IllegalArgumentException("reminder id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT Event.id, title, description, location, startDate,endDate " +
                            "FROM Event JOIN Reminder ON Event.id = Reminder.id_event " +
                            "WHERE Reminder.id = ?");
            st.setLong(1, reminder.getId());
            return EventManagerImpl.executeQueryForMultipleEvents(st);
        } catch (SQLException ex) {
            String msg = "Error when trying to find events for reminder " + reminder;
            log.debug(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Reminder> findRemindersByEvent(Event event) {
        log.debug("findReminderssByEvent");
        checkDataSource();

        if (event == null) {
            throw new IllegalArgumentException("event is null");
        }
        if (event.getId() == null) {
            throw new IllegalArgumentException("event id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT Reminder.id, start " +
                            "FROM Event JOIN Reminder ON Event.id = Reminder.id_event " +
                            "WHERE Event.id = ?");
            st.setLong(1, event.getId());
            return ReminderManagerImpl.executeQueryForMultipleReminders(st);
        } catch (SQLException ex) {
            String msg = "Error when trying to find events for reminder " + event;
            log.debug(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }
}
