package oporytskyi.busnotifier.manager;

import oporytskyi.busnotifier.dto.Direction;
import org.joda.time.LocalTime;
import org.joda.time.Period;

/**
 * Created by oleksandr.porytskyi on 5/24/2016.
 */
public class ScheduleManager {
    private static String TAG = ScheduleManager.class.getName();

    private DirectionManager directionManager;

    public ScheduleManager(DirectionManager directionManager) {
        this.directionManager = directionManager;
    }

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
}
