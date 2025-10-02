package xiao.battleroyale.compat.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.render.IBlockModelRenderer;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.IEventPoster;
import xiao.battleroyale.api.event.IEventRegister;
import xiao.battleroyale.api.init.registry.IRegistrarFactory;
import xiao.battleroyale.api.minecraft.IMcRegistry;
import xiao.battleroyale.api.network.INetworkAdapter;
import xiao.battleroyale.api.network.INetworkHook;
import xiao.battleroyale.compat.neoforge.client.renderer.NeoBlockModelRenderer;
import xiao.battleroyale.compat.neoforge.compat.journeymap.JmApi;
import xiao.battleroyale.compat.neoforge.event.NeoEventPoster;
import xiao.battleroyale.compat.neoforge.event.NeoEventRegister;
import xiao.battleroyale.compat.neoforge.init.registry.NeoRegistrarFactory;
import xiao.battleroyale.compat.neoforge.minecraft.NeoRegistry;
import xiao.battleroyale.compat.neoforge.network.NeoNetworkAdapter;
import xiao.battleroyale.compat.neoforge.network.NeoNetworkHook;
import xiao.battleroyale.init.registry.*;

@Mod(BattleRoyale.MOD_ID)
public class BattleRoyaleNeoforge {

    private final IRegistrarFactory registrarFactory;
    private final IMcRegistry mcRegistry;
    private final INetworkAdapter networkAdapter;
    private final INetworkHook networkHook;
    private final IEventRegister eventRegister;
    private final IEventPoster eventPoster;
    private final IBlockModelRenderer blockModelRenderer;
    private final BattleRoyale.CompatApi compatApi;

    public BattleRoyaleNeoforge(IEventBus modEventBus) {

        this.registrarFactory = new NeoRegistrarFactory();
        this.mcRegistry = new NeoRegistry();
        this.networkAdapter = new NeoNetworkAdapter();
        this.networkHook = new NeoNetworkHook();
        this.eventRegister = new NeoEventRegister();
        this.eventPoster = new NeoEventPoster();
        this.blockModelRenderer = new NeoBlockModelRenderer();
        this.compatApi = new BattleRoyale.CompatApi(JmApi.get());

        Dist dist = FMLLoader.getDist();
        McSide mcSide = dist.isClient() ? McSide.CLIENT : McSide.DEDICATED_SERVER;

        BattleRoyale.init(mcSide,
                this.registrarFactory,
                this.mcRegistry,
                this.networkAdapter,
                this.networkHook,
                this.eventRegister,
                this.eventPoster,
                this.blockModelRenderer,
                this.compatApi
        );

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