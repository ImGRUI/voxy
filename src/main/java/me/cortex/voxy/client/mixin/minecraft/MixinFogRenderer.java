package me.cortex.voxy.client.mixin.minecraft;

import com.llamalad7.mixinextras.sugar.Local;
import me.cortex.voxy.client.config.VoxyConfig;
import me.cortex.voxy.client.core.IGetVoxyRenderSystem;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public abstract class MixinFogRenderer {
    @Shadow protected abstract CameraSubmersionType getCameraSubmersionType(Camera camera, boolean thick);

    @Redirect(method = "applyFog(Lnet/minecraft/client/render/Camera;IZLnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/fog/FogData;renderDistanceEnd:F", opcode = Opcodes.PUTFIELD), require = 0)
    private void voxy$modifyFog(FogData instance, float distance, @Local(argsOnly = true) boolean thick, @Local(argsOnly = true) Camera camera) {
        var vrs = (IGetVoxyRenderSystem) MinecraftClient.getInstance().worldRenderer;
        CameraSubmersionType cameraSubmersionType = getCameraSubmersionType(camera, thick);

        if (VoxyConfig.CONFIG.renderVanillaFog || vrs == null || vrs.getVoxyRenderSystem() == null) {
            instance.renderDistanceEnd = distance;
        } else {
            instance.renderDistanceStart = 999999999;
            instance.renderDistanceEnd = 999999999;
            if (cameraSubmersionType == CameraSubmersionType.DIMENSION_OR_BOSS) {
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
