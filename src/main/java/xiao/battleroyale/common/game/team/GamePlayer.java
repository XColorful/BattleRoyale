package xiao.battleroyale.common.game.team;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;

import java.util.UUID;

public class GamePlayer {

    private final UUID playerId; // 玩家自身唯一UUID
    private final String playerName; // 玩家自身名称
    private final int gameSingleId; // 例如100人的对局则是1到100
    private final String gameTeamColor; // 队伍颜色
    private final boolean bot; // 是否是机器人

    private boolean isAlive = true; // 是否存活，判断倒地
    private boolean isEliminated = false; // 是否被淘汰，即彻底退出游戏循环
    private boolean isActiveEntity = true; // 是否已加载 (即玩家是否在线并加载在世界中)，该项在GameManager::initGame之后不持续更新

    private int kills; // 击杀数
    private int downs; // 被击倒数
    private float damageDealt; // 造成伤害数
    private float damageTaken; // 被造成伤害数，包含毒圈伤害
    private float zoneDamageTaken; // 记录的毒圈伤害

    private int boost;
    private int healCooldown;
    private int effectCooldown;
    private int invalidTime; // 额外检查，防止重新加载区块的时候圈已经没了（模组支持自定义圈），超过invalidTime则清除，同时应用于玩家离线重连

    private GameTeam team; // 所属队伍
    private boolean isLeader = false; // 是否是队伍队长

    private Vec3 lastPos = Vec3.ZERO; // 最后出现的位置

    public GamePlayer(@NotNull UUID playerId, @NotNull String playerName, int gameSingleId, boolean isBot, @NotNull GameTeam team) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.gameSingleId = gameSingleId;
        this.gameTeamColor = team.getGameTeamColor();
        this.bot = isBot;
        this.team = team;
        this.zoneDamageTaken = 0;
        resetBoost();
        this.invalidTime = 0;
    }

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
    public float getZoneDamageTaken() { return zoneDamageTaken; }
    public int getBoost() { return boost; }
    public int getHealCooldown() { return healCooldown; }
    public int getEffectCooldown() { return effectCooldown; }
    public int getInvalidTime() { return invalidTime; }
    public boolean isBot() { return bot; }
    public GameTeam getTeam() { return team; }
    public boolean isLeader() { return isLeader; }

    public void setKills(int kills) { this.kills = kills; }
    public void addKill() { this.kills++; }
    public void setDowns(int downs) { this.downs = downs; }
    public void addDown() { this.downs++; }
    public void setDamageDealt(float damageDealt) { this.damageDealt = damageDealt; }
    public void addDamageDealt(float amount) { this.damageDealt += amount; }
    public void setDamageTaken(float damageTaken) { this.damageTaken = damageTaken; }
    public void addDamageTaken(float amount) { this.damageTaken += amount; }

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
            this.boost = 0;
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
    public void setZoneDamageTaken(int zoneDamageTaken) { this.zoneDamageTaken = zoneDamageTaken; }
    public void addZoneDamageTaken(float amount) { this.zoneDamageTaken += amount; }
    public void setBoost(int boost) { this.boost = Math.max(Math.min(boost, BOOST_LIMIT), 0); }
    public void addBoost(int amount) { setBoost(this.boost + amount);}
    public void dropBoost() { setBoost(this.boost - 1);}
    public void resetBoost() { setBoost(0); healCooldown = 0; effectCooldown = 0; }
    public void setHealCooldown(int cooldown) { this.healCooldown = cooldown; }
    public void addHealCooldown(int amount) { setHealCooldown(this.healCooldown + amount);}
    public void dropHealCooldown() { setHealCooldown(this.healCooldown - 1); }
    public void setEffectCooldown(int cooldown) { this.effectCooldown = Math.max(cooldown, 0); }
    public void addEffectCooldown(int amount) { setEffectCooldown(this.effectCooldown + amount); }
    public void dropEffectCooldown() { setEffectCooldown(this.effectCooldown - 1); }
    public void setInvalidTime(int invalidTime) { this.invalidTime = invalidTime; }
    public void addInvalidTime() {this.invalidTime++; }
    public void setLeader(boolean leader) { this.isLeader = leader; }
    public void setTeam(GameTeam team) { this.team = team; } // 转移队伍

    public static final int BOOST_LEVEL_4 = 0xAAd46f16;
    public static final int BOOST_LEVEL_3 = 0xAAd7831e;
    public static final int BOOST_LEVEL_2 = 0xAAe1a31c;
    public static final int BOOST_LEVEL_1 = 0xAAe8c625;
    public static final int BOOST_LIMIT = 6000;
    public static int getBoostLevel(int boost) {
        if (boost >= 5400) { // 90%
            return 4;
        } else if (boost >= 3600) { // 60%
            return 3;
        } else if (boost >= 1200) { // 20%
            return 2;
        } else if (boost > 0) { // 0%
            return 1;
        } else {
            return 0;
        }
    }
    public static double getBoostPercentage(int boost) {
        return (double) boost / BOOST_LIMIT;
    }
}