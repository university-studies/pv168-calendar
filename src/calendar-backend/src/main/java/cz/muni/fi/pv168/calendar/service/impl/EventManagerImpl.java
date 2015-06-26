package cz.muni.fi.pv168.calendar.service.impl;

import cz.muni.fi.pv168.calendar.common.DBUtils;
import cz.muni.fi.pv168.calendar.common.IllegalEntityException;
import cz.muni.fi.pv168.calendar.common.ServiceFailureException;
import cz.muni.fi.pv168.calendar.entity.Event;
import cz.muni.fi.pv168.calendar.service.EventManager;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by xloffay on 5.3.14.
 */
public class EventManagerImpl implements EventManager {
    private final static Logger log = LoggerFactory.getLogger(EventManagerImpl.class);

    private final DataSource dataSource;

    public EventManagerImpl() {
        dataSource = null;
    }

    public EventManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createEvent(Event event) throws ServiceFailureException {
        log.debug("createEvent");
        if (event == null) {
            throw new IllegalArgumentException("event is null");
        }

        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            connection.setAutoCommit(false);
            String insert = "INSERT INTO Event (id_user,title,description,location," +
                    "startDate, endDate, repeat, repeatTimes) VALUES (?,?,?,?,?,?,?,?)";
            st = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            st.setLong(1,event.getUserId());
            st.setString(2, event.getTitle());
            st.setString(3, event.getDescription());
            st.setString(4, event.getLocation());
            st.setTimestamp(5, new Timestamp(event.getStartDate().getMillis()));
            st.setTimestamp(6, new Timestamp(event.getEndDate().getMillis()));
            st.setInt(7, event.getRepeat().ordinal());
            st.setInt(8, event.getRepeatTimes());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, event, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            event.setId(id);
            connection.commit();
        } catch (SQLException ex) {
            log.debug("Error when inserting event into db", ex);
            throw new ServiceFailureException("Error when inserting event into db", ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, st);
        }
    }

    @Override
    public void updateEvent(Event event) throws ServiceFailureException {
        checkDataSource();

        if (event.getId() == null) {
            throw new IllegalEntityException("event id is null");
        }
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            connection.setAutoCommit(false);
            String update = "UPDATE Event SET id_user = ?, title = ?, description = ?, " +
                    "location = ?, startDate = ?, endDate = ?, repeat = ?, " +
                    "repeatTimes = ? WHERE id = ?";
            st = connection.prepareStatement(update);
            st.setLong(1,event.getUserId());
            st.setString(2, event.getTitle());
            st.setString(3, event.getDescription());
            st.setString(4, event.getLocation());
            st.setTimestamp(5, new Timestamp(event.getStartDate().getMillis()));
            st.setTimestamp(6, new Timestamp(event.getEndDate().getMillis()));
            st.setInt(7, event.getRepeat().ordinal());
            st.setInt(8,event.getRepeatTimes());
            st.setLong(9, event.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, event, false);
            connection.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating event in the db";
            log.debug(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, st);
        }
    }

    @Override
    public void deleteEvent(Event event) throws ServiceFailureException {
        checkDataSource();
        if (event == null) {
            throw new IllegalArgumentException("event is null");
        }
        if (event.getId() == null) {
            throw new IllegalEntityException("event id is null");
        }

        log.debug("deleting event= {}", event.getId());
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            conn.setAutoCommit(false);
            String delete = "DELETE FROM Event WHERE id = ?";
            st = conn.prepareStatement(delete);
            st.setLong(1, event.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, event, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting event from the db";
            log.debug(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Event findEventById(Long id) {
        log.debug("findEventById " + id);
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        String query = "SELECT * FROM Event WHERE id = ?";
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(query)) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                rs.next();
                return rowToEvent(rs);
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
    public List<Event> findAllEvents() throws ServiceFailureException {
        log.debug("findAllEvents");

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("select * from event")) {
               return executeQueryForMultipleEvents(st);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.<Event>emptyList();
 }

    @Override
    public List<Event> findEventByStartDate(DateTime date) throws
            ServiceFailureException {
        log.debug("findEventByStartDate");
        List<Event> events = new ArrayList<Event>();

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("select * from event where" +
                    " startDate = ?")) {
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
        return Collections.<Event>emptyList();
    }

    @Override
    public List<Event> findEventByStartDateAndUser(DateTime date, Long userId)
            throws ServiceFailureException {

        log.debug("findEventByStartDateAndUser");
        List<Event> events = new ArrayList<Event>();

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("select * from event where" +
                    " startDate = ? AND id_user  = ? ")) {
                st.setTimestamp(1, new Timestamp(date.getMillis()));
                st.setLong(2, userId);
                return executeQueryForMultipleEvents(st);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.<Event>emptyList();
    }

    public static List<Event> executeQueryForMultipleEvents(PreparedStatement st) throws
            SQLException {
        ResultSet rs = st.executeQuery();

        List<Event> events = new ArrayList<Event>();
        while (rs.next()) {
            events.add(rowToEvent(rs));
        }
        return events;
    }

    private static Event rowToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getLong("id"));
        event.setUserId(rs.getLong("id_user"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setEndDate(new DateTime(rs.getTimestamp("endDate")));
        event.setStartDate(new DateTime(rs.getTimestamp("startDate")));
        event.setLocation(rs.getString("location"));
        event.setRepeatTimes(rs.getInt("repeatTimes"));
        event.setRepeat(Event.Repeat.init(rs.getInt("repeat")));

        return event;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }
}
