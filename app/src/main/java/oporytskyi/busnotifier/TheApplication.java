package oporytskyi.busnotifier;

import android.app.Application;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatDelegate;
import oporytskyi.busnotifier.manager.DirectionManager;
import oporytskyi.busnotifier.manager.ScheduleManager;

/**
 * Created by oleksandr.porytskyi on 5/24/2016.
 */
public class TheApplication extends Application {
    private static TheApplication instance;

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    private AssetManager assetManager;
    private DirectionManager directionManager;
    private ScheduleManager scheduleManager;

    public static TheApplication get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        assetManager = getAssets();

        directionManager = new DirectionManager();
        scheduleManager = new ScheduleManager(directionManager);
    }

    public DirectionManager getDirectionManager() {
        return directionManager;
    }

    public ScheduleManager getScheduleManager() {
        return scheduleManager;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}
