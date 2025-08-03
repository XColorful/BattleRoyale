package xiao.battleroyale.developer.debug.text;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.common.game.loot.GameLootManager;
import xiao.battleroyale.common.loot.CommonLootManager;
import xiao.battleroyale.util.GameUtils.GameTimeFormat;
import xiao.battleroyale.util.StringUtils;

import java.util.UUID;

import static xiao.battleroyale.util.CommandUtils.buildHoverableText;
import static xiao.battleroyale.util.CommandUtils.buildHoverableTextWithColor;

public class LootText {

    /**
     * 悬浮查看CommonLootManager各个状态
     */
    public static MutableComponent buildCommonLoot(CommonLootManager commonLootManager) {
        MutableComponent component = Component.empty();
        if (commonLootManager == null) {
            return component;
        }

        // config
        MutableComponent configComponent = Component.empty()
                .append(Component.literal("MaxChunkPerTick"))
                .append(Component.literal(":" + CommonLootManager.getMaxChunksPerTick()));
        component.append(buildHoverableText("config", configComponent));
        component.append(Component.literal(" "));
        
        // progress
        int p1 = commonLootManager.chunksToProcessSize();
        int p2 = commonLootManager.processedChunkTrackerSize();
        UUID p3 = commonLootManager.getCurrentGenerationGameId();
        ServerLevel p4 = commonLootManager.getCurrentGenerationLevel();
        int p5 = commonLootManager.totalLootRefreshedInBatch();
        MutableComponent progressComponent = Component.empty()
                .append(Component.literal("chunksToProcess").withStyle(p1 > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + p1))
                .append(Component.literal("\n"))
                .append(Component.literal("processedChunkTracker").withStyle(p2 > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + p2))
                .append(Component.literal("\n"))
                .append(Component.literal("currentGameId").withStyle(p3 != null ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + p3))
                .append(Component.literal("\n"))
                .append(Component.literal("currentLevel").withStyle(p4 != null ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + p4))
                .append(Component.literal("\n"))
                .append(Component.literal("totalRefreshed").withStyle(p5 > 0 ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + p5));
        component.append(buildHoverableTextWithColor("progress", progressComponent, p1 > 0 ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY));

        return component;
    }

    /**
     * 悬浮查看GameLootManager各个状态
     */
    public static MutableComponent buildGameLoot(GameLootManager gameLootManager, Vec3 pos) {
        MutableComponent component = Component.empty();
        if (gameLootManager == null) {
            return component;
        }

        // config
        MutableComponent configComponent = Component.empty()
                .append(Component.literal("MaxLootChunkPerTick"))
                .append(Component.literal(":" + gameLootManager.getMaxLootChunkPerTick()))
                .append(Component.literal("\n"))
                .append(Component.literal("MaxLootDistance"))
                .append(Component.literal(":" + gameLootManager.getMaxLootDistance()))
                .append(Component.literal("\n"))
                .append(Component.literal("TolerantCenterDistance"))
                .append(Component.literal(":" + gameLootManager.getTolerantCenterDistance()))
                .append(Component.literal("\n"))
                .append(Component.literal("MaxCachedCenter"))
                .append(Component.literal(":" + gameLootManager.getMaxCachedCenter()))
                .append(Component.literal("\n"))
                .append(Component.literal("MaxQueuedChunk"))
                .append(Component.literal(":" + gameLootManager.getMaxQueuedChunk()))
                .append(Component.literal("\n"))
                .append(Component.literal("BfsFrequency"))
                .append(Component.literal(":" + gameLootManager.getBfsFrequency()))
                .append(Component.literal("\n"))
                .append(Component.literal("InstantNextBfs").withStyle(gameLootManager.isInstantNextBfs() ? ChatFormatting.AQUA : ChatFormatting.GRAY))
                .append(Component.literal(":" + gameLootManager.isInstantNextBfs()))
                .append(Component.literal("\n"))
                .append(Component.literal("MaxCachedLootChunk"))
                .append(Component.literal(":" + gameLootManager.getMaxCachedLootChunk()))
                .append(Component.literal("\n"))
                .append(Component.literal("CleanCachedChunk"))
                .append(Component.literal(":" + gameLootManager.getCleanCachedChunk()));
        component.append(buildHoverableTextWithColor("config", configComponent, ChatFormatting.GRAY));
        component.append(Component.literal(" "));

        // progress
        GameTimeFormat gameTimeFormat = new GameTimeFormat(gameLootManager.getLastBfsTime());
        int p2 = gameLootManager.getLastBfsProcessedLoot();
        MutableComponent progressComponent = Component.empty()
                .append(Component.literal("lastBfsTime"))
                .append(Component.literal(":" + gameTimeFormat.gameTime() + gameTimeFormat.toSpaceFullString(true)))
                .append(Component.literal("\n"))
                .append(Component.literal("lastBfsProcessedLoot").withStyle(p2 > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + p2))
                .append(Component.literal("\n"))
                .append(Component.literal("queuedChunksRef"))
                .append(Component.literal(":" + gameLootManager.queuedChunksRefSize()))
                .append(Component.literal("\n"))
                .append(Component.literal("processedChunkCache"))
                .append(Component.literal(":" + gameLootManager.processedChunkCacheSize()))
                .append(Component.literal("\n"))
                .append(Component.literal("cachedPlayerCenterChunk"))
                .append(Component.literal(":" + gameLootManager.cachedPlayerCenterChunksSize()))
                .append(Component.literal("\n"))
                .append(Component.literal("cachedCenterOffset"))
                .append(Component.literal(":" + gameLootManager.cachedCenterOffsetSize()));
        component.append(buildHoverableTextWithColor("progress", progressComponent, p2 > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY));
        component.append(Component.literal(" "));

        // xyz status
        ChunkPos chunkPos = new ChunkPos(BlockPos.containing(pos));
        boolean s1 = gameLootManager.isInQueuedChunksRef(chunkPos);
        boolean s2 = gameLootManager.isInProcessedChunkCache(chunkPos);
        boolean s3 = gameLootManager.isInCachedCenterOffset(chunkPos);
        MutableComponent posStatusComponent = Component.empty()
                .append(Component.literal("chunkPos"))
                .append(Component.literal(":" + chunkPos))
                .append(Component.literal("\n"))
                .append(Component.literal("inQueuedChunksRef").withStyle(s1 ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + s1))
                .append(Component.literal("\n"))
                .append(Component.literal("inProcessedChunkCache").withStyle(s2 ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + s2))
                .append(Component.literal("\n"))
                .append(Component.literal("inCachedCenterOffset").withStyle(s3 ? ChatFormatting.YELLOW : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + s3));
        component.append(buildHoverableTextWithColor(StringUtils.vectorTo2fString(pos), posStatusComponent, ChatFormatting.AQUA));

        return component;
    }
}
