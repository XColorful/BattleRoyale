package xiao.battleroyale.compat.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.render.IBlockModelRenderer;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.IEventRegister;
import xiao.battleroyale.api.init.registry.IRegistrarFactory;
import xiao.battleroyale.api.minecraft.IMcRegistry;
import xiao.battleroyale.api.network.INetworkAdapter;
import xiao.battleroyale.api.network.INetworkHook;
import xiao.battleroyale.compat.fabric.compat.journeymap.JmApi;
import xiao.battleroyale.compat.fabric.compat.tacz.TaczEventRegister;
import xiao.battleroyale.compat.fabric.compat.tacz.TaczGunOperator;
import xiao.battleroyale.compat.fabric.init.FabricCommandRegistry;
import xiao.battleroyale.compat.fabric.init.FabricCommonSetup;
import xiao.battleroyale.compat.fabric.init.FabricCompatInit;
import xiao.battleroyale.compat.fabric.init.FabricModEvent;
import xiao.battleroyale.compat.fabric.init.registry.FabricRegistrarFactory;
import xiao.battleroyale.compat.fabric.minecraft.FabricRegistry;
import xiao.battleroyale.compat.fabric.network.FabricNetworkAdapter;
import xiao.battleroyale.compat.fabric.network.FabricNetworkHook;
import xiao.battleroyale.compat.fabric.event.FabricEventRegister;
import xiao.battleroyale.compat.fabric.client.renderer.FabricBlockModelRenderer;
import xiao.battleroyale.init.registry.*;

public class BattleRoyaleFabric implements ModInitializer {

    public static IRegistrarFactory registrarFactory;
    public static IMcRegistry mcRegistry;
    public static INetworkAdapter networkAdapter;
    public static INetworkHook networkHook;
    public static IEventRegister eventRegister;
    public static IBlockModelRenderer blockModelRenderer;
    public static BattleRoyale.CompatApi compatApi;

    @Override
    public void onInitialize() {
        BattleRoyaleFabric.registrarFactory = new FabricRegistrarFactory();
        BattleRoyaleFabric.mcRegistry = new FabricRegistry();
        BattleRoyaleFabric.networkAdapter = new FabricNetworkAdapter();
        BattleRoyaleFabric.networkHook = new FabricNetworkHook();
        BattleRoyaleFabric.eventRegister = new FabricEventRegister();
//        BattleRoyaleFabric.blockModelRenderer = new FabricBlockModelRenderer();
        BattleRoyaleFabric.compatApi = new BattleRoyale.CompatApi(JmApi.get(), TaczEventRegister.get(), TaczGunOperator.get());
        EnvType dist = FabricLoader.getInstance().getEnvironmentType();
        McSide mcSide = dist == EnvType.CLIENT ? McSide.CLIENT : McSide.DEDICATED_SERVER;

        if (dist == EnvType.CLIENT) {
            ClientOnlyHandler.initRenderer();
        } else {
            BattleRoyaleFabric.blockModelRenderer = null;
        }

        BattleRoyale.init(mcSide,
                BattleRoyaleFabric.registrarFactory, BattleRoyaleFabric.mcRegistry,
                BattleRoyaleFabric.networkAdapter, BattleRoyaleFabric.networkHook,
                BattleRoyaleFabric.eventRegister,
                BattleRoyaleFabric.blockModelRenderer,
                BattleRoyaleFabric.compatApi);

        Object modEventBus = null;

        ModBlocks.BLOCKS.registerAll(modEventBus);
        ModBlocks.BLOCK_ENTITIES.registerAll(modEventBus);
        ModCreativeTabs.TABS.registerAll(modEventBus);
        ModItems.ITEMS.registerAll(modEventBus);
        ModEntities.ENTITY_TYPES.registerAll(modEventBus);
        ModMenuTypes.MENU_TYPES.registerAll(modEventBus);
        ModSounds.SOUNDS.registerAll(modEventBus);

        if (dist == EnvType.CLIENT) {
            ClientOnlyHandler.init();
        }

        // 注册服务器生命周期监听 (Starting/Stopping)
        FabricModEvent.init();

        // 先注册，再 FMLCommonSetupEvent
        FabricCommonSetup.init();
        // 注册指令
        FabricCommandRegistry.init(dist);

        // 想个办法延迟这个 FMLLoadCompleteEvent 执行?
        FabricCompatInit.init();
    }

    /**
     * 内部类：将所有涉及客户端 API 的类引用隔离在这里
     */
    private static class ClientOnlyHandler {
        private static void initRenderer() {
            BattleRoyaleFabric.blockModelRenderer = new FabricBlockModelRenderer();
        }

        private static void init() {
            xiao.battleroyale.compat.fabric.client.init.FabricModEntityRender.init();
            xiao.battleroyale.compat.fabric.client.init.FabricClientSetup.init();
            xiao.battleroyale.compat.fabric.client.init.FabricClientModEvent.init();
        }
    }
}