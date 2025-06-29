package xiao.battleroyale.config.common.effect.particle;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.effect.particle.IParticleEntry;
import xiao.battleroyale.api.game.effect.particle.IParticleSingleEntry;
import xiao.battleroyale.api.game.effect.particle.ParticleConfigTag;
import xiao.battleroyale.common.effect.particle.FixedParticleData;
import xiao.battleroyale.common.effect.particle.ParticleData;
import xiao.battleroyale.config.AbstractConfigManager;
import xiao.battleroyale.config.AbstractSingleConfig;
import xiao.battleroyale.config.common.effect.EffectConfigManager;
import xiao.battleroyale.config.common.effect.particle.defaultconfigs.DefaultParticleConfigGenerator;
import xiao.battleroyale.util.JsonUtils;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class ParticleConfigManager extends AbstractConfigManager<ParticleConfigManager.ParticleConfig> {

    private static class ParticleConfigManagerHolder {
        private static final ParticleConfigManager INSTANCE = new ParticleConfigManager();
    }

    public static ParticleConfigManager get() {
        return ParticleConfigManagerHolder.INSTANCE;
    }

    private ParticleConfigManager() {
        allFolderConfigData.put(DEFAULT_PARTICLE_CONFIG_FOLDER_ID, new FolderConfigData<>());
    }

    public static void init() {
        get().reloadParticleConfigs();
    }

    public static final String PARTICLE_CONFIG_PATH = EffectConfigManager.EFFECT_CONFIG_PATH;
    public static final String PARTICLE_CONFIG_SUB_PATH = "particle";

    protected final int DEFAULT_PARTICLE_CONFIG_FOLDER_ID = 0;

    public static class ParticleConfig extends AbstractSingleConfig implements IParticleSingleEntry {
        public static final String CONFIG_TYPE = "ParticleConfig";

        public final ParticleDetailEntry entry;

        public ParticleConfig(int id, String name, String color, ParticleDetailEntry entry) {
            this(id, name, color, false, entry);
        }

        public ParticleConfig(int id, String name, String color, boolean isDefault, ParticleDetailEntry entry) {
            super(id, name, color, isDefault);
            this.entry = entry;
        }

        public int getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public String getColor() {
            return color;
        }
        public IParticleEntry getEntry() {
            return entry;
        }

        @Override
        public ParticleData createParticleData(ServerLevel serverLevel) {
            return entry.createParticleData(serverLevel);
        }

        @Override
        public FixedParticleData createParticleData(ServerLevel serverLevel, Vec3 fixedPos) {
            return new FixedParticleData(serverLevel, entry, fixedPos);
        }

        @Override
        public String getType() {
            return CONFIG_TYPE;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(ParticleConfigTag.PARTICLE_ID, id);
            if (isDefault) {
                jsonObject.addProperty(ParticleConfigTag.DEFAULT, isDefault);
            }
            jsonObject.addProperty(ParticleConfigTag.PARTICLE_NAME, name);
            jsonObject.addProperty(ParticleConfigTag.PARTICLE_COLOR, color);
            if (entry != null) {
                jsonObject.add(ParticleConfigTag.DETAIL_ENTRY, entry.toJson());
            }

            return jsonObject;
        }

        @Override
        public int getConfigId() {
            return getId();
        }

        public static ParticleDetailEntry deserializeParticleDetailEntry(JsonObject jsonObject) {
            try {
                ParticleDetailEntry detailEntry = ParticleDetailEntry.fromJson(jsonObject);
                if (detailEntry != null) {
                    return detailEntry;
                } else {
                    BattleRoyale.LOGGER.warn("Skipped invalid ParticleDetailEntry");
                    return null;
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to deserialize ParticleDetailEntry: {}", e.getMessage());
                return null;
            }
        }
    }

    @Override protected Comparator<ParticleConfig> getConfigIdComparator(int folderId) {
        return Comparator.comparingInt(ParticleConfig::getConfigId);
    }

    /**
     * IConfigManager
     */
    @Override public String getFolderType(int folderId) {
        return ParticleConfig.CONFIG_TYPE;
    }

    /**
     * IConfigDefaultable
     */
    @Override public void generateDefaultConfigs() {
        generateDefaultConfigs(DEFAULT_PARTICLE_CONFIG_FOLDER_ID);
    }

    @Override public void generateDefaultConfigs(int folderId) {
        DefaultParticleConfigGenerator.generateDefaultParticleConfigs();
    }
    @Override public int getDefaultConfigId() {
        return getDefaultConfigId(DEFAULT_PARTICLE_CONFIG_FOLDER_ID);
    }

    /**
     * IConfigLoadable
     */
    @Nullable
    @Override
    public ParticleConfig parseConfigEntry(JsonObject configObject, Path filePath, int folderId) {
        try {
            int id = JsonUtils.getJsonInt(configObject, ParticleConfigTag.PARTICLE_ID, -1);
            JsonObject detailEntryObject = JsonUtils.getJsonObject(configObject, ParticleConfigTag.DETAIL_ENTRY, null);
            if (id < 0 || detailEntryObject == null) {
                BattleRoyale.LOGGER.warn("Skipped invalid particle config in {}", filePath);
                return null;
            }
            boolean isDefault = JsonUtils.getJsonBool(configObject, ParticleConfigTag.DETAIL_ENTRY, false);
            String name = JsonUtils.getJsonString(configObject, ParticleConfigTag.PARTICLE_NAME, "");
            String color = JsonUtils.getJsonString(configObject, ParticleConfigTag.PARTICLE_COLOR, "#FFFFFF");
            JsonObject jsonObject = JsonUtils.getJsonObject(configObject, ParticleConfigTag.DETAIL_ENTRY, null);
            ParticleDetailEntry detailEntry = ParticleConfig.deserializeParticleDetailEntry(jsonObject);
            if (detailEntry == null) {
                BattleRoyale.LOGGER.warn("Failed to deserialize particle detail entry for id: {} in {}", id, filePath);
                return null;
            }

            return new ParticleConfig(id, name, color, isDefault, detailEntry);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error parsing {} entry in {}: {}", getFolderType(), filePath, e.getMessage());
            return null;
        }
    }
    @Override public String getConfigPath(int folderId) {
        return PARTICLE_CONFIG_PATH;
    }
    @Override public String getConfigSubPath(int folderId) {
        return PARTICLE_CONFIG_SUB_PATH;
    }

    /**
     * 特定类别的获取接口
     */
    public ParticleConfig getParticleConfig(int id) {
        return getConfigEntry(id, DEFAULT_PARTICLE_CONFIG_FOLDER_ID);
    }
    public List<ParticleConfig> getAllParticleConfigs() {
        return getConfigEntryList(DEFAULT_PARTICLE_CONFIG_FOLDER_ID);
    }

    /**
     * 特定类别的重新读取接口
     */
    public void reloadParticleConfigs() {
        reloadConfigs(DEFAULT_PARTICLE_CONFIG_FOLDER_ID);
    }

    @Override public void initializeDefaultConfigsIfEmpty() {
        super.initializeDefaultConfigsIfEmpty(DEFAULT_PARTICLE_CONFIG_FOLDER_ID);
    }
}
