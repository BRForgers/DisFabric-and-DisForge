package one.armelin.dischatbridge;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.platform.Platform;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.minecraft.commands.Commands;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import one.armelin.dischatbridge.commands.ShrugCommand;
import one.armelin.dischatbridge.listeners.DiscordEventListener;
import one.armelin.dischatbridge.listeners.MinecraftEventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

public class DisChatBridge {

	public static final String name = Platform.isFabric() ? "DisFabric" : "DisForge";
	public static final Logger LOGGER = LogManager.getFormatterLogger(name);
	public static Configuration config;
	public static JDA jda;

	public static OkHttpClient webhookClient = new OkHttpClient.Builder()
			.protocols(Collections.singletonList(Protocol.HTTP_1_1))
			.build();

	public static GuildMessageChannel textChannel;
	public static boolean stop = false;
	public static void serverInit() {
		config = Configuration.getConfig();
		try {
			JDABuilder jdaBuilder = JDABuilder.createDefault(config.botToken).setHttpClient(new OkHttpClient.Builder()
							.protocols(Collections.singletonList(Protocol.HTTP_1_1))
							.build())
					.addEventListeners(new DiscordEventListener())
					.enableIntents(GatewayIntent.MESSAGE_CONTENT);
			if(config.membersIntents){
				jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS).setMemberCachePolicy(MemberCachePolicy.ALL);
			}
			DisChatBridge.jda = jdaBuilder.build();
			DisChatBridge.jda.awaitReady();
			DisChatBridge.textChannel = (GuildMessageChannel) DisChatBridge.jda.getGuildChannelById(config.channelId);
		} catch (InvalidTokenException ex) {
			jda = null;
			DisChatBridge.LOGGER.error("Unable to login!", ex);
		} catch (InterruptedException ex) {
			jda = null;
			DisChatBridge.LOGGER.error("Exception", ex);
		}
		if(jda != null) {
			if (!config.botGameStatus.isEmpty())
				jda.getPresence().setActivity(Activity.playing(config.botGameStatus));
			LifecycleEvent.SERVER_STARTING.register(server -> {
				if(jda != null){
					textChannel.sendMessage(DisChatBridge.config.texts.serverStarted).queue();
				}
			});
			LifecycleEvent.SERVER_STOPPING.register(server -> {
				if(jda != null){
					stop = true;
					textChannel.sendMessage(DisChatBridge.config.texts.serverStopped).queue();
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					webhookClient.dispatcher().executorService().shutdown();
					webhookClient.connectionPool().evictAll();
					DisChatBridge.jda.shutdown();
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			CommandRegistrationEvent.EVENT.register((dispatcher, context, environment) -> {
				if (environment == Commands.CommandSelection.DEDICATED) {
					ShrugCommand.register(dispatcher);
				}
			});
			new MinecraftEventListener().init();
		}
	}
}
