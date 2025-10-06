package xiao.battleroyale.developer.debug;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.developer.debug.text.WorldText;

import java.util.*;

public class DebugWorld {

    private static class DebugWorldHolder {
        private static final DebugWorld INSTANCE = new DebugWorld();
    }

    public static DebugWorld get() {
        return DebugWorldHolder.INSTANCE;
    }

    private DebugWorld() {
        ;
    }

    /**
     * [调试]getBlockEntitiesNBT
     */
    public static final String GET_BLOCKENTITIES_NBT = "getBlockEntitiesNBT";
    public void getBlockEntitiesNbt(CommandSourceStack source, Vec3 pos) {
        ServerLevel serverLevel = source.getLevel();
        ChunkPos chunkPos = new ChunkPos(BlockPos.containing(pos));
        LevelChunk chunk = serverLevel.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
        Map<BlockPos, BlockEntity> blockEntities = chunk != null ? chunk.getBlockEntities() : null;

        DebugManager.sendDebugMessage(source, GET_BLOCKENTITIES_NBT, WorldText.buildBlockEntitesNbt(serverLevel, blockEntities));
    }

    /**
     * [调试]getBlockEntityNBT
     */
    public static final String GET_BLOCKENTITY_NBT = "getBlockEntityNBT";
    public void getBLockEntityNbt(CommandSourceStack source, Vec3 pos) {
        ServerLevel serverLevel = source.getLevel();
        BlockPos blockPos = BlockPos.containing(pos);
        BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);

        DebugManager.sendDebugMessage(source, GET_BLOCKENTITY_NBT, WorldText.buildBlockEntityNbt(serverLevel, blockPos, blockEntity));
    }

    /**
     * [调试]getPlayerItemStacks
     */
    public static final String GET_PLAYER_ITEMSTACKS = "getItemStacks";
    public void getPlayerItemStacks(CommandSourceStack source, ServerPlayer player, int min, int max) {
        Inventory inventory = player.getInventory();
        min = Math.max(0, min);
        max = Math.min(max, inventory.getContainerSize() - 1);
        List<Integer> slotIndex = new ArrayList<>();
        List<ItemStack> itemStacks = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (!itemStack.isEmpty()) {
                slotIndex.add(i);
                itemStacks.add(itemStack);
            }
        }
        DebugManager.sendDebugMessage(source, GET_PLAYER_ITEMSTACK, WorldText.buildItemStacks(player, slotIndex, itemStacks));
    }

    /**
     * [调试]getPlayerItemStack
     */
    public static final String GET_PLAYER_ITEMSTACK = "getItemStack";
    public void getPlayerItemStack(CommandSourceStack source, ServerPlayer player) {
        ItemStack itemStack = player.getInventory().getSelected();
        DebugManager.sendDebugMessage(source, GET_PLAYER_ITEMSTACK, WorldText.buildItemStack(itemStack));
    }

    /**
     * [调试]getBiome
     */
    public static final String GET_BIOME = "getBiome";
    public void getBiome(CommandSourceStack source, Vec3 pos) {
        ServerLevel serverLevel = source.getLevel();
        BlockPos blockPos = BlockPos.containing(pos);
        Holder<Biome> biomeHolder = serverLevel.getBiome(blockPos);
        Optional<ResourceKey<Biome>> biomeKeyOptional = biomeHolder.unwrapKey();
        ResourceKey<Biome> biomeRK = biomeKeyOptional.orElse(null);

        ResourceLocation biomeRL = biomeRK != null ? biomeRK.location() : null;
        DebugManager.sendDebugMessage(source, GET_BIOME, WorldText.buildBiome(blockPos, biomeRL, biomeRK));
    }

    /**
     * [调试]getStructures
     */
    public static final String GET_STRUCTURES = "getStructures";
    public void getStructures(CommandSourceStack source, Vec3 pos) {
        ServerLevel serverLevel = source.getLevel();
        BlockPos blockPos = BlockPos.containing(pos);
        StructureManager structureManager = serverLevel.structureManager();

        List<ResourceKey<Structure>> allStructuresKeys = serverLevel.registryAccess().registryOrThrow(Registries.STRUCTURE)
                .keySet().stream()
                .map(key -> ResourceKey.create(Registries.STRUCTURE, key))
                .toList();

        Map<ResourceLocation, ResourceKey<Structure>> structures = new HashMap<>();

        for (ResourceKey<Structure> structureKey : allStructuresKeys) {
            StructureStart structureStart = structureManager.getStructureWithPieceAt(blockPos, structureKey);
            if (structureStart != StructureStart.INVALID_START) {
                structures.put(structureKey.location(), structureKey);
            }
        }

        DebugManager.sendDebugMessage(source, GET_STRUCTURES, WorldText.buildStructures(blockPos, structures));
    }

    public static final String GET_SERVER_LEVEL = "getServerLevel";
    public void getServerLevel(CommandSourceStack source, String levelKeyString) {
        ResourceKey<Level> levelKey = ResourceKey.create(Registries.DIMENSION, BattleRoyale.getMcRegistry().createResourceLocation(levelKeyString));
        ServerLevel serverLevel = BattleRoyale.getMinecraftServer().getLevel(levelKey);

        DebugManager.sendDebugMessage(source, GET_SERVER_LEVEL, WorldText.buildServerLevel(serverLevel, levelKey, levelKeyString));
    }

    /**
     * [调试]getLevelKey
     */
    public static final String GET_LEVEL_KEY = "getLevelKey";
    public void getLevelKey(CommandSourceStack source) {
        ServerLevel serverLevel = source.getLevel();

        DebugManager.sendDebugMessage(source, GET_LEVEL_KEY, WorldText.buildLevelKey(serverLevel));
    }
}
