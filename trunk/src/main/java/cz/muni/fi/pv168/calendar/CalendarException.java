package cz.muni.fi.pv168.calendar;

/**
 * Created by Mario on 19.3.2014.
 */
public class CalendarException extends Exception {
    public CalendarException(String message) {
        super(message);
    }

    public CalendarException(String message, Throwable cause) {
        super(message, cause);
    }
}
