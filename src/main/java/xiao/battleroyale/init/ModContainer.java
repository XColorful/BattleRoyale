package xiao.battleroyale.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.inventory.LootSpawnerMenu;

public class ModContainer {
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPE = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BattleRoyale.MOD_ID);

    public static final RegistryObject<MenuType<LootSpawnerMenu>> LOOT_SPAWNER_MENU = CONTAINER_TYPE.register("loot_spawner_menu", () -> LootSpawnerMenu.TYPE);
}