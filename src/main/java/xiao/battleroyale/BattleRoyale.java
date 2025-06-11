package xiao.battleroyale;

import com.mojang.logging.LogUtils;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;
import xiao.battleroyale.config.ClientConfig;
import xiao.battleroyale.config.CommonConfig;
import xiao.battleroyale.init.*;
import xiao.battleroyale.resource.ResourceLoader;

@Mod(BattleRoyale.MOD_ID)
public class BattleRoyale {
    public static final String MOD_ID = "battleroyale";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BattleRoyale(FMLJavaModLoadingContext context)
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.init());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.init());

        Dist side = FMLLoader.getDist();
        ResourceLoader.INSTANCE.packType = side.isClient() ? PackType.CLIENT_RESOURCES : PackType.SERVER_DATA;

        IEventBus bus = context.getModEventBus();
        ModBlocks.BLOCKS.register(bus);
        ModBlocks.BLOCK_ENTITIES.register(bus);
        ModCreativeTabs.TABS.register(bus);
        ModItems.ITEMS.register(bus);
        ModEntities.ENTITY_TYPES.register(bus);
        ModMenuTypes.MENU_TYPES.register(bus);
        ModSounds.SOUNDS.register(bus);
    }
}
