package oporytskyi.busnotifier.dto;

import org.joda.time.LocalTime;

import java.util.List;

/**
 * Created by oleksandr.porytskyi on 5/26/2016.
 */
public class Direction {
    private String name;
    private List<LocalTime> departures;

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
}
