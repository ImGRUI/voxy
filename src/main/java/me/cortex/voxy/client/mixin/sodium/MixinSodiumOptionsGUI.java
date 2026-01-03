package me.cortex.voxy.client.mixin.sodium;

import me.cortex.voxy.client.config.IConfigPageSetter;
import me.cortex.voxy.client.config.VoxyConfigPageSodium;
import me.cortex.voxy.commonImpl.VoxyCommon;
import net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = SodiumOptionsGUI.class, remap = false)
public class MixinSodiumOptionsGUI implements IConfigPageSetter {
    @Shadow(remap = false) @Final private List<OptionPage> pages;

    @Shadow private OptionPage currentPage;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void voxy$addConfigPage(Screen prevScreen, CallbackInfo ci) {
        if (VoxyCommon.isAvailable()) {
            this.pages.add(VoxyConfigPageSodium.voxyOptionPage = VoxyConfigPageSodium.page());
        }
    }

    @Unique
    private OptionPage voxyJumpPage;

    public void voxy$setPageJump(OptionPage page) {
        this.voxyJumpPage = page;
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;init()V"))
    private void voxy$jumpPages(CallbackInfo ci) {
        if (this.voxyJumpPage != null) {
            this.currentPage = this.voxyJumpPage;
        }
    }
}