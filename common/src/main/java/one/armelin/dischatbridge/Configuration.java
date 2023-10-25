package one.armelin.dischatbridge;

import dev.architectury.platform.Platform;
import blue.endless.jankson.Comment;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Configuration {
    @Comment(value = "Sets if DisFabric/DisForge should modify in-game chat messages")
    public boolean modifyChatMessages = true;

    @Comment(value = "Bot Token; see https://discordpy.readthedocs.io/en/latest/discord.html")
    public String botToken = "";

    @Comment(value = "Bot Game Status; What will be displayed on the bot's game status (leave empty for nothing)")
    public String botGameStatus = "";

    @Comment(value = "Enable Webhook; If enabled, player messages will be send using a webhook with the players name and head, instead of a regular message.")
    public boolean isWebhookEnabled = true;

    @Comment(value = "Webhook URL; see https://support.discord.com/hc/en-us/articles/228383668-Intro-to-Webhooks")
    public String webhookURL = "";

    @Comment(value = "Use UUID instead nickname to request player head on webhook")
    public Boolean useUUIDInsteadNickname = true;

    @Comment(value = "Sets if DisFabric/DisForge should send Bot messages to Minecraft")
    public boolean allowBotMessages = false;

    @Comment(value = """
            Admins ids in Discord; see https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID-
            If more than one, enclose each id in quotation marks separated by commas, like this:
            "adminsIds": [\s
            \t\t"000",
            \t\t"111",
            \t\t"222"
            \t]""")
    public String[] adminsIds = {""};

    @Comment(value = "Channel id in Discord")
    public String channelId = "";

    @Comment(value = "If you enabled \"Server Members Intent\" in the bot's config page, change it to true. (This is only necessary if you want to enable discord mentions inside the game)")
    public boolean membersIntents = false;

    @Comment(value = "Should announce when a players join/leave the server?")
    public boolean announcePlayers = true;

    @Comment(value = "Should announce when a players get an advancement?")
    public boolean announceAdvancements = true;

    @Comment(value = "Should announce when a player die?")
    public boolean announceDeaths = true;

    @Comment(value = "Should announce when server crash?")
    public boolean announceCrashes = true;

    public Texts texts = new Texts();

    public static class Texts {

        @Comment(value = """
                Minecraft -> Discord
                Player chat message (Only used when Webhook is disabled)
                Available placeholders:
                %playername% | Player name
                %playermessage% | Player message""")
        public String playerMessage = "**%playername%:** %playermessage%";

        @Comment(value = "Minecraft -> Discord\n"+
                "Server started message")
        public String serverStarted = "**Server started!**";

        @Comment(value = "Minecraft -> Discord\n"+
                "Server stopped message")
        public String serverStopped = "**Server stopped!**";

        @Comment(value = """
                Minecraft -> Discord
                Join server
                Available placeholders:
                %playername% | Player name""")
        public String joinServer = "**%playername% joined the game**";

        @Comment(value = """
                Minecraft -> Discord
                Left server
                Available placeholders:
                %playername% | Player name""")
        public String leftServer = "**%playername% left the game**";

        @Comment(value = """
                Minecraft -> Discord
                Death message
                Available placeholders:
                %playername% | Player name
                %deathmessage% | Death message""")
        public String deathMessage = "**%deathmessage%**";

        @Comment(value = """
                Minecraft -> Discord
                Advancement type task message
                Available placeholders:
                %playername% | Player name
                %advancement% | Advancement name""")
        public String advancementTask = "%playername% has made the advancement **[%advancement%]**";

        @Comment(value = """
                Minecraft -> Discord
                Advancement type challenge message
                Available placeholders:
                %playername% | Player name
                %advancement% | Advancement name""")
        public String advancementChallenge = "%playername% has completed the challenge **[%advancement%]**";

        @Comment(value = """
                Minecraft -> Discord
                Advancement type goal message
                Available placeholders:
                %playername% | Player name
                %advancement% | Advancement name""")
        public String advancementGoal = "%playername% has reached the goal **[%advancement%]**";

        @Comment(value = """
                Discord -> Minecraft
                Colored part of the message, this part of the message will receive the same color as the role in the discord, comes before the colorless part
                Available placeholders:
                %discordname% | User nickname in the guild
                %message% | The message""")
        public String coloredText = "[Discord] ";

        @Comment(value = """
                Discord -> Minecraft
                Colorless (white) part of the message, I think you already know what it is by the other comment
                Available placeholders:
                %discordname% | Nickname of the user in the guild
                %message% | The message""")
        public String colorlessText = "<%discordname%> %message%";

        @Comment(value = """
                Discord -> Minecraft
                Replied message text, with gray color, goes before the colorless text, after colored text
                Available placeholders:
                %discordname% | Nickname of the replied user in the guild""")
        public String replyText = "↪ %discordname%\n";

        @Comment(value = "Replaces the § symbol with & in any discord message to avoid formatted messages")
        public Boolean removeVanillaFormattingFromDiscord = false;

        @Comment(value = "Removes line break from any discord message to avoid spam")
        public Boolean removeLineBreakFromDiscord = false;

        @Comment(value = """
                Minecraft -> Discord
                Crash message
                Available placeholders:
                %crashdescription% | Crash description""")
        public String crashMessage = "**Server crashed:** %crashdescription%";
    }

    public static Configuration getConfig() {
        var jankson = Jankson.builder().build();
        Configuration config;
        Path configFolder = Platform.getConfigFolder();
        Path configFile = Paths.get(configFolder.toString(), DisChatBridge.name.toLowerCase() + ".json5");
        try {
            JsonObject configJson = jankson.load(configFile.toFile());
            config = jankson.fromJson(configJson, Configuration.class);
        } catch (IOException | SyntaxError e) {
            config = new Configuration();
        }
        try {
            Files.writeString(configFile, jankson.toJson(config).toJson(true,true));
        } catch (IOException e) {
            DisChatBridge.LOGGER.error("Failed to write config file!");
            e.printStackTrace();
        }
        return config;
    }
}