package xiao.battleroyale.common.server.profile;

import com.google.gson.JsonArray;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.config.IConfigManager;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.config.IModConfigManager;
import xiao.battleroyale.api.config.common.server.profile.IProfileSingleEntry;
import xiao.battleroyale.api.config.sub.IConfigSingleEntry;
import xiao.battleroyale.api.server.profile.IProfileManager;
import xiao.battleroyale.config.common.server.ServerConfigManager;
import xiao.battleroyale.config.common.server.profile.ProfileConfigManager;
import xiao.battleroyale.config.common.server.profile.ProfileConfigManager.ProfileConfig;
import xiao.battleroyale.config.common.server.profile.config.ConfigApplicationEntry;
import xiao.battleroyale.config.common.server.profile.config.ConfigEntry;
import xiao.battleroyale.config.common.server.profile.config.ConfigManagerEntry;
import xiao.battleroyale.config.common.server.profile.config.ConfigSubManagerEntry;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.StringUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class ProfileManager implements IProfileManager {

    private static class ProfileManagerHolder {
        private static final ProfileManager INSTANCE = new ProfileManager();
    }

    public static ProfileManager get() {
        return ProfileManagerHolder.INSTANCE;
    }

    protected ProfileManager() {
        ;
    }

    public static void init(McSide mcSide) {
        ;
    }

    @Override
    public int saveCurrentProfile(@Nullable CommandSourceStack source, ServerLevel serverLevel, int id, boolean overwrite) {
        IModConfigManager modConfigManager = BattleRoyale.getModConfigManager();
        IConfigSubManager<?> profileConfigManager = modConfigManager.getConfigSubManager(ServerConfigManager.get().getNameKey(), ProfileConfigManager.get().getNameKey());
        if (profileConfigManager == null) {
            if (source != null) source.sendFailure(Component.translatable("battleroyale.message.save_current_profile_failed"));
            return -1;
        }

        JsonArray profileConfigJson = new JsonArray();
        String startSystemTime = StringUtils.getTimestampString();
        String configEntryName = String.format("profileConfig-%s", startSystemTime);
        String fileName;
        if (overwrite) {
            fileName = profileConfigManager.getCurrentSelectedFileName();
            List<? extends IConfigSingleEntry> configEntryList = profileConfigManager.getConfigEntryList();
            for (IConfigSingleEntry configSingleEntry : configEntryList) {
                profileConfigJson.add(configSingleEntry.toJson());
            }
        } else {
            fileName = configEntryName;
        }

        int savedTotal = 0;

        List<ConfigManagerEntry> configManagerEntries = new ArrayList<>();
        for (IConfigManager configManager : modConfigManager.getConfigManagers()) {
            savedTotal += addToConfigManagerEntry(configManagerEntries, configManager);
        }

        List<ConfigSubManagerEntry> configSubManagerEntries = new ArrayList<>();
        for (IConfigSubManager<?> configSubManager : modConfigManager.getConfigSubManagers()) {
            savedTotal += addToConfigSubManagerEntry(configSubManagerEntries, configSubManager);
        }

        ConfigApplicationEntry configApplicationEntry = new ConfigApplicationEntry(configManagerEntries, configSubManagerEntries);

        ProfileConfig profileConfig = new ProfileConfig(id, configEntryName, "#FFFFFFAA", configApplicationEntry);
        profileConfigJson.add(profileConfig.toJson());
        String configDirPath = String.valueOf(profileConfigManager.getConfigDirPath());
        writeJsonToFile(Paths.get(Paths.get(configDirPath, fileName + ".json").toString()).toString(), profileConfigJson);
        if (source != null) {
            int totalSaved = savedTotal;
            source.sendSuccess(() -> Component.translatable("battleroyale.message.save_current_profile_success", totalSaved), true);
        }
        return savedTotal;
    }

    @Override
    public int loadProfile(@Nullable CommandSourceStack source, ServerLevel serverLevel, int id) {
        IProfileSingleEntry profileConfig = getIProfileSingleEntry(id);
        int loadedTotal = profileConfig != null ? profileConfig.applyAllProfile() : -1;
        if (loadedTotal < 0) {
            if (source != null) source.sendFailure(Component.translatable("battleroyale.message.load_profile_failed", id));
            return -1;
        }

        if (source != null) {
            source.sendSuccess(() -> Component.translatable("battleroyale.message.load_profile_success", profileConfig.getName(), loadedTotal), true);
        }
        return loadedTotal;
    }

    private static @Nullable IProfileSingleEntry getIProfileSingleEntry(int id) {
        IModConfigManager modConfigManager = BattleRoyale.getModConfigManager();
        @Nullable IConfigSubManager<?> profileConfigManager = modConfigManager.getConfigSubManager(ServerConfigManager.get().getNameKey(), ProfileConfigManager.get().getNameKey());
        @Nullable IConfigSingleEntry configSingleEntry = profileConfigManager != null ? profileConfigManager.getConfigEntry(id) : null;
        @Nullable IProfileSingleEntry profileConfig = configSingleEntry instanceof IProfileSingleEntry entry ? entry : null;
        return profileConfig;
    }

    public static int addToConfigManagerEntry(List<ConfigManagerEntry> configManagerEntries, IConfigManager configManager) {
        int total = 0;
        List<ConfigSubManagerEntry> configSubManagerEntries = new ArrayList<>();
        for (IConfigSubManager<?> configSubManager : configManager.getConfigSubManagers()) {
            total += addToConfigSubManagerEntry(configSubManagerEntries, configSubManager);
        }

        configManagerEntries.add(new ConfigManagerEntry(configManager.getNameKey(), configSubManagerEntries));
        return total;
    }

    public static int addToConfigSubManagerEntry(List<ConfigSubManagerEntry> configSubManagerEntries, IConfigSubManager<?> configSubManager) {
        int total = 0;
        List<ConfigEntry> configEntries = new ArrayList<>();
        for (Integer folderId : configSubManager.getAllFolderId()) {
            String fileName = configSubManager.getCurrentSelectedFileName(folderId);
            int lastAppliedConfigId = configSubManager.getLastAppliedConfigId(folderId);
            configEntries.add(new ConfigEntry(folderId, fileName, lastAppliedConfigId));
            total++;
        }

        configSubManagerEntries.add(new ConfigSubManagerEntry(configSubManager.getNameKey(), configEntries));
        return total;
    }
}
