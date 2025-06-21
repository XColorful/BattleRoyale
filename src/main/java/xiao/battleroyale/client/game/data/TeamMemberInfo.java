package xiao.battleroyale.client.game.data;

public class TeamMemberInfo {

    public final int playerId;
    public final String name;
    public final float health;
    public int boost;

    public TeamMemberInfo(int playerId, String name, float health, int boost) {
        this.playerId = playerId;
        this.name = name;
        this.health = health;
        this.boost = boost;
    }
}