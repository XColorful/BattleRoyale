package xiao.battleroyale.common.game.spawn.vanilla;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.spawn.type.detail.SpawnDetailTag;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameStatsManager;
import xiao.battleroyale.common.game.GameUtilsFunction;
import xiao.battleroyale.common.game.spawn.AbstractSimpleSpawner;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.detail.TeleportDetailEntry;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;
import xiao.battleroyale.api.game.spawn.type.SpawnTypeTag;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.StringUtils;

import java.util.*;
import java.util.function.Supplier;

/**
 * 传送所有玩家后就没什么事情了
 */
public class TeleportSpawner extends AbstractSimpleSpawner<TeleportDetailEntry> {

    // common 在父类

    // detail
    protected final List<Vec3> fixedPos = new ArrayList<>(); // 如果detailType为FIXED，列表又为空，则不传送
    protected final boolean teamTogether;
    protected final boolean findGround;
    protected final double randomRange;
    protected final int hangTime;
    protected final int fixedSimulationCount;
    protected final double playerFactorContribution;
    protected final boolean useGoldenSpiral;
    protected final boolean allowOnBorder;
    protected final double globalShrinkRatio;
    protected boolean needShuffle;

    protected final List<Vec3> spawnPos = new ArrayList<>(); // 运行时点位数据
    protected int spawnPointIndex = 0;
    protected final Set<Integer> teleportedPlayerId = new HashSet<>();
    protected final Set<Integer> telepotedTeamId = new HashSet<>();
    protected final double queuedHeight = 1145.14; // findGround失败的时候临时反复传送到这个高度，直到区块能成功加载或达到最大时长

    public TeleportSpawner(SpawnShapeType shapeType, Vec3 center, Vec3 dimension, int zoneId,
                           CommonDetailType detailType,
                           TeleportDetailEntry detailEntry) {
        super(shapeType, center, dimension, zoneId, detailType, detailEntry);

        this.teamTogether = this.detailEntry.teamTogether;
        this.findGround = this.detailEntry.findGround;
        this.randomRange = this.detailEntry.randomRange;
        this.hangTime = this.detailEntry.hangTime;

        this.fixedPos.addAll(this.detailEntry.fixedPos);
        this.fixedSimulationCount = this.detailEntry.fixedSimulationCount;
        this.playerFactorContribution = this.detailEntry.playerFactorContribution;
        this.useGoldenSpiral = this.detailEntry.useGoldenSpiral;
        this.allowOnBorder = this.detailEntry.allowOnBorder;
        this.globalShrinkRatio = this.detailEntry.globalShrinkRatio;
        this.needShuffle = this.detailEntry.needShuffle;
    }

    /**
     * 预先计算所有生成的位置
     */
    @Override
    public void init(Supplier<Float> random, int spawnPointsTotal) {
        super.init(random, spawnPointsTotal);
        BattleRoyale.LOGGER.debug("TeleportSpawner::init spawnPointsTotal: {}", spawnPointsTotal);
        this.prepared = false;

        boolean success = false;
        switch (detailType) {
            case FIXED -> { // 如无固定点位则不传送
                success = TeleportSpawnerCalculator.calculateFixedPos(this, random, spawnPointsTotal);
            }
            case RANDOM -> {
                success = TeleportSpawnerCalculator.calculateRandomPos(this, random, spawnPointsTotal);
            }
            case DISTRIBUTED -> {
                success = TeleportSpawnerCalculator.calculatedDistributedPos(this, random, spawnPointsTotal, (int) (fixedSimulationCount + spawnPointsTotal * playerFactorContribution));
            }
            default -> {
                ServerLevel serverLevel = GameManager.get().getServerLevel();
                if (serverLevel != null) {
                    ChatUtils.sendMessageToAllPlayers(serverLevel, "TeleportSpawner config error: unsupported detailType");
                }
                BattleRoyale.LOGGER.warn("Unsupported CommonDetailType in TeleportSpawner");
            }
        }
        if (!success) {
            clear();
            return;
        }

        BattleRoyale.LOGGER.debug("TeleportSpawner::init complete, spawnPos.size() = {}", spawnPos.size());
        if (spawnPos.size() < spawnPointsTotal) {
            BattleRoyale.LOGGER.warn("Unexpected spawnPos.size() ({}) not match spawnPointsTotal ({})", spawnPos.size(), spawnPointsTotal);
        }
        this.prepared = true;
    }

