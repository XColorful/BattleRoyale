package xiao.battleroyale;

import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;
import xiao.battleroyale.api.config.IModConfigManager;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.ModConfigManager;
import xiao.battleroyale.init.*;
import xiao.battleroyale.resource.ResourceLoader;

import java.util.Random;

@Mod(BattleRoyale.MOD_ID)
public class BattleRoyale {
    public static final String MOD_ID = "battleroyale";
    public static final String MOD_NAME_SHORT = "cbr";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Random COMMON_RANDOM = new Random();

    private static MinecraftServer minecraftServer;

    public BattleRoyale(FMLJavaModLoadingContext context) {
        Dist dist = FMLLoader.getDist();
        ModConfigManager.init(dist);
        GameManager.init(dist);
        ResourceLoader.INSTANCE.packType = dist.isClient() ? PackType.CLIENT_RESOURCES : PackType.SERVER_DATA;

        IEventBus bus = context.getModEventBus();
        ModBlocks.BLOCKS.register(bus);
        ModBlocks.BLOCK_ENTITIES.register(bus);
        ModCreativeTabs.TABS.register(bus);
        ModItems.ITEMS.register(bus);
        ModEntities.ENTITY_TYPES.register(bus);
        ModMenuTypes.MENU_TYPES.register(bus);
        ModSounds.SOUNDS.register(bus);
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
