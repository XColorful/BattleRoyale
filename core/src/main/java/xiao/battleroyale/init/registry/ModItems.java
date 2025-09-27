package xiao.battleroyale.init.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.init.registry.IRegistrar;
import xiao.battleroyale.api.init.registry.IRegistryObject;

/**
 * 平台无关的物品注册器。
 */
public class ModItems {
    public static final IRegistrar<Item> ITEMS = BattleRoyale.getRegistrarFactory().createItems(BattleRoyale.MOD_ID);

    public static final IRegistryObject<Item> LOOT_SPAWNER_ITEM = ITEMS.register("loot_spawner", () ->
            new BlockItem(ModBlocks.LOOT_SPAWNER.get(), new Item.Properties()));
    public static final IRegistryObject<Item> ENTITY_SPAWNER_ITEM = ITEMS.register("entity_spawner", () ->
            new BlockItem(ModBlocks.ENTITY_SPAWNER.get(), new Item.Properties()));
    public static final IRegistryObject<Item> ZONE_CONTROLLER_ITEM = ITEMS.register("zone_controller", () ->
            new BlockItem(ModBlocks.ZONE_CONTROLLER.get(), new Item.Properties()));
}
