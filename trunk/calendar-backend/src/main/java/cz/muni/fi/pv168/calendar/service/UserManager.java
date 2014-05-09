package cz.muni.fi.pv168.calendar.service;

import cz.muni.fi.pv168.calendar.common.ServiceFailureException;
import cz.muni.fi.pv168.calendar.entity.User;

import java.util.Collection;

/**
 * Created by Mario on 5.3.2014.
 */
public interface UserManager {

    /**
     * The method insert user to database
     * @param user
     * @return created user
     * @throws cz.muni.fi.pv168.calendar.common.ServiceFailureException
     */
    void createUser(User user) throws ServiceFailureException;

    /**
     * The method remove user from database
     * @param user
     * @throws cz.muni.fi.pv168.calendar.common.ServiceFailureException
     */
    void removeUser(User user) throws ServiceFailureException;

    void removeUser(long id) throws ServiceFailureException;

    /**
     * The method update user in database
     * @param user
     * @throws cz.muni.fi.pv168.calendar.common.ServiceFailureException
     */
    void updateUser(User user) throws ServiceFailureException;

    /**
     * The method returns user from database by id
     * @param id user's id
     * @return user or null if user don't exist
     * @throws cz.muni.fi.pv168.calendar.common.ServiceFailureException
     */
    User getUserById(long id) throws ServiceFailureException;

    /**
     * The method returns collection of users
     * @return collection of users
     * @throws cz.muni.fi.pv168.calendar.common.ServiceFailureException
     */
    Collection<User> getAllUsers() throws ServiceFailureException;

    /**
     * The method returns user from database by user's login
     * @param login user's login
     * @return user or null if user don't exist
     * @throws ServiceFailureException
     */
    User getUserByLogin(String login) throws ServiceFailureException;
}
