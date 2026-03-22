package xiao.battleroyale.developer.debug;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.effect.type.IBoostManager;
import xiao.battleroyale.api.effect.type.IFireworkManager;
import xiao.battleroyale.api.effect.type.IMutekiManager;
import xiao.battleroyale.api.effect.type.IParticleManager;
import xiao.battleroyale.common.effect.boost.BoostData;
import xiao.battleroyale.common.effect.firework.FixedFireworkTask;
import xiao.battleroyale.common.effect.firework.PlayerTrackingFireworkTask;
import xiao.battleroyale.common.effect.muteki.EntityMutekiTask;
import xiao.battleroyale.common.effect.particle.*;
import xiao.battleroyale.common.game._GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.developer.debug.text.EffectText;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static xiao.battleroyale.util.ListUtils.getSubListSafely;

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
        IParticleManager particleManager = BattleRoyale.getEffectManager().getParticleManager();
        List<FixedParticleChannel> allFixedChannels = new ArrayList<>(particleManager.getFixedParticles().values());
        allFixedChannels.sort(Comparator.comparing(channel -> channel.channelKey));
        List<EntityParticleTask> allEntityTasks = new ArrayList<>(particleManager.getEntityParticles().values());
        allEntityTasks.sort(Comparator.comparing(task -> task.entityUUID));

        List<FixedParticleChannel> fixedChannelSubList = getSubListSafely(allFixedChannels, min, max);
        List<EntityParticleTask> entityTaskSubList = getSubListSafely(allEntityTasks, min, max);

        DebugManager.sendDebugMessage(source, GET_PARTICLES, EffectText.buildParticles(source.getLevel(), fixedChannelSubList, entityTaskSubList));
    }

    /**
     * [调试]getParticle
     */
    public static final String GET_PARTICLE = "getParticle";
    public void getParticle(CommandSourceStack source, String channel, int min, int max) {
        IParticleManager particleManager = BattleRoyale.getEffectManager().getParticleManager();
        FixedParticleChannel fixedChannel = particleManager.getFixedParticles().get(channel);
        List<FixedParticleData> fullFixedParticles = fixedChannel != null ? new ArrayList<>(fixedChannel.particles) : new ArrayList<>();

        fullFixedParticles.sort(Comparator.comparingLong(data -> data.worldTime));
        List<FixedParticleData> fixedParticles = getSubListSafely(fullFixedParticles, min, max);

        DebugManager.sendDebugMessage(source, GET_PARTICLE, EffectText.buildFixedParticle(fixedParticles));
    }
    public void getParticle(CommandSourceStack source, Entity entity, String channel, int min, int max) {
        IParticleManager particleManager = BattleRoyale.getEffectManager().getParticleManager();
        EntityParticleTask entityTask = entity != null ? particleManager.getEntityParticles().get(entity.getUUID()) : null;
        EntityParticleChannel entityChannel = entityTask != null ? entityTask.channels.get(channel) : null;
        List<ParticleData> fullParticles = entityChannel != null ? new ArrayList<>(entityChannel.particles) : new ArrayList<>();
        fullParticles.sort(Comparator.comparingLong(data -> data.worldTime));
        List<ParticleData> particles = getSubListSafely(fullParticles, min, max);

        DebugManager.sendDebugMessage(source, GET_PARTICLE, EffectText.buildEntityParticle(source.getLevel(), particles, entity));
    }

    /**
     * [调试]getFireworks
     */
    public static final String GET_FIREWORKS = "getFireworks";
    public void getFireworks(CommandSourceStack source, int min, int max) {
        IFireworkManager fireworkManager = BattleRoyale.getEffectManager().getFireworkManager();
        List<FixedFireworkTask> fixedTasks = getSubListSafely(fireworkManager.getFixedTasks(), min, max);
        List<PlayerTrackingFireworkTask> playerTasks = getSubListSafely(fireworkManager.getPlayerTrackingTasks(), min, max);

        DebugManager.sendDebugMessage(source, GET_FIREWORKS, EffectText.buildFireworkTasks(source.getLevel(), fixedTasks, playerTasks));
    }

    /**
     * [调试]getFirework
     */
    public static final String GET_FIREWORK = "getFirework";
    public void getFirework(CommandSourceStack source, int singleId) {
        IFireworkManager fireworkManager = BattleRoyale.getEffectManager().getFireworkManager();
        GamePlayer gamePlayer = _GameTeamManager.getGamePlayerBySingleId(singleId);
        List<PlayerTrackingFireworkTask> playerTasks = new ArrayList<>();
        if (gamePlayer != null) {
            UUID targetUUID = gamePlayer.getPlayerUUID();
            for (PlayerTrackingFireworkTask task : fireworkManager.getPlayerTrackingTasks()) {
                if (task.getPlayerUUID().equals(targetUUID)) {
                    playerTasks.add(task);
                }
            }
        }

        DebugManager.sendDebugMessage(source, GET_FIREWORK, EffectText.buildPlayerFireworkTasks(source.getLevel(), playerTasks));
    }
    public void getFirework(CommandSourceStack source, Entity entity) {
        IFireworkManager fireworkManager = BattleRoyale.getEffectManager().getFireworkManager();
        List<PlayerTrackingFireworkTask> playerTasks = new ArrayList<>();
        UUID targetUUID = entity.getUUID();
        for (PlayerTrackingFireworkTask task : fireworkManager.getPlayerTrackingTasks()) {
            if (task.getPlayerUUID().equals(targetUUID)) {
                playerTasks.add(task);
            }
        }

        DebugManager.sendDebugMessage(source, GET_FIREWORK, EffectText.buildPlayerFireworkTasks(source.getLevel(), playerTasks));
    }

    /**
     * [调试]getMutekis
     */
    public static final String GET_MUTEKIS = "getMutekis";
    public void getMutekis(CommandSourceStack source, int min, int max) {
        IMutekiManager mutekiManager = BattleRoyale.getEffectManager().getMutekiManager();
        List<EntityMutekiTask> fullMutekiTasks = new ArrayList<>(mutekiManager.getMutekiTasks().values());
        fullMutekiTasks.sort(Comparator.comparingLong(EntityMutekiTask::getWorldTime));
        List<EntityMutekiTask> mutekiTasks = getSubListSafely(fullMutekiTasks, min, max);

        DebugManager.sendDebugMessage(source, GET_MUTEKIS, EffectText.buildMutekiTasks(source.getLevel(), mutekiTasks));
    }

    /**
     * [调试]getMuteki
     */
    public static final String GET_MUTEKI = "getMuteki";
    public void getMuteki(CommandSourceStack source, int singleId) {
        IMutekiManager mutekiManager = BattleRoyale.getEffectManager().getMutekiManager();
        GamePlayer gamePlayer = _GameTeamManager.getGamePlayerBySingleId(singleId);
        EntityMutekiTask mutekiTask = gamePlayer != null ? mutekiManager.getMutekiTasks().get(gamePlayer.getPlayerUUID()) : null;

        DebugManager.sendDebugMessage(source, GET_MUTEKI, EffectText.buildMutekiTask(source.getLevel(), mutekiTask));
    }
    public void getMuteki(CommandSourceStack source, Entity entity) {
        IMutekiManager mutekiManager = BattleRoyale.getEffectManager().getMutekiManager();
        EntityMutekiTask mutekiTask = mutekiManager.getMutekiTasks().get(entity.getUUID());

        DebugManager.sendDebugMessage(source, GET_MUTEKI, EffectText.buildMutekiTask(source.getLevel(), mutekiTask));
    }

    /**
     * [调试]getBoosts
     */
    public static final String GET_BOOSTS = "getBoosts";
    public void getBoosts(CommandSourceStack source, int min, int max) {
        IBoostManager boostManager = BattleRoyale.getEffectManager().getBoostManager();
        List<BoostData> fullBoostData = new ArrayList<>(boostManager.getBoostData().values());
        fullBoostData.sort(Comparator.comparingLong(data -> data.worldTime));
        List<BoostData> boostData = getSubListSafely(fullBoostData, min, max);

        DebugManager.sendDebugMessage(source, GET_BOOSTS, EffectText.buildBoostData(source.getLevel(), boostData));
    }

    /**
     * [调试]getBoost
     */
    public static final String GET_BOOST = "getBoost";
    public void getBoost(CommandSourceStack source, int singleId) {
        IBoostManager boostManager = BattleRoyale.getEffectManager().getBoostManager();
        GamePlayer gamePlayer = _GameTeamManager.getGamePlayerBySingleId(singleId);
        BoostData data = gamePlayer != null ? boostManager.getBoostData(gamePlayer.getPlayerUUID()) : null;

        DebugManager.sendDebugMessage(source, GET_BOOST, EffectText.buildBoost(source.getLevel(), data));
    }
    public void getBoost(CommandSourceStack source, Entity entity) {
        IBoostManager boostManager = BattleRoyale.getEffectManager().getBoostManager();
        BoostData data = boostManager.getBoostData(entity.getUUID());

        DebugManager.sendDebugMessage(source, GET_BOOST, EffectText.buildBoost(source.getLevel(), data));
    }
}