    @Override
    public String getSpawnerTypeString() {
        return SpawnTypeTag.SPAWN_TYPE_TELEPORT;
    }

    @Override
    public void addSpawnDetailProperty() {
        Map<String, String> stringWriter = new HashMap<>();
        stringWriter.put(SpawnDetailTag.TYPE_NAME, detailType.getName());
        GameStatsManager.recordSpawnString(SPAWNER_KEY_TAG, stringWriter);

        Map<String, Boolean> boolWriter = new HashMap<>();
        boolWriter.put(SpawnDetailTag.GROUND_TEAM_TOGETHER, teamTogether);
        boolWriter.put(SpawnDetailTag.GROUND_FIND_GROUND, findGround);
        GameStatsManager.recordSpawnBool(SPAWNER_KEY_TAG, boolWriter);

        Map<String, Double> doubleWriter = new HashMap<>();
        doubleWriter.put(SpawnDetailTag.GROUND_RANDOM_RANGE, randomRange);
        GameStatsManager.recordSpawnDouble(SPAWNER_KEY_TAG, doubleWriter);
    }

    /**
     * 没有异步加载区块，不会阻塞主线程
     */
    @Override
    public void tick(int gameTime, List<GameTeam> gameTeams) {
        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (gameTime > this.hangTime) {
            this.finished = true;
            BattleRoyale.LOGGER.warn("GroundSpawner reached maximum spawn attempt time, force finished");
            return;
        }
        if (serverLevel == null) {
            return;
        }

        // 由于所有点位在init()预计算，因此全部可视作 Fixed/提前确定 类型，全都需要应用偏移
        Vec3 globalOffest = GameManager.get().getGlobalCenterOffset();
        IGameZone gameZone = ZoneManager.get().getGameZone(preZoneCenterId);
        if (gameZone != null && gameZone.isDetermined()) {
            globalOffest = gameZone.getStartCenterPos();
        }

        boolean allTeleported = true;
        // 按队伍传送，方便队伍统一传送
        for (GameTeam gameTeam : gameTeams) {
            if (telepotedTeamId.contains(gameTeam.getGameTeamId())) { // 队伍统一传送，并且传送过
                continue;
            }
            if (gameTeam.isTeamEliminated()) { // 队伍没人存活就跳过，不用标记玩家id
                telepotedTeamId.add(gameTeam.getGameTeamId()); // 标记一下，下次快速continue
                continue;
            }

            boolean teamAllTeleported = true;

            Vec3 targetSpawnPos = findSpawnPos(spawnPointIndex, serverLevel, globalOffest);
            if (targetSpawnPos == null) {
                BattleRoyale.LOGGER.warn("TeleportSpawner can't find a targetSpawnPos, this is unexpected");
                ChatUtils.sendMessageToAllPlayers(serverLevel, "Unexpected error in TeleportSpawner");
                this.finished = true;
                return;
            } else if (targetSpawnPos.y == queuedHeight) {
                teamAllTeleported = false;
                allTeleported = false;
            }

            // 依次传送队伍内未被淘汰玩家
            List<GamePlayer> standingPlayers = gameTeam.getStandingPlayers();
            boolean indexAdded = false;
            for (int i = 0; i < standingPlayers.size(); i++) {
                // 找新点位
                if (!teamTogether && i > 0) {
                    targetSpawnPos = findSpawnPos(spawnPointIndex, serverLevel, globalOffest);
                    if (targetSpawnPos == null) {
                        BattleRoyale.LOGGER.warn("TeleportSpawner can't find a targetSpawnPos, this is unexpected");
                        ChatUtils.sendMessageToAllPlayers(serverLevel, "Unexpected error in TeleportSpawner");
                        this.finished = true;
                        return;
                    } else if (targetSpawnPos.y == queuedHeight) {
                        teamAllTeleported = false;
                        allTeleported = false;
                    }
                }

                // 传送玩家
                GamePlayer gamePlayer = standingPlayers.get(i);
                ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                if (player != null) {
                    if ((!indexAdded || !teamTogether) && targetSpawnPos.y != queuedHeight) { // (没添加过计数就添加一次，不是队伍统一传送就添加一次) && 成功找到地面
                        spawnPointIndex++;
                        indexAdded = true;
                    }
                    GameUtilsFunction.safeTeleport(player, serverLevel, targetSpawnPos, 0, 0);
                    addSpawnStats(gamePlayer, targetSpawnPos);
                    gamePlayer.setLastPos(targetSpawnPos); // 立即更新，防止下一tick找不到又躲了逻辑位置
                    teleportedPlayerId.add(gamePlayer.getGameSingleId());
                    if (targetSpawnPos.y == queuedHeight) {
                        BattleRoyale.LOGGER.debug("GroundSpawner: Telepoted gamePlayer {} to team spawn position {}", gamePlayer.getGameSingleId(), targetSpawnPos);
                    } else {
                        BattleRoyale.LOGGER.info("GroundSpawner: Telepoted gamePlayer {} to team spawn position {}", gamePlayer.getGameSingleId(), targetSpawnPos);
                    }
                } else {
                    teamAllTeleported = false; // 离线玩家也保留其尝试次数，超过最大限制后即使登录也不重新传送
                    allTeleported = false;
                    BattleRoyale.LOGGER.warn("GroundSpawner: Could not find ServerPlayer {} (UUID: {}), playerId: {}, teamId: {}", gamePlayer.getPlayerName(), gamePlayer.getPlayerUUID(), gamePlayer.getGameSingleId(), gamePlayer.getGameTeamId());
                }
            }
            if (teamAllTeleported) {
                telepotedTeamId.add(gameTeam.getGameTeamId());
            }
        }

        // 如果碰到缺失点位的情况（理论上点位充足，可能是碰到区块未加载），下一tick继续处理
        this.finished = allTeleported;
    }

