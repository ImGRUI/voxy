package me.cortex.voxy.client.mixin.flashback;

import com.moulberry.flashback.Flashback;
import me.cortex.voxy.client.config.VoxyConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.cortex.voxy.client.compat.FlashbackCopy.CopyLods;
import static me.cortex.voxy.client.compat.FlashbackCopy.IDENTIFIERS;


@Mixin(value = Flashback.class, remap = false)
public class MixinFlashbackFlashback {
    @Inject(method = "finishRecordingReplay()V", at = @At("TAIL"))
    private static void voxy$copylods(CallbackInfo ci) {
        if (VoxyConfig.CONFIG.saveOldLODs) CopyLods();
        IDENTIFIERS.clear();
    }
}
