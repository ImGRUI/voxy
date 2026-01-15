package me.cortex.voxy.client.mixin.minecraft;

import me.cortex.voxy.client.core.IGetVoxyRenderSystem;
import me.cortex.voxy.commonImpl.VoxyCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class MixinDebugScreenOverlay {
    @Inject(method = "getSystemInformation", at = @At("RETURN"))
    private void injectDebug(CallbackInfoReturnable<List<String>> cir) {
        var ret = cir.getReturnValue();
        var instance = VoxyCommon.getInstance();
        if (instance != null) {
            ret.add("");
            ret.add("");
            instance.addDebug(ret);
        }
        var renderer = ((IGetVoxyRenderSystem) Minecraft.getInstance().levelRenderer).getVoxyRenderSystem();
        if (renderer != null) {
            renderer.addDebugInfo(ret);
        }
    }
}
