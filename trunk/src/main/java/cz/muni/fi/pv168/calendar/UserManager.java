package cz.muni.fi.pv168.calendar;

import java.util.Collection;

/**
 * Created by Mario on 5.3.2014.
 */
public interface UserManager {

    User createUser(User user);

    void removeUser(User user);

    User getUserById(int id);

    void updateUser(User user);

    Collection<User> getAllUsers();
}
