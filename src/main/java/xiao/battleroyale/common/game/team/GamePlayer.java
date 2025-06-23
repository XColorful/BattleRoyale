package xiao.battleroyale.common.game.team;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.effect.EffectManager;

import java.util.UUID;

public class GamePlayer {

    private UUID playerUUID; // 玩家自身唯一UUID
    private final String playerName; // 玩家自身名称
    private final int gameSingleId; // 例如100人的对局则是1到100
    private final String gameTeamColor; // 队伍颜色
    private final boolean bot; // 是否是机器人

    private boolean isAlive = true; // 是否存活，判断倒地
    private boolean isEliminated = false; // 是否被淘汰，即彻底退出游戏循环
    private boolean isActiveEntity = true; // 是否已加载 (即玩家是否在线并加载在世界中)，该项在GameManager::initGame之后不持续更新

    private int invalidTime; // 额外检查，防止重新加载区块的时候圈已经没了（模组支持自定义圈），超过invalidTime则清除，同时应用于玩家离线重连

    private GameTeam team; // 所属队伍
    private boolean isLeader = false; // 是否是队伍队长

    private float lastHealth = 0;
    private Vec3 lastPos = Vec3.ZERO; // 最后出现的位置

    public GamePlayer(@NotNull UUID playerUUID, @NotNull String playerName, int gameSingleId, boolean isBot, @NotNull GameTeam team) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.gameSingleId = gameSingleId;
        this.gameTeamColor = team.getGameTeamColor();
        this.bot = isBot;
        this.team = team;
        this.invalidTime = 0;
    }

    public UUID getPlayerUUID() { return playerUUID; }
    public String getPlayerName() { return playerName; }
    public boolean isAlive() { return isAlive; }
    public boolean isEliminated() { return isEliminated; }
    public int getGameSingleId() { return gameSingleId; }
    public int getGameTeamId() { return team != null ? team.getGameTeamId() : -1; }
    public String getGameTeamColor() { return gameTeamColor; }
    public boolean isActiveEntity() { return isActiveEntity; }
    public Vec3 getLastPos() { return lastPos; }
    public float getLastHealth() { return lastHealth; }
    public int getInvalidTime() { return invalidTime; }
    public boolean isBot() { return bot; }
    public GameTeam getTeam() { return team; }
    public boolean isLeader() { return isLeader; }

    public void setAlive(boolean alive) {
        this.isAlive = alive;
        this.isEliminated = true; // TODO 倒地机制完成前默认淘汰

        if (team != null && team.isTeamEliminated()) { // 队伍无人则倒地
            this.isEliminated = true;
        }

        if (this.isEliminated) { // 自动更新
            this.isAlive = false;
        }

        if (!isAlive) {
            EffectManager.get().clearBoost(this.playerUUID);
        }
    }

    public void setEliminated(boolean eliminated) {
        this.isEliminated = eliminated;
        if (eliminated) {
            setAlive(false); // 如果被淘汰，默认也是倒地
        }
    }

    /**
     * 设置玩家是否为活跃实体（在线并加载）。
     * 玩家断线重连时，此状态会改变，但不影响淘汰状态。
     * @param activeEntity 是否是活跃实体。
     */
    public void setActiveEntity(boolean activeEntity) { this.isActiveEntity = activeEntity; }
    public void setLastPos(Vec3 lastPos) { this.lastPos = lastPos; }
    public void setLastHealth(float lastHealth) { this.lastHealth = lastHealth; }
    public void setInvalidTime(int invalidTime) { this.invalidTime = invalidTime; }
    public void addInvalidTime() {this.invalidTime++; }
    public void setLeader(boolean leader) { this.isLeader = leader; }
    public void setTeam(GameTeam team) { this.team = team; } // 转移队伍

    public static final int BOOST_LIMIT = 6000;

    /**
     * 用于切换人机玩家实体
     */
    public void assignNewBotEntity(LivingEntity livingEntity) {
        if (!isBot()) {
            return;
        }
        this.playerUUID = livingEntity.getUUID();
    }
}