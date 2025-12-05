package me.cortex.voxy.client.mixin.flashback;

import com.moulberry.flashback.screen.SaveReplayScreen;
import me.cortex.voxy.client.compat.FlashbackCopy;
import me.cortex.voxy.client.config.VoxyConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SaveReplayScreen.class, remap = false)
public class MixinFlashbackSaveReplayScreen {
    @Inject(method = "deleteReplay()V", at = @At("TAIL"))
    private static void voxy$deleteReplay(CallbackInfo ci) {
        if (VoxyConfig.CONFIG.saveOldLoDs) FlashbackCopy.deleteReplayLOD();
    }
}
