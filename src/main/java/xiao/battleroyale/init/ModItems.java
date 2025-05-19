package xiao.battleroyale.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xiao.battleroyale.BattleRoyale;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BattleRoyale.MOD_ID);

    public static final RegistryObject<Item> LOOT_SPAWNER_ITEM = ITEMS.register("loot_spawner", () ->
            new BlockItem(ModBlocks.LOOT_SPAWNER.get(), new Item.Properties()));
    public static final RegistryObject<Item> ENTITY_SPAWNER_ITEM = ITEMS.register("entity_spawner", () ->
            new BlockItem(ModBlocks.ENTITY_SPAWNER.get(), new Item.Properties()));
}