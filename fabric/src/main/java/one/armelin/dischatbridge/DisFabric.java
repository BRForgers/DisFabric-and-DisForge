package one.armelin.dischatbridge;

import net.fabricmc.api.DedicatedServerModInitializer;

public class DisFabric implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        DisChatBridge.serverInit();
    }
}