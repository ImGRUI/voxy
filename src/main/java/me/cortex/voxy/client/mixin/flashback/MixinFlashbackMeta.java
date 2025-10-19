package me.cortex.voxy.client.mixin.flashback;

import com.google.gson.JsonObject;
import com.moulberry.flashback.record.FlashbackMeta;
import me.cortex.voxy.client.compat.IFlashbackMeta;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static me.cortex.voxy.client.VoxyClientInstance.copyDir;
import static me.cortex.voxy.client.VoxyClientInstance.getBasePath;

@Mixin(value = FlashbackMeta.class, remap = false)
public class MixinFlashbackMeta implements IFlashbackMeta {

    @Shadow public UUID replayIdentifier;
    @Unique private File voxyPath;

    @Override
    public void setVoxyPath(File path) {
        this.voxyPath = path;
    }

    @Override
    public File getVoxyPath() {
        return this.voxyPath;
    }

    @Inject(method = "toJson", at = @At("RETURN"))
    private void voxy$injectSaveVoxyPath(CallbackInfoReturnable<JsonObject> cir) throws IOException {
        var val = cir.getReturnValue();
        if (val != null && this.voxyPath != null) {
            Path basePath = getBasePath();
            Path copyPath = MinecraftClient.getInstance().runDirectory.toPath().resolve(".voxy").resolve("flashback").resolve(replayIdentifier.toString());
            Files.createDirectories(copyPath);
            copyDir(basePath, copyPath);
            val.addProperty("voxy_storage_path", copyPath.toString());
        }
    }

    @Inject(method = "fromJson", at = @At("RETURN"))
    private static void voxy$injectGetVoxyPath(JsonObject meta, CallbackInfoReturnable<FlashbackMeta> cir) {
        var val = cir.getReturnValue();
        if (val != null && meta != null) {
            if (meta.has("voxy_storage_path")) {
                ((IFlashbackMeta)val).setVoxyPath(new File(meta.get("voxy_storage_path").getAsString()));
            }
        }
    }
}
