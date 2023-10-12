package one.armelin.dischatbridge.listeners;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import one.armelin.dischatbridge.DisChatBridge;
import one.armelin.dischatbridge.events.ServerChat;
import one.armelin.dischatbridge.utils.MarkdownParser;
import one.armelin.dischatbridge.utils.Utils;
import org.jetbrains.annotations.NotNull;


public class MinecraftEventListener {

    public static final MediaType JSON = MediaType.get("application/json");
    public void init() {
        ServerChat.RECEIVED.register((playerEntity, message) -> {
            Tuple<String, String> convertedPair = Utils.convertMentionsFromNames(message.getString());
            if (!DisChatBridge.stop && playerEntity != null) {
                if (DisChatBridge.config.isWebhookEnabled) {
                    String json = getWebhookJson(playerEntity, convertedPair);
                    RequestBody body = RequestBody.create(json, JSON);
                    Request request = new Request.Builder()
                            .url(DisChatBridge.config.webhookURL)
                            .post(body)
                            .build();
                    try {
                        DisChatBridge.webhookClient.newCall(request).execute();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    DisChatBridge.textChannel.sendMessage(DisChatBridge.config.texts.playerMessage.replace("%playername%", MarkdownSanitizer.escape(playerEntity.getScoreboardName())).replace("%playermessage%", convertedPair.getA())).queue();
                }
            }
            if (DisChatBridge.config.modifyChatMessages) {
                JsonObject newComponent = new JsonObject();
                newComponent.addProperty("text", MarkdownParser.parseMarkdown(convertedPair.getB()));
                return CompoundEventResult.interruptTrue(Component.Serializer.fromJson(newComponent.toString()));
            }
            return CompoundEventResult.pass();
        });

        PlayerEvent.PLAYER_ADVANCEMENT.register((playerEntity, advancement) -> {
            if (DisChatBridge.config.announceAdvancements && advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceChat() && !DisChatBridge.stop) {
                switch (advancement.getDisplay().getFrame()) {
                    case GOAL -> DisChatBridge.textChannel.sendMessage(DisChatBridge.config.texts.advancementGoal.replace("%playername%", MarkdownSanitizer.escape(playerEntity.getScoreboardName())).replace("%advancement%", MarkdownSanitizer.escape(advancement.getDisplay().getTitle().getString()))).queue();
                    case TASK -> DisChatBridge.textChannel.sendMessage(DisChatBridge.config.texts.advancementTask.replace("%playername%", MarkdownSanitizer.escape(playerEntity.getScoreboardName())).replace("%advancement%", MarkdownSanitizer.escape(advancement.getDisplay().getTitle().getString()))).queue();
                    case CHALLENGE -> DisChatBridge.textChannel.sendMessage(DisChatBridge.config.texts.advancementChallenge.replace("%playername%", MarkdownSanitizer.escape(playerEntity.getScoreboardName())).replace("%advancement%", MarkdownSanitizer.escape(advancement.getDisplay().getTitle().getString()))).queue();
                }
            }
        });

        EntityEvent.LIVING_DEATH.register((entity, source) -> {
            if (entity instanceof ServerPlayer playerEntity && DisChatBridge.config.announceDeaths && !DisChatBridge.stop) {
                DisChatBridge.textChannel.sendMessage(DisChatBridge.config.texts.deathMessage.replace("%deathmessage%", MarkdownSanitizer.escape(source.getLocalizedDeathMessage(entity).getString())).replace("%playername%", MarkdownSanitizer.escape(playerEntity.getScoreboardName()))).queue();
            }
            return EventResult.pass();
        });

        PlayerEvent.PLAYER_JOIN.register((playerEntity) -> {
            if (DisChatBridge.config.announcePlayers && !DisChatBridge.stop) {
                DisChatBridge.textChannel.sendMessage(DisChatBridge.config.texts.joinServer.replace("%playername%", MarkdownSanitizer.escape(playerEntity.getScoreboardName()))).queue();
            }
        });

        PlayerEvent.PLAYER_QUIT.register((playerEntity) -> {
            if (DisChatBridge.config.announcePlayers && !DisChatBridge.stop) {
                DisChatBridge.textChannel.sendMessage(DisChatBridge.config.texts.leftServer.replace("%playername%", MarkdownSanitizer.escape(playerEntity.getScoreboardName()))).queue();
            }
        });
    }

    @NotNull
    private static String getWebhookJson(ServerPlayer playerEntity, Tuple<String, String> convertedPair) {
        JsonObject body = new JsonObject();
        body.addProperty("username", playerEntity.getScoreboardName());
        body.addProperty("avatar_url", "https://mc-heads.net/avatar/" + (DisChatBridge.config.useUUIDInsteadNickname ? playerEntity.getUUID() : playerEntity.getScoreboardName()));
        JsonObject allowed_mentions = new JsonObject();
        JsonArray parse = new JsonArray();
        parse.add("users");
        parse.add("roles");
        allowed_mentions.add("parse", parse);
        body.add("allowed_mentions", allowed_mentions);
        body.addProperty("content", convertedPair.getA());
        return body.toString();
    }
}