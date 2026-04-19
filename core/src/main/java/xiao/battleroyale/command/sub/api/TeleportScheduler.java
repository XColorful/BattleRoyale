package xiao.battleroyale.command.sub.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.util.GameUtils;
import xiao.battleroyale.util.WorldUtils;

import java.time.LocalDateTime;
import java.util.*;

public abstract class TeleportScheduler<T> implements IEventHandler {

    public static boolean createWithGamePlayers(List<Vec3> teleportPos, boolean findGround, int maxHangTime, List<GamePlayer> gamePlayers) {
        if (teleportPos.isEmpty()) return false;
        new GamePlayerTeleportScheduler(teleportPos, findGround, maxHangTime, gamePlayers).register();
        return true;
    }
    public static boolean createWithPlayers(@NotNull ResourceKey<Level> levelKey, List<Vec3> teleportPos, boolean findGround, int maxHangTime, Collection<? extends Entity> livingEntities) {
        if (teleportPos.isEmpty()) return false;
        List<UUID> playersUUID = livingEntities.stream()
                .filter(LivingEntity.class::isInstance)
                .map(Entity::getUUID)
                .toList();
        if (playersUUID.isEmpty()) return false;
        return new PlayerTeleportScheduler(levelKey, teleportPos, findGround, maxHangTime, playersUUID).register();
    }
    private TeleportScheduler(List<Vec3> teleportPos, boolean findGround, int maxHangTime, List<T> players) {
        this.startTime = String.valueOf(LocalDateTime.now());
        BattleRoyale.LOGGER.debug("Created a TeleportScheduler at {}", this.startTime);
        this.teleportPos = new ArrayList<>(teleportPos);
        this.findGround = findGround;
        this.maxHangTime = maxHangTime;
        this.tickRemain = this.maxHangTime;
        this.players = new ArrayList<>(players);
    }

    private final String startTime;
    private final List<Vec3> teleportPos;
    private int posIndex = 0;
    private Vec3 getCurrentPos() {
        return teleportPos.get(posIndex);
    }
    private void nextPosIndex() {
        if (++posIndex >= teleportPos.size()) posIndex = 0;
    }
    private final double queuedHeight = 1145.14; // findGround失败的时候临时反复传送到这个高度，直到区块能成功加载或达到最大时长
    private final boolean findGround;
    private final int maxHangTime;
    private int tickRemain;
    private final List<T> players;
    protected abstract @Nullable LivingEntity resolveEntity(T target);
    protected @Nullable ServerLevel cachedServerLevel;
    protected abstract void getAndSetServerLevel();

    @ApiStatus.Internal
    protected boolean register() {
        return BattleRoyale.getEventRegister().register(this, EventType.SERVER_TICK_EVENT, EventPriority.HIGH, true);
    }
    @ApiStatus.Internal
    protected boolean unregister() {
        return BattleRoyale.getEventRegister().unregister(this, EventType.SERVER_TICK_EVENT, EventPriority.HIGH, true);
    }

    @Override
    public String getEventHandlerName() {
        return "AlgorithmCommand::TeleportScheduler$" + startTime;
    }
    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.SERVER_TICK_EVENT) {
            onTeleportTick((IServerTickEvent) event);
        } else {
            onReceiveWrongEvent(eventType);
            BattleRoyale.LOGGER.warn("{} may be registered wrongly, developer should not manually register this to any event", getEventHandlerName());
            this.unregister();
        }
    }
    protected void onTeleportTick(IServerTickEvent event) {
        try {
            getAndSetServerLevel();
            if (this.cachedServerLevel != null) {
                @NotNull ServerLevel serverLevel = this.cachedServerLevel; // 局部变量减少多次访问
                Iterator<T> iterator = this.players.iterator();
                while (iterator.hasNext()) {
                    @Nullable LivingEntity player = resolveEntity(iterator.next());
                    if (player == null) continue; // 保留离线玩家尝试次数

                    // 获取点位
                    Vec3 pos = getCurrentPos();
                    if (findGround) {
                        int groundY = WorldUtils.getGroundY(serverLevel, pos.x, pos.z);
                        double targetY = groundY + 1.0;
                        if (!WorldUtils.isGroundValid(serverLevel, targetY)) {
                            pos = new Vec3(pos.x, queuedHeight, pos.z);
                        }
                    }

                    // 传送
                    BattleRoyale.getGameManager().safeTeleport(player, serverLevel, pos, 0, 0); // TeleportScheduler传送

                    // 传送后移除玩家
                    if (pos.y != queuedHeight) {
                        iterator.remove();
                        nextPosIndex();
                    }
                }
            }
            // 正常传送完玩家
            if (players.isEmpty()) {
                BattleRoyale.LOGGER.debug("{} complete teleport", getEventHandlerName());
                unregister();
                return;
            }
        } catch (NullPointerException e) {
            BattleRoyale.LOGGER.error("Encountered an exception during TeleportScheduler's onTeleportTick (started at {}) : {}", startTime, e.getMessage());
            unregister();
            return;
        } finally {
            this.cachedServerLevel = null;
        }

        // 传送超时，强制取消注册
        if (--this.tickRemain <= 0) {
            BattleRoyale.LOGGER.debug("{} reached maxHangTime {} and unregister", getEventHandlerName(), this.maxHangTime);
            unregister();
        }
    }

    // --------具体子类--------

    private static class GamePlayerTeleportScheduler extends TeleportScheduler<GamePlayer> {

        private GamePlayerTeleportScheduler(List<Vec3> teleportPos, boolean findGround, int maxHangTime, List<GamePlayer> gamePlayers) {
            super(teleportPos, findGround, maxHangTime, gamePlayers);
        }

        @Override
        protected @Nullable LivingEntity resolveEntity(GamePlayer target) {
            assert this.cachedServerLevel != null;
            return GameUtils.getLivingEntity(this.cachedServerLevel, target.getPlayerUUID());
        }

        @Override
        protected void getAndSetServerLevel() {
            this.cachedServerLevel = BattleRoyale.getGameManager().getServerLevel();
        }
    }

    private static class PlayerTeleportScheduler extends TeleportScheduler<UUID> {
        private final @NotNull ResourceKey<Level> levelKey;

        private PlayerTeleportScheduler(@NotNull ResourceKey<Level> levelKey, List<Vec3> teleportPos, boolean findGround, int maxHangTime, List<UUID> playersUUID) {
            super(teleportPos, findGround, maxHangTime, playersUUID);
            this.levelKey = levelKey;
        }

        @Override
        protected @Nullable LivingEntity resolveEntity(UUID playerUUID) {
            assert this.cachedServerLevel != null;
            @Nullable Entity entity = GameUtils.getEntity(this.cachedServerLevel, playerUUID);
            return entity instanceof LivingEntity livingEntity ? livingEntity : null;
        }

        @Override
        protected void getAndSetServerLevel() {
            this.cachedServerLevel = BattleRoyale.getMinecraftServer().getLevel(levelKey);
        }
    }
}
