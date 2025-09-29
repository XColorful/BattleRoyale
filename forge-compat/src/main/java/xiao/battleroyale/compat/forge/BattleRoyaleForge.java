package xiao.battleroyale.compat.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.IEventPoster;
import xiao.battleroyale.api.event.IEventRegister;
import xiao.battleroyale.api.init.registry.IRegistrarFactory;
import xiao.battleroyale.api.minecraft.IMcRegistry;
import xiao.battleroyale.api.network.INetworkAdapter;
import xiao.battleroyale.api.network.INetworkHook;
import xiao.battleroyale.compat.forge.compat.journeymap.JmApi;
import xiao.battleroyale.compat.forge.compat.tacz.TaczEventRegister;
import xiao.battleroyale.compat.forge.event.ForgeEventPoster;
import xiao.battleroyale.compat.forge.event.ForgeEventRegister;
import xiao.battleroyale.compat.forge.init.registry.ForgeRegistrarFactory;
import xiao.battleroyale.compat.forge.minecraft.ForgeRegistry;
import xiao.battleroyale.compat.forge.network.ForgeNetworkAdapter;
import xiao.battleroyale.compat.forge.network.ForgeNetworkHook;
import xiao.battleroyale.init.registry.*;

@Mod(BattleRoyale.MOD_ID)
public class BattleRoyaleForge {

    private final IRegistrarFactory registrarFactory;
    private final IMcRegistry mcRegistry;
    private final INetworkAdapter networkAdapter;
    private final INetworkHook networkHook;
    private final IEventRegister eventRegister;
    private final IEventPoster eventPoster;
    private final BattleRoyale.CompatApi compatApi;

    public BattleRoyaleForge() {
        this.registrarFactory = new ForgeRegistrarFactory();
        this.mcRegistry = new ForgeRegistry();
        this.networkAdapter = new ForgeNetworkAdapter();
        this.networkHook = new ForgeNetworkHook();
        this.eventRegister = new ForgeEventRegister();
        this.eventPoster = new ForgeEventPoster();
        this.compatApi = new BattleRoyale.CompatApi(JmApi.get(), TaczEventRegister.get());
        Dist dist = FMLLoader.getDist();
        McSide mcSide = dist.isClient() ? McSide.CLIENT : McSide.DEDICATED_SERVER;

        BattleRoyale.init(mcSide, this.registrarFactory, this.mcRegistry, this.networkAdapter, this.networkHook, this.eventRegister, this.eventPoster, this.compatApi);

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