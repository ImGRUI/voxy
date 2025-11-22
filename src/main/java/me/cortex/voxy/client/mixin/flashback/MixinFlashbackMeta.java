package me.cortex.voxy.client.mixin.flashback;

import com.google.gson.JsonObject;
import com.moulberry.flashback.record.FlashbackMeta;
import com.moulberry.flashback.screen.EditReplayScreen;
import me.cortex.voxy.client.compat.FlashbackCopy;
import me.cortex.voxy.client.compat.IFlashbackMeta;
import me.cortex.voxy.client.config.VoxyConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

@Mixin(value = FlashbackMeta.class, remap = false)
public class MixinFlashbackMeta implements IFlashbackMeta {
    @Shadow public UUID replayIdentifier;
    @Unique private File voxyPath;
    @Unique private boolean voxySavedLods;

    @Override
    public void setVoxyPath(File path) {
        this.voxyPath = path;
    }

    @Override
    public File getVoxyPath() {
        return this.voxyPath;
    }

    @Override
    public void setVoxySavedLods(boolean savedLods) {
        this.voxySavedLods = savedLods;
    }

    @Override
    public boolean getVoxySavedLods() {
        return this.voxySavedLods;
    }

    @Inject(method = "toJson", at = @At("RETURN"))
    private void voxy$injectSaveVoxyPath(CallbackInfoReturnable<JsonObject> cir) {
        var val = cir.getReturnValue();
        if (val != null && this.voxyPath != null) {
            FlashbackCopy.replayIdentifier = replayIdentifier.toString();
            FlashbackCopy.basePath = getVoxyPath().toPath().toAbsolutePath();
            Screen screen = Minecraft.getInstance().screen;
            if (screen instanceof EditReplayScreen) {
                if (getVoxySavedLods()) {
                    Path copyPath = Minecraft.getInstance().gameDirectory.toPath().resolve(".voxy").resolve("flashback").resolve(replayIdentifier.toString()).toAbsolutePath();
                    val.addProperty("voxy_storage_path", copyPath.toString());
                    val.addProperty("voxy_copied_lods", true);
                } else {
                    val.addProperty("voxy_storage_path", this.voxyPath.getAbsoluteFile().getPath());
                }
                return;
            }
            if (VoxyConfig.CONFIG.saveOldLoDs) {
                Path copyPath = Minecraft.getInstance().gameDirectory.toPath().resolve(".voxy").resolve("flashback").resolve(replayIdentifier.toString()).toAbsolutePath();
                val.addProperty("voxy_storage_path", copyPath.toString());
                val.addProperty("voxy_copied_lods", true);
            } else {
                val.addProperty("voxy_storage_path", this.voxyPath.getAbsoluteFile().getPath());
            }
        }
    }

    @Inject(method = "fromJson", at = @At("RETURN"))
    private static void voxy$injectGetVoxyPath(JsonObject meta, CallbackInfoReturnable<FlashbackMeta> cir) {
        var val = cir.getReturnValue();
        if (val != null && meta != null) {
            if (meta.has("voxy_storage_path")) {
                ((IFlashbackMeta)val).setVoxyPath(new File(meta.get("voxy_storage_path").getAsString()));
            }
            if (meta.has("voxy_copied_lods")) {
                ((IFlashbackMeta)val).setVoxySavedLods(meta.get("voxy_copied_lods").getAsBoolean());
                FlashbackCopy.voxySavedLods = meta.get("voxy_copied_lods").getAsBoolean();
            } else {
                ((IFlashbackMeta)val).setVoxySavedLods(false);
                FlashbackCopy.voxySavedLods = false;
            }
        }
    }
}
