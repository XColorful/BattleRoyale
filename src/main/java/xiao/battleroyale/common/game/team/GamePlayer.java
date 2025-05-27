package xiao.battleroyale.common.game.team;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GamePlayer {

    private final UUID playerId; // 玩家自身唯一UUID
    private final String playerName; // 玩家自身名称
    private int kills; // 击杀数
    private int downs; // 被击倒数
    private float damageDealt; // 造成伤害数
    private float damageTaken; // 被造成伤害数
    private boolean isAlive = true; // 是否存活，判断倒地
    private boolean isEliminated = false; // 是否被淘汰，即彻底退出游戏循环
    private final int gameSingleId; // 例如100人的对局则是1到100
    private final String gameTeamColor; // 队伍颜色
    private GameTeam team; // 所属队伍
    private boolean isLeader = false; // 是否是队伍队长

    private boolean isActiveEntity = false; // 是否已加载 (即玩家是否在线并加载在世界中)
    private Vec3 lastPos; // 最后出现的位置
    private int zoneDamageTaken; // 已承受的毒圈伤害，优先用此来处理区块外的人机是否应该算减员，如果在圈外然后又重新加载，再造成伤害的时候通常就能直接判断清除
    private int invalidTime; // 额外检查，防止重新加载区块的时候圈已经没了（模组支持自定义圈），超过invalidTime则清除，同时应用于玩家离线重连
    private final boolean bot; // 是否是机器人

    /**
     * 构造函数。
     * @param playerId 玩家的 UUID。
     * @param playerName 玩家的名称。
     * @param gameSingleId 游戏内单个玩家的唯一 ID。
     * @param gameTeamColor 队伍颜色。
     * @param isBot 是否是机器人。
     * @param team 玩家所属的队伍。
     */
    public GamePlayer(@NotNull UUID playerId, @NotNull String playerName, int gameSingleId, String gameTeamColor, boolean isBot, GameTeam team) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.gameSingleId = gameSingleId;
        this.gameTeamColor = gameTeamColor;
        this.bot = isBot;
        this.team = team;
        this.zoneDamageTaken = 0;
        this.invalidTime = 0;
    }

    // Getters
    public UUID getPlayerUUID() { return playerId; }
    public String getPlayerName() { return playerName; }
    public int getKills() { return kills; }
    public int getDowns() { return downs; }
    public float getDamageDealt() { return damageDealt; }
    public float getDamageTaken() { return damageTaken; }
    public boolean isAlive() { return isAlive; }
    public boolean isEliminated() { return isEliminated; }
    public int getGameSingleId() { return gameSingleId; }
    public int getGameTeamId() { return team != null ? team.getGameTeamId() : -1; }
    public String getGameTeamColor() { return gameTeamColor; }
    public boolean isActiveEntity() { return isActiveEntity; }
    public Vec3 getLastPos() { return lastPos; }
    public int getZoneDamageTaken() { return zoneDamageTaken; }
    public int getInvalidTime() { return invalidTime; }
    public boolean isBot() { return bot; }
    public GameTeam getTeam() { return team; }
    public boolean isLeader() { return isLeader; }

    // Setters (只针对可变字段)
    public void setKills(int kills) { this.kills = kills; }
    public void addKill() { this.kills++; }
    public void setDowns(int downs) { this.downs = downs; }
    public void addDown() { this.downs++; }
    public void setDamageDealt(float damageDealt) { this.damageDealt = damageDealt; }
    public void addDamageDealt(float amount) { this.damageDealt += amount; }
    public void setDamageTaken(float damageTaken) { this.damageTaken = damageTaken; }
    public void addDamageTaken(float amount) { this.damageTaken += amount; }

    /**
     * 设置玩家是否存活（倒地/站立）。
     * 当玩家被淘汰时，此值会自动变为 false。
     * @param alive 是否存活。
     */
    public void setAlive(boolean alive) { this.isAlive = alive; }

    /**
     * 设置玩家是否已被淘汰。
     * 当玩家被淘汰时，其 alive 状态也会强制变为 false。
     * @param eliminated 是否被淘汰。
     */
    public void setEliminated(boolean eliminated) {
        this.isEliminated = eliminated;
        if (eliminated) {
            this.isAlive = false; // 如果被淘汰，则肯定不活跃
        }
    }

    /**
     * 设置玩家是否为活跃实体（在线并加载）。
     * 玩家断线重连时，此状态会改变，但不影响淘汰状态。
     * @param activeEntity 是否是活跃实体。
     */
    public void setActiveEntity(boolean activeEntity) { this.isActiveEntity = activeEntity; }
    public void setLastPos(Vec3 lastPos) { this.lastPos = lastPos; }
    public void setZoneDamageTaken(int zoneDamageTaken) { this.zoneDamageTaken = zoneDamageTaken; }
    public void addZoneDamageTaken(int amount) { this.zoneDamageTaken += amount; }
    public void setInvalidTime(int invalidTime) { this.invalidTime = invalidTime; }
    public void addInvalidTime() {this.invalidTime++; }
    public void setLeader(boolean leader) { this.isLeader = leader; }

    /**
     * 设置玩家所属的队伍。
     * @param team 新的队伍对象。
     */
    public void setTeam(GameTeam team) { this.team = team; } // 转移队伍
}