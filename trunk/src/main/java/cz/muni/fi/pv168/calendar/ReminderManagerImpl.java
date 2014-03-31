package cz.muni.fi.pv168.calendar;

import cz.muni.fi.pv168.calendar.common.DBUtils;
import cz.muni.fi.pv168.calendar.common.IllegalEntityException;
import cz.muni.fi.pv168.calendar.common.ServiceFailureException;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Pavol Loffay, p.loffay@gmail.com on 3/7/14.
 */
public class ReminderManagerImpl implements ReminderManager {
    Logger log = LoggerFactory.getLogger(ReminderManagerImpl.class);

    public final DataSource dataSource;

    public ReminderManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createReminder(Reminder reminder) throws ServiceFailureException {
        log.debug("createReminder");
        checkDataSource();

        if (reminder == null) {
            throw new IllegalArgumentException("event is null");
        }

        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            connection.setAutoCommit(false);
            String insert = "INSERT INTO Reminder (start) VALUES (?)";
            st = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            st.setTimestamp(1, new Timestamp(reminder.getDate().getMillis()));

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, reminder, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            reminder.setId(id);
            connection.commit();
        } catch (SQLException ex) {
            log.debug("Error when inserting reminder into db", ex);
            throw new ServiceFailureException("Error when inserting reminder into db", ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, st);
        }
    }

    @Override
    public void updateReminder(Reminder reminder) throws ServiceFailureException{
        log.debug("updateReminder");
        checkDataSource();

        if (reminder.getId() == null) {
            throw new IllegalEntityException("reminder id is null");
        }
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            connection.setAutoCommit(false);
            String update = "UPDATE Reminder SET start = ? WHERE id = ?";
            st = connection.prepareStatement(update);
            st.setTimestamp(1, new Timestamp(reminder.getDate().getMillis()));
            st.setLong(2, reminder.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, reminder, false);
            connection.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating reminder in the db";
            log.debug(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, st);
        }
    }

    @Override
    public void deleteReminder(Reminder reminder) throws ServiceFailureException {
        log.debug("deleteReminder");
        checkDataSource();

        if (reminder == null) {
            throw new IllegalArgumentException("reminder is null");
        }
        if (reminder.getId() == null) {
            throw new IllegalEntityException("reminder id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            conn.setAutoCommit(false);
            String delete = "DELETE FROM Reminder WHERE id = ?";
            st = conn.prepareStatement(delete);

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, reminder, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting reminder from the db";
            log.debug(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Reminder findReminderById(Long id) throws ServiceFailureException {
        log.debug("findEventById {}", id);
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        String query = "SELECT * FROM Event WHERE id = ?";
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(query)) {
                st.setLong(1, id);

                ResultSet rs = st.executeQuery();
                if (rs.next())
                    return rowToReminder(rs);
                else
                    return null;
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Reminder> findAllReminders() throws ServiceFailureException {
        log.debug("findAllReminders");

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("select * from reminder")) {
                return executeQueryForMultipleEvents(st);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.<Reminder>emptyList();
    }

    @Override
    public List<Reminder> findRemindersByDate(DateTime date) throws
            ServiceFailureException {
        log.debug("findReminderByDate");
        List<Event> events = new ArrayList<Event>();

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("select * from reminder " +
                    "where start = ?")) {
                st.setTimestamp(1, new Timestamp(date.getMillis()));
                return executeQueryForMultipleEvents(st);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.<Reminder>emptyList();
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    public static List<Reminder> executeQueryForMultipleEvents(PreparedStatement st) throws
            SQLException {
        ResultSet rs = st.executeQuery();

        List<Reminder> reminders = new ArrayList<Reminder>();
        while (rs.next()) {
            reminders.add(rowToReminder(rs));
        }
        return reminders;
    }

    private static Reminder rowToReminder(ResultSet rs) throws SQLException {
        Reminder reminder = new Reminder();
        reminder.setId(rs.getLong("id"));
        reminder.setDate(new DateTime(rs.getTimestamp("start")));

        return reminder;
    }
}
