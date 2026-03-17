package xiao.battleroyale.config.common.server.function;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.common.server.function.IFunctionConfigManager;
import xiao.battleroyale.api.config.common.server.function.IFunctionSingleEntry;
import xiao.battleroyale.api.config.common.server.function.FunctionConfigTag;
import xiao.battleroyale.config.AbstractConfigSubManager;
import xiao.battleroyale.config.AbstractSingleConfig;
import xiao.battleroyale.config.FolderConfigData;
import xiao.battleroyale.config.common.server.ServerConfigManager;
import xiao.battleroyale.config.common.server.function.defaultconfigs.DefaultFunctionConfigGenerator;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Path;
import java.util.Comparator;

public class FunctionConfigManager
        extends AbstractConfigSubManager<FunctionConfigManager.FunctionConfig>
        implements IFunctionConfigManager<FunctionConfigManager.FunctionConfig> {

    private static class FunctionConfigManagerHolder {
        private static final FunctionConfigManager INSTANCE = new FunctionConfigManager();
    }

    public static FunctionConfigManager get() {
        return FunctionConfigManagerHolder.INSTANCE;
    }

    private FunctionConfigManager() {
        allFolderConfigData.put(DEFAULT_FUNCTION_CONFIG_FOLDER, new FolderConfigData<>(DEFAULT_FUNCTION_CONFIG_FOLDER));
    }

    public static void init() {
        ServerConfigManager.get().registerSubManager(get());
    }

    public static final String FUNCTION_CONFIG_PATH = ServerConfigManager.SERVER_CONFIG_PATH;
    public static final String FUNCTION_CONFIG_SUB_PATH = "function";

    protected final int DEFAULT_FUNCTION_CONFIG_FOLDER = 0;

    /**
     * 单个功能配置条目
     */
    public static class FunctionConfig extends AbstractSingleConfig implements IFunctionSingleEntry {
        public static final String CONFIG_TYPE = "FunctionConfig";

        public FunctionConfig(int id, String name, String color) {
            this(id, name, color, false);
        }
        public FunctionConfig(int id, String name, String color, boolean isDefault) {
            super(id, name, color, isDefault);
        }
        @Override public @NotNull FunctionConfig copy() {
            return new FunctionConfig(id, name, color, isDefault);
        }

        @Override
        public String getType() {
            return CONFIG_TYPE;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(FunctionConfigTag.ID, id);
            if (isDefault) {
                jsonObject.addProperty(FunctionConfigTag.DEFAULT, isDefault);
            }
            jsonObject.addProperty(FunctionConfigTag.NAME, name);
            jsonObject.addProperty(FunctionConfigTag.COLOR, color);

            return jsonObject;
        }

        @Override
        public void applyDefault() {
        }
    }

    @Override
    protected Comparator<FunctionConfig> getConfigIdComparator(int configType) {
        return Comparator.comparingInt(FunctionConfig::getConfigId);
    }

    /**
     * IConfigManager
     */
    @Override
    public String getFolderType(int folderId) {
        return FunctionConfig.CONFIG_TYPE;
    }

    /**
     * IConfigDefaultable
     */
    @Override
    public boolean generateDefaultConfigs() {
        return generateDefaultConfigs(DEFAULT_FUNCTION_CONFIG_FOLDER);
    }

    @Override
    public boolean generateDefaultConfigs(int folderId) {
        DefaultFunctionConfigGenerator.generateAllDefaultConfigs(String.valueOf(getConfigDirPath()));
        return true;
    }

    @Override
    public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_FUNCTION_CONFIG_FOLDER);
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override
    public FunctionConfig parseConfigEntry(JsonObject configObject, Path filePath, int folderId) {
        try {
            int id = JsonUtils.getJsonInt(configObject, FunctionConfigTag.ID, -1);
            if (id < 0) {
                BattleRoyale.LOGGER.warn("Skipped invalid function config in {}", filePath);
                return null;
            }
            boolean isDefault = JsonUtils.getJsonBool(configObject, FunctionConfigTag.DEFAULT, false);
            String name = JsonUtils.getJsonString(configObject, FunctionConfigTag.NAME, "");
            String color = JsonUtils.getJsonString(configObject, FunctionConfigTag.COLOR, "#FFFFFF");

            return new FunctionConfig(id, name, color, isDefault);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error parsing {} entry in {}: {}", getFolderType(folderId), filePath, e.getMessage());
            return null;
        }
    }
    @Override public String getConfigPath(int folderId) {
        return FUNCTION_CONFIG_PATH;
    }
    @Override public String getConfigSubPath(int folderId) {
        return FUNCTION_CONFIG_SUB_PATH;
    }

    @Override
    public void initializeDefaultConfigsIfEmpty() {
        super.initializeDefaultConfigsIfEmpty(DEFAULT_FUNCTION_CONFIG_FOLDER);
    }
}