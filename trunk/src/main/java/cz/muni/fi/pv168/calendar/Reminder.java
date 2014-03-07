package cz.muni.fi.pv168.calendar;

import java.util.Date;

/**
 * Created by Pavol Loffay, p.loffay@gmail.com on 3/7/14.
 */
public class Reminder {

    private Long id;
    private Date date;

    public Reminder() {
    }

    public Reminder(Date date, Long id) {
        this.date = date;
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
