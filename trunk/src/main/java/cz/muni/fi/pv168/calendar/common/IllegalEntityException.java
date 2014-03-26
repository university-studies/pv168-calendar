package cz.muni.fi.pv168.calendar.common;

/**
 * Created by Pavol Loffay, p.loffay@gmail.com on 3/25/14.
 */
public class IllegalEntityException extends RuntimeException {
    public IllegalEntityException() {
    }

    public IllegalEntityException(String message) {
        super(message);
    }

    public IllegalEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalEntityException(Throwable cause) {
        super(cause);
    }
}
