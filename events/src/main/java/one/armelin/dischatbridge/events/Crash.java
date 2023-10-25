package one.armelin.dischatbridge.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface Crash {
    Event<Crash.Crashed> CRASHED = EventFactory.createLoop();

    @FunctionalInterface
    interface Crashed {
        void crashed(String string, Throwable throwable);
    }
}
