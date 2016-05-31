package oporytskyi.busnotifier.dto;

import org.joda.time.LocalTime;
import org.joda.time.Period;

import java.util.List;

/**
 * Created by oleksandr.porytskyi on 5/26/2016.
 */
public class Direction {
    private String name;
    private List<LocalTime> departures;
    private Period beforehand;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LocalTime> getDepartures() {
        return departures;
    }

    public void setDepartures(List<LocalTime> departures) {
        this.departures = departures;
    }

    public Period getBeforehand() {
        if (beforehand == null) {
            beforehand = Period.minutes(0);
        }
        return beforehand;
    }

    public void setBeforehand(Period beforehand) {
        this.beforehand = beforehand;
    }
}
