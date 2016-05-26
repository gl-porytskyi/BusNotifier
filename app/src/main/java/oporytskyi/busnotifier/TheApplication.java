package oporytskyi.busnotifier;

import android.app.Application;
import oporytskyi.busnotifier.manager.ScheduleManager;

/**
 * Created by oleksandr.porytskyi on 5/24/2016.
 */
public class TheApplication extends Application {
    private static TheApplication instance;

    private ScheduleManager scheduleManager;

    public static TheApplication get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        scheduleManager = new ScheduleManager();
    }

    public ScheduleManager getScheduleManager() {
        return scheduleManager;
    }
}
