package one.armelin.dischatbridge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("disforge")
public class DisForge {
    public DisForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(DisChatBridge.name.toLowerCase(), FMLJavaModLoadingContext.get().getModEventBus());
        DisChatBridge.serverInit();
    }
}