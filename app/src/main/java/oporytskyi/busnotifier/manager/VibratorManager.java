package oporytskyi.busnotifier.manager;


import android.app.Application;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

/**
 * @author Oleksandr Porytskyi
 */
public class VibratorManager {
    private static final String TAG = VibratorManager.class.getName();
    private static final long[] PATTERN = {0, 500, 500, 500, 100, 100, 100, 100, 100, 100, 100};

    private final Vibrator vibrator;

    public VibratorManager(Application application) {
        vibrator = (Vibrator) application.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void vibrate() {
        Log.d(TAG, "vibrate");
        vibrator.vibrate(PATTERN, 0);
    }

    public void stop() {
        Log.d(TAG, "stop vibrate");
        vibrator.cancel();
    }
}
