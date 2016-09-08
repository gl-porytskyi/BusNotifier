package oporytskyi.busnotifier.dagger;

import android.app.Application;
import dagger.Module;
import dagger.Provides;
import oporytskyi.busnotifier.manager.VibratorManager;

import javax.inject.Singleton;

/**
 * @author Oleksandr Porytskyi
 */
@Module
public class TheModule {
    private Application application;

    public TheModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    VibratorManager vibratorService() {
        return new VibratorManager(application);
    }
}
