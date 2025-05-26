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
    private boolean isEliminated = false;
    private final int gameSingleId; // 例如100人的对局则是1到100
    private final String gameTeamColor; // 队伍颜色
    private GameTeam team;

    private boolean isActiveEntity; // 是否已加载
    private Vec3 lastPos; // 最后出现的位置
    private int zoneDamageTaken; // 已承受的毒圈伤害，优先用此来处理区块外的人机是否应该算减员，如果在圈外然后又重新加载，再造成伤害的时候通常就能直接判断清除
    private int invalidTime; // 额外检查，防止重新加载区块的时候圈已经没了（模组支持自定义圈），超过invalidTime则清除，同时应用于玩家离线重连
    private final boolean bot;

    public GamePlayer(@NotNull UUID playerId, @NotNull String playerName, int gameSingleId, String gameTeamColor, boolean isBot, GameTeam team) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.gameSingleId = gameSingleId;
        this.gameTeamColor = gameTeamColor;
        this.bot = isBot;
        this.team = team;
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
    public int getGameTeamId() { return team.getGameTeamId(); }
    public String getGameTeamColor() { return gameTeamColor; }
    public boolean isActiveEntity() { return isActiveEntity; }
    public Vec3 getLastPos() { return lastPos; }
    public int getZoneDamageTaken() { return zoneDamageTaken; }
    public int getInvalidTime() { return invalidTime; }
    public boolean isBot() { return bot; }
    public GameTeam getTeam() { return team; }

    // Setters (only for mutable fields)
    public void setKills(int kills) { this.kills = kills; }
    public void addKill() { this.kills++; } // 方便击杀数增加
    public void setDowns(int downs) { this.downs = downs; }
    public void addDown() { this.downs++; } // 方便被击倒数增加
    protected void setDamageDealt(float damageDealt) { this.damageDealt = damageDealt; }
    public void addDamageDealt(float amount) { this.damageDealt += amount; }
    protected void setDamageTaken(float damageTaken) { this.damageTaken = damageTaken; }
    public void addDamageTaken(float amount) { this.damageTaken += amount; }
    public void setAlive(boolean alive) { this.isAlive = alive; }
    public void setEliminated(boolean eliminated) { this.isEliminated = eliminated; this.isAlive = false; }
    public void setActiveEntity(boolean activeEntity) { this.isActiveEntity = activeEntity; } // 玩家断线重连不重新计数，防止变相抗毒
    public void setLastPos(Vec3 lastPos) { this.lastPos = lastPos; }
    protected void setZoneDamageTaken(int zoneDamageTaken) { this.zoneDamageTaken = zoneDamageTaken; }
    public void addZoneDamageTaken(int amount) { this.zoneDamageTaken += amount; }
    protected void setInvalidTime(int invalidTime) { this.invalidTime = invalidTime; }
    public void addInvalidTime() {this.invalidTime++; }
}
