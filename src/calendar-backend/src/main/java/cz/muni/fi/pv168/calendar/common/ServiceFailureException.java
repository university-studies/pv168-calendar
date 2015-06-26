package cz.muni.fi.pv168.calendar.common;

/**
 * Created by Mario on 19.3.2014.
 */
public class ServiceFailureException extends RuntimeException {
    public ServiceFailureException(String message) {
        super(message);
    }

    public ServiceFailureException(Throwable cause) {
        super(cause);
    }

    public ServiceFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
