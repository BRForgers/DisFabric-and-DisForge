package one.armelin.dischatbridge.events;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public interface ServerChat {

    Event<Received> RECEIVED = EventFactory.createCompoundEventResult();

    @FunctionalInterface
    interface Received {
        CompoundEventResult<Component> received(@Nullable ServerPlayer player, Component component);
    }
}
