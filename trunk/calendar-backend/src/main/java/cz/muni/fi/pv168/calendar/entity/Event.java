package cz.muni.fi.pv168.calendar.entity;

import org.joda.time.DateTime;

/**
 * Created by xloffay on 5.3.14.
 */
public class Event {
    private Long id;
    private String title;
    private String description;
    private String location;
    private DateTime startDate;
    private DateTime endDate;

    private Repeat repeat;
    private Integer repeatTimes;

    public enum Repeat {
      ONCE,
      DAILY,
      WEEKLY,
      MONTHLY,
      YEARLY,
      UNDEF;

        public static Repeat init(Integer x) {
            if (x == null)
                return UNDEF;

            switch (x) {
                case 0:
                    return ONCE;
                case 1:
                    return DAILY;
                case 2:
                    return WEEKLY;
                case 3:
                    return MONTHLY;
                case 4:
                    return YEARLY;
                default:
                    return UNDEF;
            }
        }
    };

    public Event() {
        repeat = Repeat.UNDEF;
        repeatTimes = 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public Repeat getRepeat() {
        return repeat;
    }

    public void setRepeat(Repeat repeat) {
        this.repeat = repeat;
    }

    public Integer getRepeatTimes() {
        return repeatTimes;
    }

    public void setRepeatTimes(Integer repeatTimes) {
        this.repeatTimes = repeatTimes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;

        Event event = (Event) o;

        if (!id.equals(event.id)) return false;

        return true;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
