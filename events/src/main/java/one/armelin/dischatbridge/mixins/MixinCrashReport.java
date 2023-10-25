package one.armelin.dischatbridge.mixins;

import net.minecraft.CrashReport;
import one.armelin.dischatbridge.events.Crash;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CrashReport.class)
public class MixinCrashReport {
    @Inject(at = @At(value = "RETURN"), method = "<init>*")
    public void newCrash(String string, Throwable throwable, CallbackInfo ci) {
        Crash.CRASHED.invoker().crashed(string, throwable);
    }
}
