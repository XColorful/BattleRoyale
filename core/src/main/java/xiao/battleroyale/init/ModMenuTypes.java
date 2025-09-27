package xiao.battleroyale.init;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;
import xiao.battleroyale.inventory.LootSpawnerMenu;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, "battleroyale");

    public static final RegistryObject<MenuType<LootSpawnerMenu>> LOOT_SPAWNER_MENU =
            MENU_TYPES.register("loot_spawner_menu", () -> IForgeMenuType.create((windowId, inv, extraData) -> {
                BlockPos pos = extraData.readBlockPos();
                LootSpawnerBlockEntity blockEntity = (LootSpawnerBlockEntity) inv.player.level().getBlockEntity(pos);
                return new LootSpawnerMenu(null, windowId, inv, blockEntity);
            }));
}