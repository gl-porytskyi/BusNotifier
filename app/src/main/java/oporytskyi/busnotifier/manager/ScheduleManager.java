package oporytskyi.busnotifier.manager;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleksandr.porytskyi on 5/24/2016.
 */
public class ScheduleManager {
    private static String TAG = ScheduleManager.class.getName();

    private List<LocalTime> departures;
    private Period offset;

    public ScheduleManager() {
        prepareDepartures();
        offset = Period.minutes(10);
    }

    private void prepareDepartures() {
        departures = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("HH:mm");
        departures.add(LocalTime.parse("11:45", dateTimeFormatter));
        departures.add(LocalTime.parse("12:45", dateTimeFormatter));
        departures.add(LocalTime.parse("13:45", dateTimeFormatter));
        departures.add(LocalTime.parse("14:45", dateTimeFormatter));
        departures.add(LocalTime.parse("15:45", dateTimeFormatter));
        departures.add(LocalTime.parse("17:05", dateTimeFormatter));
        departures.add(LocalTime.parse("17:35", dateTimeFormatter));
        departures.add(LocalTime.parse("18:00", dateTimeFormatter));
        departures.add(LocalTime.parse("18:10", dateTimeFormatter));
        departures.add(LocalTime.parse("18:30", dateTimeFormatter));
        departures.add(LocalTime.parse("18:40", dateTimeFormatter));
        departures.add(LocalTime.parse("19:00", dateTimeFormatter));
        departures.add(LocalTime.parse("19:20", dateTimeFormatter));
        departures.add(LocalTime.parse("19:40", dateTimeFormatter));
        departures.add(LocalTime.parse("20:35", dateTimeFormatter));
        departures.add(LocalTime.parse("21:00", dateTimeFormatter));
    }

    public LocalDateTime getClosest() {
        LocalTime now = LocalTime.now(DateTimeZone.forID("Europe/Kiev"));
        for (LocalTime departure : departures) {
            LocalTime depart = departure.minus(offset);
            if (depart.isAfter(now)) {
                return LocalDateTime.now().withTime(departure.getHourOfDay(), departure.getMinuteOfHour(), 0, 0);
            }
        }
        return LocalDateTime.now().plusSeconds(10).plus(offset);
    }

    public Period getOffset() {
        return offset;
    }
}
