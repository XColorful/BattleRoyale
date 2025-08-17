package xiao.battleroyale.client.game.data;

import java.util.UUID;

public class TeamMemberInfo {

    public final int playerId;
    public final String name;
    public final float health;
    public int boost;
    public UUID uuid;
    public boolean alive;

    public TeamMemberInfo(int playerId, String name, float health, int boost, UUID uuid, boolean alive) {
        this.playerId = playerId;
        this.name = name;
        this.health = health;
        this.boost = boost;
        this.uuid = uuid;
        this.alive = alive;
    }
}