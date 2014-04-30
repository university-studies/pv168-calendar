package cz.muni.fi.pv168.calendar.service.impl;

import cz.muni.fi.pv168.calendar.entity.User;
import cz.muni.fi.pv168.calendar.common.ServiceFailureException;
import cz.muni.fi.pv168.calendar.service.UserManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by Mario on 1.4.2014.
 */
public class UserManagerSpringImpl implements UserManager {

    private JdbcTemplate jdbc;

    public UserManagerSpringImpl(DataSource dataSource){
        this.jdbc = new JdbcTemplate(dataSource);
    }

    @Override
    public void createUser(User user) throws ServiceFailureException {
        SimpleJdbcInsert insertUser = new SimpleJdbcInsert(jdbc).withTableName("users").usingGeneratedKeyColumns("id");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name",user.getLogin())
                .addValue("password",user.getPassword())
                .addValue("email",user.getEmail());

        Number id = insertUser.executeAndReturnKey(parameters);
        user.setId(id.longValue());
    }

    @Override
    public void removeUser(User user) throws ServiceFailureException {
        removeUser(user.getId());
    }

    @Override
    public void removeUser(long id) throws ServiceFailureException {
        jdbc.update("DELETE FROM users WHERE id=?",id);
    }

    @Override
    public void updateUser(User user) throws ServiceFailureException {
        jdbc.update("UPDATE users SET name=?,password=?,email=? WHERE id=?",
                user.getLogin(),user.getPassword(),user.getEmail(),user.getId());

    }

    private RowMapper<User> usersMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            User user = new User(resultSet.getString("name"),resultSet.getString("password"),resultSet.getString("email"));
            user.setId(resultSet.getLong("id"));
            return user;
        }
    };


    @Override
    public User getUserById(long id) throws ServiceFailureException {
        return jdbc.queryForObject("SELECT id,name,password,email FROM users WHERE id=?",usersMapper,id);
    }

    @Override
    public Collection<User> getAllUsers() throws ServiceFailureException {
        return jdbc.query("SELECT id,name,password,email FROM users",usersMapper);
    }

    @Override
    public User getUserByLogin(String login) throws ServiceFailureException {
        return jdbc.queryForObject("SELECT id,name,password,email FROM Users WHERE name=?",usersMapper,login);
    }
}
