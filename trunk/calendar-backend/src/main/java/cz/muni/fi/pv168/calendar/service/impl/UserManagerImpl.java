package cz.muni.fi.pv168.calendar.service.impl;
/*
    TODO
    - pridat logger

*/
import cz.muni.fi.pv168.calendar.entity.User;
import cz.muni.fi.pv168.calendar.common.ServiceFailureException;
import cz.muni.fi.pv168.calendar.service.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final Logger log = LoggerFactory.getLogger(UserManagerImpl.class);

    public UserManagerImpl(DataSource ds){
        if(ds == null) throw new IllegalArgumentException("Parameter ds is null");
        if(log == null) throw new IllegalArgumentException("Parameter log is null");
        dataSource = ds;
        //this.log = log;
    }

    @Override
    public void createUser(User user) throws ServiceFailureException {
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
            throw new ServiceFailureException("database insert failed",ex);
        }
    }

    @Override
    public void removeUser(User user) throws ServiceFailureException {
        removeUser(user.getId());
    }

    @Override
    public void removeUser(long id) throws ServiceFailureException {
        log.debug("removeUser({})",id);
        if(id <= 0){
            log.error("cannot remove user (invalid id)");
            throw new IllegalArgumentException("parameter user is null");
        }
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement st = connection.prepareStatement("DELETE FROM Users WHERE id=?")){
                st.setLong(1,id);
                int n = st.executeUpdate();
                if(n != 1){
                    throw new ServiceFailureException("user wans't deleted from DB");
                }
            }
        }catch(SQLException ex){
            log.error("cannot remove user",ex);
            throw new ServiceFailureException("cannot remove user",ex);
        }
    }

    @Override
    public User getUserById(long id) throws ServiceFailureException {
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
                        user.setHashedPassword(password);
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
            throw new ServiceFailureException("Cannot get user from database",ex);
        }
    }

    @Override
    public void updateUser(User user) throws ServiceFailureException {
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
                    throw new ServiceFailureException("user wasn't updated with id " + user.getId());
                }
            }
        }catch (SQLException ex){
            log.error("cannot update user",ex);
            throw new ServiceFailureException("cannot update user",ex);
        }
    }

    @Override
    public Collection<User> getAllUsers()throws ServiceFailureException {
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
                        user.setHashedPassword(password);
                        user.setId(id);
                        userList.add(user);
                    }
                }
            }
        }catch(SQLException ex){
            log.error("cannot get all users",ex);
            throw new ServiceFailureException("database select failed",ex); //treba doplnit message parameter
        }


        return userList;
    }

    @Override
    public User getUserByLogin(String login) throws ServiceFailureException {
        if(login == null){
            throw new ServiceFailureException("Parameter login is null");
        }
        log.debug("getUserByLogin ({})",login);
        try(Connection connection = this.dataSource.getConnection()){
            try(PreparedStatement st = connection.prepareStatement("SELECT id,name,password,email FROM Users WHERE name=?")){
                st.setString(1,login);
                try(ResultSet rs = st.executeQuery()){
                    if(rs.next()){
                        long userId = rs.getLong("id");
                        String name = rs.getString("name");
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        User user = new User(name,password,email);
                        user.setHashedPassword(password);
                        user.setId(userId);
                        log.debug("retrieve user from database({})",user);
                        return user;
                    }else{
                        log.debug("login ({}) isn't in database",login);
                        return null;
                    }
                }
            }
        }catch (SQLException ex){
            throw new ServiceFailureException("Cannot get user from database",ex);
        }
    }
}
