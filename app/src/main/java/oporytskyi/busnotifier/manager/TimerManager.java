package oporytskyi.busnotifier.manager;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import oporytskyi.busnotifier.activity.MainActivity;
import oporytskyi.busnotifier.receiver.AlarmReceiver;
import org.joda.time.DateTime;

/**
 * @author Oleksandr Porytskyi
 */
public class TimerManager {
    private final PendingIntent broadcast;
    private AlarmManager alarmManager;

    private Application application;

    public TimerManager(Application application) {
        this.application = application;
        broadcast = PendingIntent.getBroadcast(this.application, 0, new Intent(AlarmReceiver.ALARM_ACTION), 0);
        alarmManager = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarm(DateTime dateTime) {
        Intent intent = new Intent(application, MainActivity.class);
        PendingIntent activity = PendingIntent.getActivity(application, 0, intent, 0);

        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(dateTime.getMillis(), activity);

        alarmManager.setAlarmClock(alarmClockInfo, broadcast);
    }

    public void cancel() {
        alarmManager.cancel(broadcast);
    }
}
