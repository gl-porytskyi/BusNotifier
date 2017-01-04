package oporytskyi.busnotifier.dagger;

import android.app.Application;
import dagger.Module;
import dagger.Provides;
import oporytskyi.busnotifier.manager.DirectionManager;
import oporytskyi.busnotifier.manager.MessageManager;
import oporytskyi.busnotifier.manager.ScheduleManager;
import oporytskyi.busnotifier.manager.TimerManager;
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

    @Provides
    @Singleton
    TimerManager timerManager() {
        return new TimerManager(application);
    }

    @Provides
    @Singleton
    MessageManager messageManager() {
        return new MessageManager(application);
    }

    @Provides
    @Singleton
    DirectionManager directionManager() {
        return new DirectionManager(application);
    }

    @Provides
    @Singleton
    ScheduleManager scheduleManager() {
        return new ScheduleManager(application);
    }
}
