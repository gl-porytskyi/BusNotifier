package oporytskyi.busnotifier.manager;

import oporytskyi.busnotifier.dto.Direction;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import javax.inject.Inject;
import java.util.TimeZone;

/**
 * Created by oleksandr.porytskyi on 5/24/2016.
 */
public class ScheduleManager {
    private static String TAG = ScheduleManager.class.getName();

    @Inject
    TimerManager timerManager;
    @Inject
    MessageManager messageManager;
    @Inject
    DirectionManager directionManager;

    private DateTime alarm;
    private DateTime departure;

    public boolean isEligable(LocalTime localTime, Period beforehand) {
        LocalTime now = LocalTime.now(directionManager.getDateTimeZone());
        return localTime.minus(beforehand).isAfter(now);
    }

    public LocalTime getClosest(Direction direction) {
        for (LocalTime departure : direction.getDepartures()) {
            if (isEligable(departure, direction.getBeforehand())) {
                return departure;
            }
        }
        return null;
    }

    public void schedule(LocalTime localTime, Direction direction) {
        departure = localTime.toDateTimeToday(directionManager.getDateTimeZone()).toDateTime(DateTimeZone.forTimeZone(TimeZone.getDefault()));
        alarm = departure.minus(direction.getBeforehand());
        timerManager.setAlarm(alarm);
        messageManager.showNotification(departure, direction.getName());
    }

    public DateTime getAlarm() {
        return alarm;
    }

    public DateTime getDeparture() {
        return departure;
    }

    public void cancel() {
        departure = null;
        timerManager.cancel();
        messageManager.remove();
    }
}
