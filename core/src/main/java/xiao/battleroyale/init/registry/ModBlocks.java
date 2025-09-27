package xiao.battleroyale.init.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
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

    public static final IRegistryObject<Block> LOOT_SPAWNER = BLOCKS.register("loot_spawner", LootSpawner::new);
    public static final IRegistryObject<Block> ENTITY_SPAWNER = BLOCKS.register("entity_spawner", EntitySpawner::new);
    public static final IRegistryObject<Block> ZONE_CONTROLLER = BLOCKS.register("zone_controller", ZoneController::new);

    public static final IRegistryObject<BlockEntityType<LootSpawnerBlockEntity>> LOOT_SPAWNER_BE =
            BLOCK_ENTITIES.register("loot_spawner", () ->
                    BlockEntityType.Builder.of(LootSpawnerBlockEntity::new, LOOT_SPAWNER.get()).build(null));
    public static final IRegistryObject<BlockEntityType<EntitySpawnerBlockEntity>> ENTITY_SPAWNER_BE =
            BLOCK_ENTITIES.register("entity_spawner", () ->
                    BlockEntityType.Builder.of(EntitySpawnerBlockEntity::new, ENTITY_SPAWNER.get()).build(null));
    public static final IRegistryObject<BlockEntityType<ZoneControllerBlockEntity>> ZONE_CONTROLLER_BE =
            BLOCK_ENTITIES.register("zone_controller", () ->
                    BlockEntityType.Builder.of(ZoneControllerBlockEntity::new, ZONE_CONTROLLER.get()).build(null));
}
