package oporytskyi.busnotifier;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;
import oporytskyi.busnotifier.dagger.DaggerTheComponent;
import oporytskyi.busnotifier.dagger.TheComponent;
import oporytskyi.busnotifier.dagger.TheModule;

/**
 * @author Oleksandr Porytskyi
 */
public class TheApplication extends Application {
    private static TheApplication instance;

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    private TheComponent theComponent;

    @Deprecated
    public static TheApplication get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        theComponent = DaggerTheComponent.builder()
                .theModule(new TheModule(this))
                .build();
    }

    public TheComponent getTheComponent() {
        return theComponent;
    }
}
