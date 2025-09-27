package xiao.battleroyale.compat.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.init.registry.IRegistrarFactory;
import xiao.battleroyale.compat.forge.init.registry.ForgeRegistrarFactory;
import xiao.battleroyale.init.registry.*;

@Mod(BattleRoyale.MOD_ID)
public class BattleRoyaleForge {

    private final IRegistrarFactory registrarFactory;

    public BattleRoyaleForge() {
        this.registrarFactory = new ForgeRegistrarFactory();

        Dist dist = FMLLoader.getDist();
        McSide mcSide = dist.isClient() ? McSide.CLIENT : McSide.DEDICATED_SERVER;

        BattleRoyale.init(mcSide, this.registrarFactory);

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