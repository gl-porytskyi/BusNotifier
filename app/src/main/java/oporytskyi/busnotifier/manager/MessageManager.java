package oporytskyi.busnotifier.manager;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import oporytskyi.busnotifier.R;
import oporytskyi.busnotifier.activity.MainActivity;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Oleksandr Porytskyi
 */
public class MessageManager {
    private final int mNotificationId = 1;

    private Application application;
    private NotificationManager notificationManager;

    public MessageManager(Application application) {
        this.application = application;
        notificationManager = (NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);
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

        notificationManager.notify(mNotificationId, mBuilder.build());
    }

    public void remove() {
        notificationManager.cancel(mNotificationId);
    }
}
