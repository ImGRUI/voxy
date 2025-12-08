package me.cortex.voxy.client.compat;

import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.playback.ReplayServer;
import com.moulberry.flashback.record.FlashbackMeta;
import me.cortex.voxy.common.Logger;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FlashbackCompat {
    public static final boolean FLASHBACK_INSTALLED = FabricLoader.getInstance().isModLoaded("flashback");

    public static Path getReplayStoragePath() {
        if (!FLASHBACK_INSTALLED) {
            return null;
        }
        return getReplayStoragePath0();
    }

    private static Path getReplayStoragePath0() {
        ReplayServer replayServer = Flashback.getReplayServer();
        if (replayServer != null) {
            FlashbackMeta meta = replayServer.getMetadata();
            if (meta != null) {
                var path = ((IFlashbackMeta)meta).getVoxyPath();
                if (path != null) {
                    Logger.info("Flashback replay server exists and meta exists");
                    if (path.exists()) {
                        Logger.info("Flashback Voxy path exists in filesystem, using this as LoD data source");
                        return path.toPath();
                    } else {
                        Logger.warn("Flashback meta had Voxy path saved but path doesn't exist");
                    }
                }
            }
        }
        return null;
    }
}
