package one.armelin.dischatbridge.utils;

import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import one.armelin.dischatbridge.DisChatBridge;

import java.util.Timer;
import java.util.TimerTask;

public class DiscordCommandOutput implements CommandSource {

    StringBuilder outputString = new StringBuilder();
    Thread outputThread = null;
    long lastOutputMillis = 0;

    @Override
    public void sendSystemMessage(Component message) {
        String messageString = message.getString();
        DisChatBridge.LOGGER.info(messageString);
        long currentOutputMillis = System.currentTimeMillis();
        if((outputString.length() + messageString.length()) > 2000) {
            DisChatBridge.textChannel.sendMessage(outputString).queue();
            outputString = new StringBuilder();
        }else{
            outputString.append("> ").append(messageString).append("\n");
        }
        if((currentOutputMillis - lastOutputMillis) > 50) {
            outputThread = new Thread(() -> new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    DisChatBridge.textChannel.sendMessage(outputString).queue();
                    outputString = new StringBuilder();
                }
            }, 51));
            outputThread.start();
        }
        lastOutputMillis = currentOutputMillis;
    }

    @Override
    public boolean acceptsSuccess() {
        return true;
    }

    @Override
    public boolean acceptsFailure() {
        return true;
    }

    @Override
    public boolean shouldInformAdmins() {
        return true;
    }
}
