package cz.muni.fi.pv168.calendar;

import cz.muni.fi.pv168.calendar.common.ServiceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by xloffay on 26.3.14.
 */
public class EventManagerImplSpring implements EventManager{

    private JdbcTemplate jdbc;

    public EventManagerImplSpring(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }


    @Override
    public void createEvent(Event event) throws ServiceFailureException {
        SimpleJdbcInsert insertCustomer = new SimpleJdbcInsert(jdbc)
                .withTableName("event").usingGeneratedKeyColumns("id");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("title", event.getTitle())
                .addValue("address", event.getDescription())
                .addValue("phone", event.getLocation());

        Number id = insertCustomer.executeAndReturnKey(parameters);
        event.setId(id.longValue());
    }

    @Override
    public void updateEvent(Event event) throws ServiceFailureException {

    }

    @Override
    public void deleteEvent(Event event) throws ServiceFailureException {
        jdbc.update("DELETE FROM event WHERE id=?", event.getId());
    }

    @Override
    public Event findEventById(Long id) throws ServiceFailureException {
        return null;
    }

    private RowMapper<Event> customerMapper = new RowMapper<Event>() {
        @Override
        public Event mapRow(ResultSet resultSet, int i) throws SQLException {
            Event event = new Event();
            event.setId(resultSet.getLong("id"));
            event.setTitle(resultSet.getString("title"));
            event.setDescription(resultSet.getString("desctiption"));
            event.setLocation(resultSet.getString("location"));
            return event;
        }
    };

    @Override
    public List<Event> findAllEvents() throws ServiceFailureException {
        return null;
    }

    @Override
    public List<Event> findEventByStartDate(Date date) throws ServiceFailureException {
        return null;
    }
}
