package xiao.battleroyale;

import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.config.IModConfigManager;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.init.IRegistrarFactory;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.ModConfigManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.resource.ResourceLoader;

import java.util.Random;

public class BattleRoyale {
    public static final String MOD_ID = "battleroyale";
    public static final String MOD_NAME_SHORT = "cbr";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Random COMMON_RANDOM = new Random();

    protected static boolean initialized;
    protected static McSide mcSide = McSide.CLIENT;
    protected static MinecraftServer minecraftServer;
    private static IRegistrarFactory registrarFactory;

    public static void init(McSide mcSide, IRegistrarFactory factory) {
        if (initialized) return;

        BattleRoyale.mcSide = mcSide;
        BattleRoyale.registrarFactory = factory;

        ModConfigManager.init(mcSide);
        GameConfigManager.init(mcSide);

        ResourceLoader.INSTANCE.packType = mcSide.isClientSide() ? PackType.CLIENT_RESOURCES : PackType.SERVER_DATA;

        initialized = true;
    }

    public static McSide getMcSide() {
        return mcSide;
    }
    public static IRegistrarFactory getRegistrarFactory() {
        if (registrarFactory == null) {
            throw new IllegalStateException("Registrar factory has not been initialized. Call init() first.");
        }
        return registrarFactory;
    }
    public static void setMinecraftServer(MinecraftServer server) {
        minecraftServer = server;
    }
    public static MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }
    public static IGameManager getGameManager() {
        return GameManager.get();
    }
    public static IModConfigManager getModConfigManager() {
        return ModConfigManager.getApi();
    }
}
