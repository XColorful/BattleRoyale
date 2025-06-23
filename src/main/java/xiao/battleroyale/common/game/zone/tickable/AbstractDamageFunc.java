package xiao.battleroyale.common.game.zone.tickable;

public abstract class AbstractDamageFunc extends AbstractSimpleFunc {

    public final float damage;

    public AbstractDamageFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset, float damage) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.damage = damage;
    }
}
