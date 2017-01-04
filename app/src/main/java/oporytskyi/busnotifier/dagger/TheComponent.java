package oporytskyi.busnotifier.dagger;

import dagger.Component;
import oporytskyi.busnotifier.activity.MainActivity;
import oporytskyi.busnotifier.fragment.DirectionFragment;
import oporytskyi.busnotifier.fragment.ScheduledFragment;
import oporytskyi.busnotifier.manager.ScheduleManager;
import oporytskyi.busnotifier.receiver.AlarmReceiver;

import javax.inject.Singleton;

/**
 * @author Oleksandr Porytskyi
 */
@Component(modules = TheModule.class)
@Singleton
public interface TheComponent {
    void inject(MainActivity o);

    void inject(AlarmReceiver o);

    void inject(ScheduledFragment o);

    void inject(DirectionFragment o);

    void inject(ScheduleManager o);
}
