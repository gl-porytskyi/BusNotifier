package oporytskyi.busnotifier.manager;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import oporytskyi.busnotifier.R;
import oporytskyi.busnotifier.TheApplication;
import oporytskyi.busnotifier.activity.MainActivity;
import oporytskyi.busnotifier.dto.Direction;
import oporytskyi.busnotifier.receiver.AlarmReceiver;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.TimeZone;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by oleksandr.porytskyi on 5/24/2016.
 */
public class ScheduleManager {
    private static String TAG = ScheduleManager.class.getName();

    private Application application;
    private DirectionManager directionManager;

    public ScheduleManager(DirectionManager directionManager) {
        this.directionManager = directionManager;
        application = TheApplication.get();
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

    public void schedule(LocalTime localTime, Direction direction) {
        DateTime dateTime = localTime.toDateTimeToday(directionManager.getDateTimeZone()).toDateTime(DateTimeZone.forTimeZone(TimeZone.getDefault()));
        setAlarm(dateTime.minus(direction.getBeforehand()));
        showNotification(dateTime, direction.getName());
//        Snackbar.make(departuresArea, "ScheduledFragment!", Snackbar.LENGTH_LONG)                .setAction("Action", null).show();
    }

    private void setAlarm(DateTime dateTime) {
        Intent intent = new Intent(application, MainActivity.class);
        PendingIntent activity = PendingIntent.getActivity(application, 0, intent, 0);

        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(dateTime.getMillis(), activity);
        AlarmManager alarmManager = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);

        PendingIntent broadcast = PendingIntent.getBroadcast(application, 0, new Intent(AlarmReceiver.ALARM_ACTION), 0);
        alarmManager.setAlarmClock(alarmClockInfo, broadcast);
    }

    private void showNotification(DateTime dateTime, String directionName) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.shortTime();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(application)
                .setSmallIcon(R.drawable.ic_dialog_time)
                .setContentTitle(directionName)
                .setContentText("Departure at " + dateTime.toString(dateTimeFormatter));

        Intent intent = new Intent(application, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(application, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setOngoing(true);

        int mNotificationId = 1;
        NotificationManager notificationManager = (NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationId, mBuilder.build());
    }
}
