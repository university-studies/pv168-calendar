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

    public Reminder(DateTime date) {
        this.start = date;
    }

    public DateTime getDate() {
        return start;
    }

    public void setDate(DateTime date) {
        this.start = date;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reminder)) return false;

        Reminder reminder = (Reminder) o;

        if (!id.equals(reminder.id)) return false;
        if (start != null ? !start.equals(reminder.start) : reminder.start != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (start != null ? start.hashCode() : 0);
        return result;
    }
}
