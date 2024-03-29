package one.armelin.dischatbridge.utils;

import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Member;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Tuple;
import one.armelin.dischatbridge.DisChatBridge;

public class Utils {

    public static Tuple<String, String> convertMentionsFromNames(String message) {

        if (!message.contains("@")) return new Tuple<>(message, message);

        List<String> messageList = Arrays.asList(message.split("@\\S+"));
        if(messageList.isEmpty()) {
            messageList = new ArrayList<>();
            messageList.add("");
        }

        StringBuilder discordString = new StringBuilder(), mcString = new StringBuilder();
        Pattern pattern = Pattern.compile("@\\S+");
        Matcher matcher = pattern.matcher(message);

        int x = 0;
        while(matcher.find()) {
            Member member = null;
            for (Member m : DisChatBridge.textChannel.getGuild().getMemberCache()) {
                String name = matcher.group().substring(1);
                if (m.getUser().getName().equalsIgnoreCase(name) || (m.getNickname() != null && m.getNickname().equalsIgnoreCase(name))) {
                    member = m;
                }
            }
            if (member == null) {
                discordString.append(messageList.get(x)).append(matcher.group());
                mcString.append(messageList.get(x)).append(matcher.group());
            } else {
                discordString.append(messageList.get(x)).append(member.getAsMention());
                mcString.append(messageList.get(x)).append(ChatFormatting.YELLOW.toString()).append("@").append(member.getEffectiveName()).append(ChatFormatting.WHITE.toString());
            }
            x++;
        }
        if(x < messageList.size()) {
            discordString.append(messageList.get(x));
            mcString.append(messageList.get(x));
        }
        return new Tuple<>(discordString.toString(), mcString.toString());
    }

    public static String sanitize(String text, boolean always) {
        return text
                .replace("§", (DisChatBridge.config.texts.removeVanillaFormattingFromDiscord || always) ? "&" : "§")
                .replace("\n", (DisChatBridge.config.texts.removeLineBreakFromDiscord || always) ? " " : "\n");
    }
}
