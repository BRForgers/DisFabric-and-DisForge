package one.armelin.dischatbridge.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.CompoundEventResult;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.players.PlayerList;
import net.minecraft.network.chat.Component;
import one.armelin.dischatbridge.events.ServerChat;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ShrugCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("shrug").then(argument("message", MessageArgument.message()).executes(context -> {
                    CommandSourceStack serverCommandSource = context.getSource();
                    PlayerList playerManager = serverCommandSource.getServer().getPlayerList();
                    MessageArgument.resolveChatMessage(context, "message", signedMessage -> {
                        CompoundEventResult<Component> result = ServerChat.RECEIVED.invoker().received(
                                serverCommandSource.getPlayer(),
                                Component.nullToEmpty(signedMessage.decoratedContent().getString() + " ¯\\_(ツ)_/¯")
                        );
                        playerManager.broadcastChatMessage(
                                signedMessage.withUnsignedContent(result.object()),
                                serverCommandSource,
                                ChatType.bind(
                                        serverCommandSource.getPlayer() == null
                                        ? ChatType.SAY_COMMAND
                                        : ChatType.CHAT, serverCommandSource
                                )
                        );
                    });
                    return 1;
                }
        )));
        dispatcher.register(literal("shrug").executes(context -> {
                    CommandSourceStack serverCommandSource = context.getSource();
                    PlayerList playerManager = serverCommandSource.getServer().getPlayerList();
                    String raw = "¯\\_(ツ)_/¯";
                    CompoundEventResult<Component> result = ServerChat.RECEIVED.invoker().received(
                            serverCommandSource.getPlayer(),
                            Component.nullToEmpty(raw)
                    );
                    playerManager.broadcastChatMessage(
                            PlayerChatMessage.unsigned(
                                    serverCommandSource.getPlayer() == null
                                            ? Util.NIL_UUID
                                            : serverCommandSource.getPlayer().getUUID(),
                                    result.object().getString()
                            ),
                            serverCommandSource,
                            ChatType.bind(
                                    serverCommandSource.getPlayer() == null
                                    ? ChatType.SAY_COMMAND
                                    : ChatType.CHAT, serverCommandSource
                            )
                    );
                    return 1;
                }
        ));
    }
}
