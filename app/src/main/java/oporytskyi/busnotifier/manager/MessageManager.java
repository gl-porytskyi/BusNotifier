package oporytskyi.busnotifier.manager;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import oporytskyi.busnotifier.R;
import oporytskyi.busnotifier.activity.MainActivity;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * @author Oleksandr Porytskyi
 */
public class MessageManager {
    private Application application;

    public MessageManager(Application application) {
        this.application = application;
    }

    public void showNotification(DateTime dateTime, String directionName) {
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
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationId, mBuilder.build());
    }

    public void remove() {
    }
}
