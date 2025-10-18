package xiao.battleroyale.compat.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.render.IBlockModelRenderer;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.IEventRegister;
import xiao.battleroyale.api.init.registry.IRegistrarFactory;
import xiao.battleroyale.api.minecraft.IMcRegistry;
import xiao.battleroyale.api.network.INetworkAdapter;
import xiao.battleroyale.api.network.INetworkHook;
import xiao.battleroyale.compat.neoforge.client.renderer.NeoBlockModelRenderer;
import xiao.battleroyale.compat.neoforge.compat.journeymap.JmApi;
import xiao.battleroyale.compat.neoforge.event.NeoEventRegister;
import xiao.battleroyale.compat.neoforge.init.registry.NeoRegistrarFactory;
import xiao.battleroyale.compat.neoforge.minecraft.NeoRegistry;
import xiao.battleroyale.compat.neoforge.network.NeoNetworkAdapter;
import xiao.battleroyale.compat.neoforge.network.NeoNetworkHook;
import xiao.battleroyale.init.registry.*;

@Mod(BattleRoyale.MOD_ID)
public class BattleRoyaleNeoforge {

    public static IRegistrarFactory registrarFactory;
    public static IMcRegistry mcRegistry;
    public static INetworkAdapter networkAdapter;
    public static INetworkHook networkHook;
    public static IEventRegister eventRegister;
    public static IBlockModelRenderer blockModelRenderer;
    public static BattleRoyale.CompatApi compatApi;

    public BattleRoyaleNeoforge(IEventBus modEventBus) {

        BattleRoyaleNeoforge.registrarFactory = new NeoRegistrarFactory();
        BattleRoyaleNeoforge.mcRegistry = new NeoRegistry();
        BattleRoyaleNeoforge.networkAdapter = new NeoNetworkAdapter();
        BattleRoyaleNeoforge.networkHook = new NeoNetworkHook();
        BattleRoyaleNeoforge.eventRegister = new NeoEventRegister();
        BattleRoyaleNeoforge.blockModelRenderer = new NeoBlockModelRenderer();
        BattleRoyaleNeoforge.compatApi = new BattleRoyale.CompatApi(JmApi.get());

        Dist dist = FMLLoader.getDist();
        McSide mcSide = dist.isClient() ? McSide.CLIENT : McSide.DEDICATED_SERVER;

        BattleRoyale.init(mcSide,
                BattleRoyaleNeoforge.registrarFactory, BattleRoyaleNeoforge.mcRegistry,
                BattleRoyaleNeoforge.networkAdapter, BattleRoyaleNeoforge.networkHook,
                BattleRoyaleNeoforge.eventRegister,
                BattleRoyaleNeoforge.blockModelRenderer,
                BattleRoyaleNeoforge.compatApi
        );

        // NeoForge 自动将 modEventBus 传入构造函数，无需 FMLJavaModLoadingContext.get()
        // IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.registerAll(modEventBus);
        ModBlocks.BLOCK_ENTITIES.registerAll(modEventBus);
        ModCreativeTabs.TABS.registerAll(modEventBus);
        ModItems.ITEMS.registerAll(modEventBus);
        ModEntities.ENTITY_TYPES.registerAll(modEventBus);
        ModMenuTypes.MENU_TYPES.registerAll(modEventBus);
        ModSounds.SOUNDS.registerAll(modEventBus);

        // NeoForge.EVENT_BUS.register(this);
    }
}