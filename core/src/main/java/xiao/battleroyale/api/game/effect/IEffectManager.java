package xiao.battleroyale.api.game.effect;

public interface IEffectManager {

    void clear();

    void forceEnd();

    boolean shouldEnd();
}
