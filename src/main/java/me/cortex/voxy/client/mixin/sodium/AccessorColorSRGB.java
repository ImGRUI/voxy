package me.cortex.voxy.client.mixin.sodium;

import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.util.color.ColorSRGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ColorSRGB.class, remap = false)
public interface AccessorColorSRGB {
    @Invoker
    static int invokeLinearToSrgb(float value) {
        return 0;
    }
}
