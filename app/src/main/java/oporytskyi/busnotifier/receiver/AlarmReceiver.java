package oporytskyi.busnotifier.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import oporytskyi.busnotifier.TheApplication;
import oporytskyi.busnotifier.activity.MainActivity;
import oporytskyi.busnotifier.manager.VibratorManager;

import javax.inject.Inject;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String ALARM_ACTION = "oporytskyi.busnotifier.receiver.AlarmReceiver.ALARM_ACTION";

    private static final String TAG = AlarmReceiver.class.getName();

    @Inject
    VibratorManager vibratorManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Alarm goes off");

        ((TheApplication) context.getApplicationContext()).getTheComponent().inject(this);

        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);

//        playSound(context);
        vibratorManager.vibrate();
    }

    private void playSound(Context context) {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        Ringtone r = RingtoneManager.getRingtone(context, alert);
        r.play();
    }
}
