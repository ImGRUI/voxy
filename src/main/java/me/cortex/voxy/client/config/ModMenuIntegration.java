package me.cortex.voxy.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.cortex.voxy.common.Logger;
import me.cortex.voxy.commonImpl.VoxyCommon;
import net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (VoxyCommon.isAvailable()) {
                var screen = (SodiumOptionsGUI) SodiumOptionsGUI.createScreen(parent);
                ((IConfigPageSetter)screen).voxy$setPageJump(VoxyConfigPageSodium.voxyOptionPage);
                return screen;
            } else {
                return null;
            }
        };
    }
}