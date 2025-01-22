package one.armelin.dischatbridge;

import net.neoforged.fml.common.Mod;

@Mod("disforge")
public class DisForge {
    public DisForge() {
		// Submit our event bus to let architectury register our content on the right time
        DisChatBridge.serverInit();
    }
}