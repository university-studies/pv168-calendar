package cz.muni.fi.pv168.calendar;
/*
    TODO
    - pridat logger

*/
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by Mario on 5.3.2014.
 */
public class UserManagerImpl implements UserManager {
    private final DataSource dataSource;
    private final Logger log;

    public UserManagerImpl(DataSource ds, Logger log){
        if(ds == null) throw new IllegalArgumentException("Parameter ds is null");
        if(log == null) throw new IllegalArgumentException("Parameter log is null");
        dataSource = ds;
        this.log = log;
    }

    @Override
    public void createUser(User user) throws  CalendarException{
        if(user == null){
            log.error("cannot insert user (parameter is null Value)");
            throw new IllegalArgumentException("parameter user is null");
        }
        log.debug("create user ({})",user);
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement st = connection.prepareStatement("INSERT INTO Users (name,password,email) values(?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)){
                st.setString(1, user.getLogin());
                st.setString(2,user.getPassword());
                st.setString(3,user.getEmail());
                st.executeUpdate();
                try(ResultSet keys = st.getGeneratedKeys()){
                    if(keys.next()){
                        long id = keys.getLong(1);
                        user.setId(id);
                    }
                }
            }
        }catch (SQLException ex){
            log.error("cannot insert user",ex);
            throw new CalendarException("database insert failed",ex);
        }
    }

    @Override
    public void removeUser(User user) throws  CalendarException{
        log.debug("removeUser({})",user);
        if(user == null){
            log.error("cannot remove user (parameter is null)");
            throw new IllegalArgumentException("parameter user is null");
        }
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement st = connection.prepareStatement("DELETE FROM Users WHERE id=? AND name=? AND password=? AND email=?")){
                st.setLong(1,user.getId());
                st.setString(2,user.getLogin());
                st.setString(3,user.getPassword());
                st.setString(4,user.getEmail());
                int n = st.executeUpdate();
                if(n != 1){
                    throw new CalendarException("user wans't deleted from DB");
                }
            }
        }catch(SQLException ex){
            log.error("cannot remove user",ex);
            throw new CalendarException("cannot remove user",ex);
        }
    }

    @Override
    public User getUserById(long id) throws  CalendarException{
        log.debug("getUserById({})",id);
        if(id <= 0){
            log.error("cannot get user because of invalid id");
            throw  new IllegalArgumentException("User's id is less or equal zero");
        }
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement st = connection.prepareStatement("SELECT * FROM Users WHERE id = (?)")){
                st.setLong(1,id);
                try(ResultSet rs = st.executeQuery()){
                    if(rs.next()){
                        long userId = rs.getLong("id");
                        String name = rs.getString("name");
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        User user = new User(name,password,email);
                        user.setId(userId);
                        log.debug("retrieve user from database({})",user);
                        return user;
                    }else{
                        log.debug("id ({}) isn't in database",id);
                        return null;
                    }
                }
            }
        }catch(SQLException ex){
            log.error("cannot get user",ex);
            throw new CalendarException("Cannot get user from database",ex);
        }
    }

    @Override
    public void updateUser(User user) throws CalendarException{
        log.debug("updateUser({})",user);
        if(user == null){
            log.error("cannot update user (parameter is null)");
            throw new IllegalArgumentException("Parameter user is null");
        }
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement st = connection.prepareStatement("UPDATE users SET name=?, password=?, email=? WHERE id=?")){
                st.setString(1,user.getLogin());
                st.setString(2,user.getPassword());
                st.setString(3,user.getEmail());
                st.setLong(4,user.getId());
                int n = st.executeUpdate();
                if(n != 1){
                    throw new CalendarException("user wasn't updated with id " + user.getId());
                }
            }
        }catch (SQLException ex){
            log.error("cannot update user",ex);
            throw new CalendarException("cannot update user",ex);
        }
    }

    @Override
    public Collection<User> getAllUsers()throws  CalendarException{
        log.debug("getAllUsers");
        List<User> userList = new ArrayList<User>();
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement ps = connection.prepareStatement("SELECT * FROM Users")){
                ps.execute();
                try(ResultSet rs = ps.getResultSet()){
                    while (rs.next()){
                        long id = rs.getLong("id");
                        String name = rs.getString("name");
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        User user = new User(name,password,email);
                        user.setId(id);
                        userList.add(user);
                    }
                }
            }
        }catch(SQLException ex){
            log.error("cannot get all users",ex);
            throw new CalendarException("database select failed",ex); //treba doplnit message parameter
        }


        return userList;
    }
}
