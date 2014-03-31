package cz.muni.fi.pv168.calendar;

import org.joda.time.DateTime;

/**
 * Created by Pavol Loffay, p.loffay@gmail.com on 3/7/14.
 */
public class Reminder {

    private Long id;
    private DateTime start;

    public Reminder() {
    }

    public Reminder(DateTime date, Long id) {

        this.start = date;
        this.id = id;
    }

    public DateTime getDate() {
        return start;
    }

    public void setDate(DateTime date) {
        this.start = start;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
