package xiao.battleroyale.common.game.stats;

public class AbstractStats {

    protected int knocks; // 击倒数（直接淘汰不增加击倒数）
    protected int kills; // 击杀数
    protected int downs; // 自身倒地次数
    protected float damageDealt;
    protected float playerDamageTaken; // 被玩家造成的伤害
    protected float otherDamageTaken; // 被非玩家伤害来源造成的伤害

    public AbstractStats() {
        ;
    }

    public int getKnocks() { return knocks; }
    public int getKills() { return kills; }
    public int getDowns() { return downs; }
    public float getDamageDealt() { return damageDealt; }
    public float getPlayerDamageTaken() { return playerDamageTaken; }
    public float getOtherDamageTaken() { return otherDamageTaken; }
}
