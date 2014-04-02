package cz.muni.fi.pv168.calendar.service.impl;

import cz.muni.fi.pv168.calendar.entity.Event;
import cz.muni.fi.pv168.calendar.common.ServiceFailureException;
import cz.muni.fi.pv168.calendar.service.EventManager;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;


import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by xloffay on 26.3.14.
 */
public class EventManagerSpringImpl implements EventManager {
    Logger log = LoggerFactory.getLogger(EventManagerSpringImpl.class);

    private JdbcTemplate jdbc;

    public EventManagerSpringImpl(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    @Override
    public void createEvent(Event event) throws ServiceFailureException {
        log.debug("Spring create event");

        SimpleJdbcInsert insertCustomer = new SimpleJdbcInsert(jdbc)
                .withTableName("event").usingGeneratedKeyColumns("id");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("title", event.getTitle())
                .addValue("description", event.getDescription())
                .addValue("location", event.getLocation())
                .addValue("startDate", event.getStartDate().toDate())
                .addValue("endDate", event.getEndDate().toDate())
                .addValue("repeat", event.getRepeat().ordinal())
                .addValue("repeatTimes", event.getRepeatTimes());

        Number id = insertCustomer.executeAndReturnKey(parameters);
        event.setId(id.longValue());
    }

    @Override
    public void updateEvent(Event event) throws ServiceFailureException {
        log.debug("Spring update Event");

        jdbc.update("UPDATE event set title = ?, description = ?, location = ?," +
                "startDate = ?, endDate = ?, repeat = ?, repeatTimes = ? where id = ?",
                event.getTitle(), event.getDescription(), event.getLocation(),
                event.getStartDate().toDate(), event.getEndDate().toDate(),
                event.getRepeat().ordinal(), event.getRepeatTimes(), event.getId());
    }

    @Override
    public void deleteEvent(Event event) throws ServiceFailureException {
        log.debug("Spring delete Event");

        jdbc.update("DELETE FROM event WHERE id=?", event.getId());
    }

    private RowMapper<Event> eventMapper = new RowMapper<Event>() {
        @Override
        public Event mapRow(ResultSet resultSet, int i) throws SQLException {
            Event event = new Event();
            event.setId(resultSet.getLong("id"));
            event.setTitle(resultSet.getString("title"));
            event.setDescription(resultSet.getString("description"));
            event.setLocation(resultSet.getString("location"));
            event.setStartDate(new DateTime(resultSet.getTimestamp("startDate")));
            event.setEndDate(new DateTime(resultSet.getTimestamp("endDate")));
            event.setRepeat(Event.Repeat.init(resultSet.getInt("repeat")));
            event.setRepeatTimes(resultSet.getInt("repeatTimes"));
            return event;
        }
    };

    @Override
    public List<Event> findEventById(Long id) throws ServiceFailureException {
        return jdbc.query("SELECT * FROM event WHERE id = ?",
            eventMapper, id);
    }

    @Override
    public List<Event> findAllEvents() throws ServiceFailureException {
        return jdbc.query("SELECT * FROM event", eventMapper);
    }

    @Override
    public List<Event> findEventByStartDate(DateTime date) throws ServiceFailureException {
        return jdbc.query("SELECT * FROM event WHERE event.startDate = ?", eventMapper,
         date.toDate());
    }
}
