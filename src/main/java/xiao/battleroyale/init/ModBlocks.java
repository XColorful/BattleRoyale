package xiao.battleroyale.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.block.EntitySpawner;
import xiao.battleroyale.block.LootSpawner;
import xiao.battleroyale.block.entity.EntitySpawnerBlockEntity;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BattleRoyale.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BattleRoyale.MOD_ID);

    public static RegistryObject<Block> LOOT_SPAWNER = BLOCKS.register("loot_spawner", LootSpawner::new);
    public static RegistryObject<Block> ENTITY_SPAWNER = BLOCKS.register("entity_spawner", EntitySpawner::new);

    public static RegistryObject<BlockEntityType<LootSpawnerBlockEntity>> LOOT_SPAWNER_BE =
            BLOCK_ENTITIES.register("loot_spawner", () ->
                    BlockEntityType.Builder.of(LootSpawnerBlockEntity::new, LOOT_SPAWNER.get()).build(null));
    public static RegistryObject<BlockEntityType<EntitySpawnerBlockEntity>> ENTITY_SPAWNER_BE =
            BLOCK_ENTITIES.register("entity_spawner", () ->
                    BlockEntityType.Builder.of(EntitySpawnerBlockEntity::new, ENTITY_SPAWNER.get()).build(null));
}