package xiao.battleroyale.init.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.init.registry.IRegistrar;
import xiao.battleroyale.api.init.registry.IRegistryObject;
import xiao.battleroyale.block.EntitySpawner;
import xiao.battleroyale.block.LootSpawner;
import xiao.battleroyale.block.ZoneController;
import xiao.battleroyale.block.entity.EntitySpawnerBlockEntity;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;
import xiao.battleroyale.block.entity.ZoneControllerBlockEntity;

/**
 * 平台无关的方块注册器。
 */
public class ModBlocks {
    public static final IRegistrar<Block> BLOCKS =
            BattleRoyale.getRegistrarFactory().createBlocks(BattleRoyale.MOD_ID);
    public static final IRegistrar<BlockEntityType<?>> BLOCK_ENTITIES =
            BattleRoyale.getRegistrarFactory().createBlockEntities(BattleRoyale.MOD_ID);

    public static final IRegistryObject<Block> LOOT_SPAWNER = BLOCKS.register("loot_spawner", () ->
            new LootSpawner(BlockBehaviour.Properties.of()
                    .sound(SoundType.WOOD)
                    .strength(2.5F, 2.5F)
                    .noOcclusion()
                    .noCollission()));
    public static final IRegistryObject<Block> ENTITY_SPAWNER = BLOCKS.register("entity_spawner", () ->
            new EntitySpawner(BlockBehaviour.Properties.of()
                    .sound(SoundType.WOOD)
                    .strength(2.5F, 2.5F)
                    .noOcclusion()
                    .noCollission()));
    public static final IRegistryObject<Block> ZONE_CONTROLLER = BLOCKS.register("zone_controller", () ->
            new ZoneController(BlockBehaviour.Properties.of()));

    public static final IRegistryObject<BlockEntityType<LootSpawnerBlockEntity>> LOOT_SPAWNER_BE =
            BLOCK_ENTITIES.register("loot_spawner", () ->
                    new BlockEntityType<>(LootSpawnerBlockEntity::new, LOOT_SPAWNER.get()));
    public static final IRegistryObject<BlockEntityType<EntitySpawnerBlockEntity>> ENTITY_SPAWNER_BE =
            BLOCK_ENTITIES.register("entity_spawner", () ->
                    new BlockEntityType<>(EntitySpawnerBlockEntity::new, ENTITY_SPAWNER.get()));
    public static final IRegistryObject<BlockEntityType<ZoneControllerBlockEntity>> ZONE_CONTROLLER_BE =
            BLOCK_ENTITIES.register("zone_controller", () ->
                    new BlockEntityType<>(ZoneControllerBlockEntity::new, ZONE_CONTROLLER.get()));
}
