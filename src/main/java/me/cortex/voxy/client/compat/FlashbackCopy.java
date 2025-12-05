package me.cortex.voxy.client.compat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cortex.voxy.common.Logger;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FlashbackCopy {
    public static HashSet<String> IDENTIFIERS = new HashSet<>();
    public static boolean FlashbackSaving = false;
    public static String replayIdentifier;
    public static Path basePath;
    public static boolean voxySavedLods;

    private static void copyDir(Path source, Path target) {
        try (var stream = Files.walk(source)) {
            stream.forEach(file -> {
                if (file.toString().contains("LOG")) {
                    return;
                }
                try {
                    Files.createDirectories(target);
                    Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    Logger.warn("Failed to copy", file);
                }
            });
        } catch (Exception e) {
            Logger.info("Failed to walk in", source);
        }
    }

    private static void deleteDir(Path deleteDir) {
        try (var stream = Files.walk(deleteDir)) {
            stream
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            Logger.warn("Failed to delete", path);
                        }
                    });
        } catch (Exception e) {
            Logger.warn("Failed to walk in", deleteDir);
        }
    }

    public static void CopyLods() {
        Path copyPath = Minecraft.getInstance().gameDirectory.toPath().resolve(".voxy").resolve("flashback").resolve(replayIdentifier).toAbsolutePath();
        CopyLods(basePath, copyPath);
        Logger.warn("Copied LoDs for", replayIdentifier);
    }

    private static void CopyLods(Path basePath, Path copyPath) {
        FlashbackSaving = true;
        for (String worldId : IDENTIFIERS) {
            Path newBasePath = basePath.resolve(worldId);
            Path newCopyPath = copyPath.resolve(worldId);
            copyDir(newBasePath, newCopyPath);
        }
        try {
            Files.copy(basePath.resolve("config.json"), copyPath.resolve("config.json"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Logger.warn("Failed to copy config.json", basePath);
        }
        FlashbackSaving = false;
    }

    public static void CheckReplays() {
        Path replays = Minecraft.getInstance().gameDirectory.toPath().resolve("flashback").resolve("replays").toAbsolutePath();
        Path flashbackLodFolder = Minecraft.getInstance().gameDirectory.toPath().resolve(".voxy").resolve("flashback").toAbsolutePath();
        List<Path> flashbackLodFolders = new ArrayList<>();

        try (var stream = Files.walk(replays)) {
            stream
                    .filter(path -> path.toString().endsWith(".zip"))
                    .forEach(zipPath -> {
                        try {
                            String lodUUID = lodUUID(zipPath);
                            if (lodUUID != null) {
                                flashbackLodFolders.add(flashbackLodFolder.resolve(lodUUID));
                            }
                        } catch (Exception e) {
                            Logger.warn("Failed to check replay", zipPath);
                        }
                    });
        } catch (IOException e) {
            Logger.warn("Failed to walk replays files");
        }

        try (var stream = Files.list(flashbackLodFolder)) {
            stream
                    .filter(Files::isDirectory)
                    .forEach(path -> {
                        try {
                            if (!flashbackLodFolders.contains(path)) {
                                // delete system (32)
                                deleteDir(path);
                                Logger.warn("Deleted permanently", path);
                            }
                        } catch (Exception e) {
                            Logger.warn("Failed to delete", path);
                        }
                    });
        } catch (IOException e) {
            Logger.warn("Failed to walk flashback LoDs files");
        }
    }

    public static void deleteReplayLOD() {
        Path flashbackLod = Minecraft.getInstance().gameDirectory.toPath().resolve(".voxy").resolve("flashback").resolve(replayIdentifier).toAbsolutePath();
        try {
            FileUtils.deleteDirectory(flashbackLod.toFile());
            Logger.warn("Deleted Replay LoD", replayIdentifier);
        } catch (IOException e) {
            Logger.error("Failed to delete Replay LoD", replayIdentifier);
        }
    }

    private static String lodUUID(Path zipPath) {
        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
            ZipEntry zipEntry = zipFile.getEntry("metadata.json");
            if (zipEntry != null) {
                try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    JsonObject jsonObject = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
                    if (jsonObject.has("voxy_copied_lods") && jsonObject.get("voxy_copied_lods").getAsBoolean()) {
                        return jsonObject.get("uuid").getAsString();
                    }
                }
            }
        } catch (IOException e) {
            Logger.warn("Failed to read LoD location from", zipPath);
        }
        return null;
    }
}
