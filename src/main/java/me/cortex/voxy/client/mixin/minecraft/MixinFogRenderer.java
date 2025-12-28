package me.cortex.voxy.client.mixin.minecraft;

import com.llamalad7.mixinextras.sugar.Local;
import me.cortex.voxy.client.config.VoxyConfig;
import me.cortex.voxy.client.core.IGetVoxyRenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.level.material.FogType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public abstract class MixinFogRenderer {
    @Shadow protected abstract FogType getFogType(Camera camera, boolean thick);

    @Redirect(method = "setupFog", at = @At(value = "FIELD", target ="Lnet/minecraft/client/renderer/fog/FogData;renderDistanceEnd:F", opcode = Opcodes.PUTFIELD), require = 0)
    private void voxy$modifyFog(FogData instance, float distance, @Local(argsOnly = true) boolean thick, @Local(argsOnly = true) Camera camera) {
        var vrs = (IGetVoxyRenderSystem) Minecraft.getInstance().levelRenderer;
        FogType fogType = getFogType(camera, thick);

        if (VoxyConfig.CONFIG.renderVanillaFog || vrs == null || vrs.getVoxyRenderSystem() == null) {
            instance.renderDistanceEnd = distance;
        } else {
            instance.renderDistanceStart = 999999999;
            instance.renderDistanceEnd = 999999999;
            if (VoxyConfig.CONFIG.customFog && fogType == FogType.ATMOSPHERIC) {
                instance.environmentalEnd = VoxyConfig.CONFIG.environmentalEnd;
            }
            if (fogType == FogType.DIMENSION_OR_BOSS && VoxyConfig.CONFIG.fixNetherFog && VoxyConfig.CONFIG.useEnvironmentalFog) {
                instance.environmentalStart = 1;
                instance.environmentalEnd = 99999999;
            }
            if (!VoxyConfig.CONFIG.useEnvironmentalFog) {
                instance.environmentalStart = 99999999;
                instance.environmentalEnd = 99999999;
            }
        }
    }
}
