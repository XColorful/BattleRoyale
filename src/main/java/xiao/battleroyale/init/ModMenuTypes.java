package xiao.battleroyale.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xiao.battleroyale.inventory.LootSpawnerMenu;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, "battleroyale");

    public static final RegistryObject<MenuType<LootSpawnerMenu>> LOOT_SPAWNER_MENU =
            MENU_TYPES.register("loot_spawner_menu", () -> LootSpawnerMenu.TYPE);

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}