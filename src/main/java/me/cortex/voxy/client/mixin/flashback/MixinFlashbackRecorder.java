package me.cortex.voxy.client.mixin.flashback;

import com.moulberry.flashback.record.FlashbackMeta;
import com.moulberry.flashback.record.Recorder;
import me.cortex.voxy.client.VoxyClientInstance;
import me.cortex.voxy.client.compat.FlashbackCopy;
import me.cortex.voxy.client.compat.IFlashbackMeta;
import me.cortex.voxy.commonImpl.VoxyCommon;
import me.cortex.voxy.commonImpl.WorldIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = Recorder.class, remap = false)
public class MixinFlashbackRecorder {
    @Shadow private volatile boolean needsInitialSnapshot;
    @Shadow @Final private FlashbackMeta metadata;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void voxy$getStoragePath(RegistryAccess registryAccess, CallbackInfo retInf) {
        if (VoxyCommon.isAvailable()) {
            var instance = VoxyCommon.getInstance();
            if (instance instanceof VoxyClientInstance ci) {
                ((IFlashbackMeta)this.metadata).setVoxyPath(ci.getStorageBasePath().toFile());
            }
        }
    }
    @Inject(method = "endTick", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void voxy$getDimensionChange(boolean close, CallbackInfo ci, Minecraft minecraft, boolean isLevelLoaded, boolean changedDimensions) {
        if (!needsInitialSnapshot) {
            Level level1 = Minecraft.getInstance().level;
            WorldIdentifier identifier1 = WorldIdentifier.of(level1);
            if (identifier1 != null) {
                FlashbackCopy.IDENTIFIERS.add(identifier1.getWorldId());
            }
        }
        if (changedDimensions) {
            Level level1 = Minecraft.getInstance().level;
            WorldIdentifier identifier1 = WorldIdentifier.of(level1);
            if (identifier1 != null) {
                FlashbackCopy.IDENTIFIERS.add(identifier1.getWorldId());
            }
        }
    }
}
