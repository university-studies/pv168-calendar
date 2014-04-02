package cz.muni.fi.pv168.calendar.service;

import cz.muni.fi.pv168.calendar.entity.Event;
import cz.muni.fi.pv168.calendar.entity.User;

import java.util.Collection;

/**
 * Created by Mario Kudolani on 1.4.2014.
 */
public interface UsersEventManager {

    Collection<Event> findEventsByUserId(long userId);

    void addEventToUser(Event event,User user);

    void removeEventFromUser(Event event,User user);
}
