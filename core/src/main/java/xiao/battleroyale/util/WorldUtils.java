package xiao.battleroyale.util;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.Collection;
import java.util.UUID;

public class WorldUtils {

    public static ChunkPos getChunkPos(Vec3 pos) {
        return new ChunkPos(BlockPos.containing(pos));
    }

    public static int getGroundY(@NotNull ServerLevel serverLevel, double x, double z) {
        int maxBuildHeight = serverLevel.getMaxY();
        BlockPos lookupPos = BlockPos.containing(x, maxBuildHeight, z);
        return serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, lookupPos.getX(), lookupPos.getZ());
    }
    /**
     * 是否非虚空
     */
    public static boolean isGroundValid(@NotNull ServerLevel serverLevel, double y) {
        return y >= serverLevel.getMinBuildHeight() + 2;
    }

    /**
     * 向单个玩家发送/更新进度条
     */
    public static void sendBossBar(@NotNull ServerPlayer player, @NotNull UUID barUUID,
                                   Component title, float barProgress, BossEvent.BossBarColor color) {
        sendBossBar(player, barUUID, title, barProgress,
                color, BossEvent.BossBarOverlay.PROGRESS);
    }
    /**
     * 向单个玩家发送/更新进度条
     */
    public static void sendBossBar(@NotNull ServerPlayer player, @NotNull UUID barUUID,
                                   Component title, float barProgress,
                                   BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay) {
        BossEvent dummyEvent = new BossEvent(barUUID, title, color, overlay) {
            @Override public float getProgress() { return barProgress; }
        };

        player.connection.send(ClientboundBossEventPacket.createAddPacket(dummyEvent));
        player.connection.send(ClientboundBossEventPacket.createUpdateProgressPacket(dummyEvent));
        player.connection.send(ClientboundBossEventPacket.createUpdateNamePacket(dummyEvent));
        player.connection.send(ClientboundBossEventPacket.createUpdateStylePacket(dummyEvent));
    }
    public static void sendBossBar(@NotNull ServerLevel serverLevel, Collection<GamePlayer> gamePlayers,
                                   @NotNull UUID barUUID, Component title, float barProgress,
                                   BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay) {
        BossEvent dummyEvent = new BossEvent(barUUID, title, color, overlay) {
            @Override public float getProgress() { return barProgress; }
        };
        ClientboundBossEventPacket addPacket = ClientboundBossEventPacket.createAddPacket(dummyEvent);
        ClientboundBossEventPacket progressPacket = ClientboundBossEventPacket.createUpdateProgressPacket(dummyEvent);
        ClientboundBossEventPacket namePacket = ClientboundBossEventPacket.createUpdateNamePacket(dummyEvent);
        ClientboundBossEventPacket stylePacket = ClientboundBossEventPacket.createUpdateStylePacket(dummyEvent);

        for (GamePlayer gamePlayer : gamePlayers) {
            @Nullable ServerPlayer serverPlayer = GameUtils.getServerPlayerOrNull(serverLevel, gamePlayer.getPlayerUUID());
            if (serverPlayer != null) {
                serverPlayer.connection.send(addPacket);
                serverPlayer.connection.send(progressPacket);
                serverPlayer.connection.send(namePacket);
                serverPlayer.connection.send(stylePacket);
            }
        }
    }

    /**
     * 强制移除进度条 (无视服务器是否存在实例，直接向客户端发包)
     */
    public static void removeBossBar(@NotNull ServerPlayer player, @NotNull UUID barUUID) {
        player.connection.send(ClientboundBossEventPacket.createRemovePacket(barUUID));
    }
    public static void removeBossBar(@NotNull ServerLevel serverLevel, Collection<GamePlayer> gamePlayers,
                                     @NotNull UUID barUUID) {
        ClientboundBossEventPacket removePacket = ClientboundBossEventPacket.createRemovePacket(barUUID);
        for (GamePlayer gamePlayer : gamePlayers) {
            @Nullable ServerPlayer serverPlayer = GameUtils.getServerPlayerOrNull(serverLevel, gamePlayer.getPlayerUUID());
            if (serverPlayer != null) {
                serverPlayer.connection.send(removePacket);
            }
        }
    }
}
