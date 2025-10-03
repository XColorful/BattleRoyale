package xiao.battleroyale.compat.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.render.IBlockModelRenderer;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.IEventPoster;
import xiao.battleroyale.api.event.IEventRegister;
import xiao.battleroyale.api.init.registry.IRegistrarFactory;
import xiao.battleroyale.api.minecraft.IMcRegistry;
import xiao.battleroyale.api.network.INetworkAdapter;
import xiao.battleroyale.api.network.INetworkHook;
import xiao.battleroyale.compat.forge.client.renderer.ForgeBlockModelRenderer;
import xiao.battleroyale.compat.forge.compat.journeymap.JmApi;
import xiao.battleroyale.compat.forge.event.ForgeEventPoster;
import xiao.battleroyale.compat.forge.event.ForgeEventRegister;
import xiao.battleroyale.compat.forge.init.registry.ForgeRegistrarFactory;
import xiao.battleroyale.compat.forge.minecraft.ForgeRegistry;
import xiao.battleroyale.compat.forge.network.ForgeNetworkAdapter;
import xiao.battleroyale.compat.forge.network.ForgeNetworkHook;
import xiao.battleroyale.init.registry.*;

@Mod(BattleRoyale.MOD_ID)
public class BattleRoyaleForge {

    public static IRegistrarFactory registrarFactory;
    public static IMcRegistry mcRegistry;
    public static INetworkAdapter networkAdapter;
    public static INetworkHook networkHook;
    public static IEventRegister eventRegister;
    public static IEventPoster eventPoster;
    public static IBlockModelRenderer blockModelRenderer;
    public static BattleRoyale.CompatApi compatApi;

    public BattleRoyaleForge() {
        BattleRoyaleForge.registrarFactory = new ForgeRegistrarFactory();
        BattleRoyaleForge.mcRegistry = new ForgeRegistry();
        BattleRoyaleForge.networkAdapter = new ForgeNetworkAdapter();
        BattleRoyaleForge.networkHook = new ForgeNetworkHook();
        BattleRoyaleForge.eventRegister = new ForgeEventRegister();
        BattleRoyaleForge.eventPoster = new ForgeEventPoster();
        BattleRoyaleForge.blockModelRenderer = new ForgeBlockModelRenderer();
        BattleRoyaleForge.compatApi = new BattleRoyale.CompatApi(JmApi.get());

        Dist dist = FMLLoader.getDist();
        McSide mcSide = dist.isClient() ? McSide.CLIENT : McSide.DEDICATED_SERVER;

        BattleRoyale.init(mcSide,
                BattleRoyaleForge.registrarFactory, BattleRoyaleForge.mcRegistry,
                BattleRoyaleForge.networkAdapter, BattleRoyaleForge.networkHook,
                BattleRoyaleForge.eventRegister, BattleRoyaleForge.eventPoster,
                BattleRoyaleForge.blockModelRenderer,
                BattleRoyaleForge.compatApi);

        // 确保所有 ModXXX 静态字段被初始化
        try {
            Class.forName(ModBlocks.class.getName());
            Class.forName(ModCreativeTabs.class.getName());
            Class.forName(ModItems.class.getName());
            Class.forName(ModEntities.class.getName());
            Class.forName(ModMenuTypes.class.getName());
            Class.forName(ModSounds.class.getName());
        } catch (ClassNotFoundException e) {
            BattleRoyale.LOGGER.error("Failed to load core registrar class: {}", e.getMessage());
        }

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.registerAll(modEventBus);
        ModBlocks.BLOCK_ENTITIES.registerAll(modEventBus);
        ModCreativeTabs.TABS.registerAll(modEventBus);
        ModItems.ITEMS.registerAll(modEventBus);
        ModEntities.ENTITY_TYPES.registerAll(modEventBus);
        ModMenuTypes.MENU_TYPES.registerAll(modEventBus);
        ModSounds.SOUNDS.registerAll(modEventBus);
    }
}