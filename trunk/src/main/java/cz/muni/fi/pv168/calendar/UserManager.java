package cz.muni.fi.pv168.calendar;

import java.util.Collection;

/**
 * Created by Mario on 5.3.2014.
 */
public interface UserManager {

    /**
     * The method insert user to database
     * @param user
     * @return created user
     * @throws CalendarException
     */
    void createUser(User user) throws CalendarException;

    /**
     * The method remove user from database
     * @param user
     * @throws CalendarException
     */
    void removeUser(User user) throws CalendarException;

    /**
     * The method update user in database
     * @param user
     * @throws CalendarException
     */
    void updateUser(User user) throws CalendarException;

    /**
     * The method returns user from database by id
     * @param id user's id
     * @return user or null if user don't exist
     * @throws CalendarException
     */
    User getUserById(long id) throws  CalendarException;

    /**
     * The method returns collection of users
     * @return collection of users
     * @throws CalendarException
     */
    Collection<User> getAllUsers() throws CalendarException;
}
