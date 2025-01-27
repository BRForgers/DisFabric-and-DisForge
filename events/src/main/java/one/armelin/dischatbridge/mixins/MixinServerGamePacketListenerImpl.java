package one.armelin.dischatbridge.mixins;

import dev.architectury.event.CompoundEventResult;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import one.armelin.dischatbridge.events.ServerChat;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl {
    @Shadow public ServerPlayer player;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V"), method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;)V", cancellable = true)
    private void broadcastChatMessage(PlayerChatMessage message, CallbackInfo ci) {
        CompoundEventResult<Component> eventResult = ServerChat.RECEIVED.invoker().received(this.player, message.decoratedContent());
        if (eventResult.isPresent()) {
            player.server.getPlayerList().broadcastChatMessage(message.withUnsignedContent(eventResult.object()), this.player, ChatType.bind(ChatType.CHAT, player));
            ci.cancel();
        }
    }
}
