package me.cortex.voxy.client.mixin.flashback;

import com.moulberry.flashback.Flashback;
import me.cortex.voxy.client.compat.FlashbackCopy;
import me.cortex.voxy.client.config.VoxyConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Flashback.class, remap = false)
public class MixinFlashbackFlashback {
    @Inject(method = "finishRecordingReplay()V", at = @At("TAIL"))
    private static void voxy$copylods(CallbackInfo ci) {
        if (VoxyConfig.CONFIG.saveOldLODs) FlashbackCopy.CopyLods();
        FlashbackCopy.IDENTIFIERS.clear();
    }
}
