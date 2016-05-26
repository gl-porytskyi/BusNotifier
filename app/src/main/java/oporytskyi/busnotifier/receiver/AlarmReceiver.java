package oporytskyi.busnotifier.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String ALARM_ACTION = "oporytskyi.busnotifier.receiver.AlarmReceiver.ALARM_ACTION";

    private static final String TAG = AlarmReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "ALARM!!!");
        playSound(context);
        vibrate(context);
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

    private void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{0, 500, 500, 500, 100, 100, 100, 100, 100, 100, 100}, 0);
    }
}
