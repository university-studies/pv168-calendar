package cz.muni.fi.pv168.calendar.service.impl;

import cz.muni.fi.pv168.calendar.common.ServiceFailureException;
import cz.muni.fi.pv168.calendar.entity.Event;
import cz.muni.fi.pv168.calendar.entity.User;
import cz.muni.fi.pv168.calendar.service.UsersEventManager;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by Mario Kudolani on 8.4.2014.
 */
public class UsersEventManagerImpl implements UsersEventManager {

    private DataSource ds;
    private Logger log = LoggerFactory.getLogger(UsersEventManagerImpl.class);

    public UsersEventManagerImpl(DataSource ds){
        if(ds == null) throw new IllegalArgumentException("parameter datasource is null");
        this.ds = ds;
    }

    @Override
    public Collection<Event> findEventsByUserId(long userId) throws ServiceFailureException {
        try(Connection connection = ds.getConnection()){
            try(PreparedStatement st = connection.prepareStatement("SELECT * FROM EVENT WHERE id_user=?")){
                st.setLong(1,userId);
                return EventManagerImpl.executeQueryForMultipleEvents(st);
            }
        }catch(SQLException e){
            throw new ServiceFailureException("cannot find event by user id",e);
        }
    }

    @Override
    public void addEventToUser(Event event, User user) throws ServiceFailureException {
        if(event == null || user == null) throw new IllegalArgumentException("parameter(-s) is(are) null");
        try(Connection c = ds.getConnection()){
            try(PreparedStatement st = c.prepareStatement("UPDATE event SET id_user=? WHERE id=?")){
                st.setLong(1,user.getId());
                st.setLong(2,event.getId());
                int n = st.executeUpdate();
                if(n != 1){
                    throw new ServiceFailureException("there is more than one updated rows(or zero rows): " + n);
                }
                event.setUserId(user.getId());
            }
        }catch(SQLException e){
            throw new ServiceFailureException("cannot add event to user",e);
        }

    }

    @Override
    public void removeEventFromUser(Event event) throws ServiceFailureException {
        if(event == null) throw new IllegalArgumentException("parameter event is null");
        try(Connection c = ds.getConnection()){
            try(PreparedStatement st = c.prepareStatement("UPDATE event SET id_user=? WHERE id=?")){
                st.setLong(1,-1);
                st.setLong(2,event.getId());
                int n = st.executeUpdate();
                if(n != 1){
                    throw new ServiceFailureException("there is more than one updated rows(or zero rows): " + n);
                }
                event.setUserId(-1);
            }
        }catch(SQLException e){
            throw new ServiceFailureException("cannot add event to user",e);
        }
    }
}
