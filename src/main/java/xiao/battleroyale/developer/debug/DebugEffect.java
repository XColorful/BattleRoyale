package xiao.battleroyale.developer.debug;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;

public class DebugEffect {

    private static class DebugEffectHolder {
        private static final DebugEffect INSTANCE = new DebugEffect();
    }

    public static DebugEffect get() {
        return DebugEffectHolder.INSTANCE;
    }

    private DebugEffect() {
        ;
    }

    /**
     * [调试]getParticles
     */
    public static final String GET_PARTICLES = "getParticles";
    public void getParticles(CommandSourceStack source, int min, int max) {
        ;
    }

    /**
     * [调试]getParticle
     */
    public static final String GET_PARTICLE = "getParticle";
    public void getParticle(CommandSourceStack source, String channel, int min, int max) {
        ;
    }
    public void getParticle(CommandSourceStack source, Entity entity, String channel, int min, int max) {
        ;
    }

    /**
     * [调试]getFireworks
     */
    public static final String GET_FIREWORKS = "getFireworks";
    public void getFireworks(CommandSourceStack source, int min, int max) {
        ;
    }

    /**
     * [调试]getFirework
     */
    public static final String GET_FIREWORK = "getFirework";
    public void getFirework(CommandSourceStack source, int singleId) {
        ;
    }
    public void getFirework(CommandSourceStack source, Entity entity) {
        ;
    }

    /**
     * [调试]getMutekis
     */
    public static final String GET_MUTEKIS = "getMutekis";
    public void getMutekis(CommandSourceStack source, int min, int max) {
        ;
    }

    /**
     * [调试]getMuteki
     */
    public static final String GET_MUTEKI = "getMuteki";
    public void getMuteki(CommandSourceStack source, int singleId) {
        ;
    }
    public void getMuteki(CommandSourceStack source, Entity entity) {
        ;
    }

    /**
     * [调试]getBoosts
     */
    public static final String GET_BOOSTS = "getBoosts";
    public void getBoosts(CommandSourceStack source, int min, int max) {
        ;
    }

    /**
     * [调试]getBoost
     */
    public static final String GET_BOOST = "getBoost";
    public void getBoost(CommandSourceStack source, int singleId) {
        ;
    }
    public void getBoost(CommandSourceStack source, Entity entity) {
        ;
    }
}
