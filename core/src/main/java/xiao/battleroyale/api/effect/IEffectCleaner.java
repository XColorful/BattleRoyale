package xiao.battleroyale.api.effect;

import java.util.UUID;

public interface IEffectCleaner {

    void clearFirework();

    void clearMuteki();
    boolean clearMuteki(UUID uuid);

    void clearBoost();
    void clearBoost(UUID entityUUID);

    // 清除所有粒子
    void clearParticle();
    // 仅游戏区域调用
    void clearParticle(UUID entityUUID);
    void clearParticle(UUID entityUUID, String channelKey);
    // 仅GameManager调用
    void clearGameParticle();
    // 仅非玩家指令调用
    void clearCommandParticle();
}
