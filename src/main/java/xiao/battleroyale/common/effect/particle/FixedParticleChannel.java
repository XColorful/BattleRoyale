package xiao.battleroyale.common.effect.particle;

public class FixedParticleChannel extends AbstractParticleChannel<FixedParticleData> {

    public static final String COMMAND_CHANNEL = "command"; // 非玩家输入指令使用的通道
    public static final String GAME_CHANNEL = "game"; // GameManager使用的通道
}
