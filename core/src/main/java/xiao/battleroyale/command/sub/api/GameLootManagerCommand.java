package xiao.battleroyale.command.sub.api;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.util.WorldUtils;

import static xiao.battleroyale.command.CommandArg.*;

public class GameLootManagerCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(GAME_LOOT_MANAGER)
                // IGameLootConfigGetter
                .then(Commands.literal(GET_MAX_LOOT_CHUNK_PER_TICK).executes(GameLootManagerCommand::getMaxLootChunkPerTick))
                .then(Commands.literal(GET_MAX_LOOT_DISTANCE).executes(GameLootManagerCommand::getMaxLootDistance))
                .then(Commands.literal(GET_TOLERANT_CENTER_DISTANCE).executes(GameLootManagerCommand::getTolerantCenterDistance))
                .then(Commands.literal(GET_MAX_CACHED_CENTER).executes(GameLootManagerCommand::getMaxCachedCenter))
                .then(Commands.literal(GET_MAX_QUEUED_CHUNK).executes(GameLootManagerCommand::getMaxQueuedChunk))
                .then(Commands.literal(GET_BFS_FREQUENCY).executes(GameLootManagerCommand::getBfsFrequency))
                .then(Commands.literal(IS_INSTANT_NEXT_BFS).executes(GameLootManagerCommand::isInstantNextBfs))
                .then(Commands.literal(GET_MAX_CACHED_LOOT_CHUNK).executes(GameLootManagerCommand::getMaxCachedLootChunk))
                .then(Commands.literal(GET_CLEAN_CACHED_CHUNK).executes(GameLootManagerCommand::getCleanCachedChunk))
                .then(Commands.literal(GET_SIMULATION_DISTANCE).executes(GameLootManagerCommand::getSimulationDistance))
                // IGameLootStatus
                .then(Commands.literal(GET_LAST_BFS_TIME).executes(GameLootManagerCommand::getLastBfsTime))
                .then(Commands.literal(GET_LAST_BFS_PROCESSED_LOOT).executes(GameLootManagerCommand::getLastBfsProcessedLoot))
                .then(Commands.literal(QUEUED_CHUNKS_REF_SIZE).executes(GameLootManagerCommand::queuedChunksRefSize))
                .then(Commands.literal(PROCESSED_CHUNK_CACHE_SIZE).executes(GameLootManagerCommand::processedChunkCacheSize))
                .then(Commands.literal(CACHED_PLAYER_CENTER_CHUNKS_SIZE).executes(GameLootManagerCommand::cachedPlayerCenterChunksSize))
                .then(Commands.literal(CACHED_CENTER_OFFSET_SIZE).executes(GameLootManagerCommand::cachedCenterOffsetSize))
                // IGameLootTester
                .then(Commands.literal(IS_IN_QUEUED_CHUNKS_REF)
                        .executes(GameLootManagerCommand::isInQueuedChunksRefAtExecute)
                        .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                .executes(GameLootManagerCommand::isInQueuedChunksRef)))
                .then(Commands.literal(IS_IN_PROCESSED_CHUNK_CACHE)
                        .executes(GameLootManagerCommand::isInProcessedChunkCacheAtExecute)
                        .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                .executes(GameLootManagerCommand::isInProcessedChunkCache)))
                .then(Commands.literal(IS_IN_CACHED_CENTER_OFFSET)
                        .executes(GameLootManagerCommand::isInCachedCenterOffsetAtExecute)
                        .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                .executes(GameLootManagerCommand::isInCachedCenterOffset)))

                // IGameLootOperator
                .then(Commands.literal(FORCE_CLEAR_QUEUED_CHUNK_REF).executes(GameLootManagerCommand::forceClearQueuedChunkRef))
                .then(Commands.literal(FORCE_CLEAR_PROCESSED_CHUNK_CACHE).executes(GameLootManagerCommand::forceClearProcessedChunkCache))
                .then(Commands.literal(FORCE_CLEAR_PLAYER_CENTER_CHUNKS).executes(GameLootManagerCommand::forceClearPlayerCenterChunks));
    }

    // --------IGameLootConfigGetter--------
    
    private static int getMaxLootChunkPerTick(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().getMaxLootChunkPerTick();
    }
    private static int getMaxLootDistance(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().getMaxLootDistance();
    }
    private static int getTolerantCenterDistance(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().getTolerantCenterDistance();
    }
    private static int getMaxCachedCenter(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().getMaxCachedCenter();
    }
    private static int getMaxQueuedChunk(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().getMaxQueuedChunk();
    }
    private static int getBfsFrequency(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().getBfsFrequency();
    }
    private static int isInstantNextBfs(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().isInstantNextBfs() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int getMaxCachedLootChunk(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().getMaxCachedLootChunk();
    }
    private static int getCleanCachedChunk(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().getCleanCachedChunk();
    }
    private static int getSimulationDistance(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().getSimulationDistance();
    }

    // --------IGameLootStatus--------
    
    private static int getLastBfsTime(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().getLastBfsTime();
    }
    private static int getLastBfsProcessedLoot(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().getLastBfsProcessedLoot();
    }
    private static int queuedChunksRefSize(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().queuedChunksRefSize();
    }
    private static int processedChunkCacheSize(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().processedChunkCacheSize();
    }
    private static int cachedPlayerCenterChunksSize(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().cachedPlayerCenterChunksSize();
    }
    private static int cachedCenterOffsetSize(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().cachedCenterOffsetSize();
    }

    // --------IGameLootTester--------

    private static int isInQueuedChunksRefAtExecute(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().isInQueuedChunksRef(WorldUtils.getChunkPos(context.getSource().getPosition())) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int isInQueuedChunksRef(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().isInQueuedChunksRef(WorldUtils.getChunkPos(Vec3Argument.getVec3(context, XYZ))) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int isInProcessedChunkCacheAtExecute(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().isInProcessedChunkCache(WorldUtils.getChunkPos(context.getSource().getPosition())) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int isInProcessedChunkCache(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().isInProcessedChunkCache(WorldUtils.getChunkPos(Vec3Argument.getVec3(context, XYZ))) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int isInCachedCenterOffsetAtExecute(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().isInCachedCenterOffset(WorldUtils.getChunkPos(context.getSource().getPosition())) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int isInCachedCenterOffset(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLootManager().isInCachedCenterOffset(WorldUtils.getChunkPos(Vec3Argument.getVec3(context, XYZ))) ? Command.SINGLE_SUCCESS : 0;
    }

    // --------IGameLootOperator--------

    private static int forceClearQueuedChunkRef(CommandContext<CommandSourceStack> context) {
        BattleRoyale.getGameManager().getGameLootManager().forceClearQueuedChunkRef();
        return Command.SINGLE_SUCCESS;
    }
    private static int forceClearProcessedChunkCache(CommandContext<CommandSourceStack> context) {
        BattleRoyale.getGameManager().getGameLootManager().forceClearProcessedChunkCache();
        return Command.SINGLE_SUCCESS;
    }
    private static int forceClearPlayerCenterChunks(CommandContext<CommandSourceStack> context) {
        BattleRoyale.getGameManager().getGameLootManager().forceClearPlayerCenterChunks();
        return Command.SINGLE_SUCCESS;
    }
}