    /**
     * "id" : {
     *     "player/bot": "playerName",
     *     "spawnPos": "x,y,z"
     * }
     */
    private void addSpawnStats(GamePlayer gamePlayer, Vec3 teleportPos) {
        Map<String, String> stringWriter = new HashMap<>();
        stringWriter.put(gamePlayer.isBot() ? "bot" : "player", gamePlayer.getPlayerName());
        stringWriter.put("spawnPos", StringUtils.vectorToString(teleportPos));
        GameStatsManager.recordSpawnString(Integer.toString(gamePlayer.getGameSingleId()), stringWriter);
    }

    @Nullable
    public Vec3 findSpawnPos(int index, ServerLevel serverLevel, Vec3 globalOffset) {
        if (index >= spawnPos.size()) {
            BattleRoyale.LOGGER.warn("GroundSpawner: Not enough spawn point for all players");
            return null;
        }

        Vec3 basePos = spawnPos.get(index).add(globalOffset);
        if (!findGround) {
            return basePos;
        }

        BlockPos lookupPos = new BlockPos((int) basePos.x(), 320, (int) basePos.z()); // 最大建筑高度320
        int groundY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, lookupPos.getX(), lookupPos.getZ());
        double targetY = groundY + 1.0;
        // 在主世界加载失败时 targetY 返回 -63（最小建筑高度 -64），加2保证在范围内
        if (targetY < serverLevel.getMinBuildHeight() + 2) {
            BattleRoyale.LOGGER.debug("GroundSpawner attempt to use invalid targetY {}, adjusting to default height ({}) for queued spawn", targetY, queuedHeight);
            return new Vec3(basePos.x, queuedHeight, basePos.z);
        }
        return new Vec3(basePos.x, targetY, basePos.z);
    }

    @Override
    public void clearAfterGame() {
        spawnPos.clear(); // 运行时点位数据
        spawnPointIndex = 0;
        teleportedPlayerId.clear();
        telepotedTeamId.clear();
    }
}
