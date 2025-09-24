package xiao.battleroyale.developer.debug;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.effect.boost.BoostData;
import xiao.battleroyale.common.effect.boost.BoostManager;
import xiao.battleroyale.common.effect.firework.FireworkManager;
import xiao.battleroyale.common.effect.firework.FixedFireworkTask;
import xiao.battleroyale.common.effect.firework.PlayerTrackingFireworkTask;
import xiao.battleroyale.common.effect.muteki.EntityMutekiTask;
import xiao.battleroyale.common.effect.muteki.MutekiManager;
import xiao.battleroyale.common.effect.particle.*;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameTeamManager;
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
        List<FixedParticleChannel> allFixedChannels = new ArrayList<>(ParticleManager.get().getFixedParticles().values());
        allFixedChannels.sort(Comparator.comparing(channel -> channel.channelKey));
        List<EntityParticleTask> allEntityTasks = new ArrayList<>(ParticleManager.get().getEntityParticles().values());
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
        FixedParticleChannel fixedChannel = ParticleManager.get().getFixedParticles().get(channel);
        List<FixedParticleData> fullFixedParticles = fixedChannel != null ? new ArrayList<>(fixedChannel.particles) : new ArrayList<>();

        fullFixedParticles.sort(Comparator.comparingLong(data -> data.worldTime));
        List<FixedParticleData> fixedParticles = getSubListSafely(fullFixedParticles, min, max);

        DebugManager.sendDebugMessage(source, GET_PARTICLE, EffectText.buildFixedParticle(fixedParticles));
    }
    public void getParticle(CommandSourceStack source, Entity entity, String channel, int min, int max) {
        EntityParticleTask entityTask = entity != null ? ParticleManager.get().getEntityParticles().get(entity.getUUID()) : null;
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
        List<FixedFireworkTask> fixedTasks = getSubListSafely(FireworkManager.get().getFixedTasks(), min, max);
        List<PlayerTrackingFireworkTask> playerTasks = getSubListSafely(FireworkManager.get().getPlayerTrackingTasks(), min, max);

        DebugManager.sendDebugMessage(source, GET_FIREWORKS, EffectText.buildFireworkTasks(source.getLevel(), fixedTasks, playerTasks));
    }

    /**
     * [调试]getFirework
     */
    public static final String GET_FIREWORK = "getFirework";
    public void getFirework(CommandSourceStack source, int singleId) {
        GamePlayer gamePlayer = GameTeamManager.getGamePlayerBySingleId(singleId);
        List<PlayerTrackingFireworkTask> playerTasks = new ArrayList<>();
        if (gamePlayer != null) {
            UUID targetUUID = gamePlayer.getPlayerUUID();
            for (PlayerTrackingFireworkTask task : FireworkManager.get().getPlayerTrackingTasks()) {
                if (task.getPlayerUUID().equals(targetUUID)) {
                    playerTasks.add(task);
                }
            }
        }

        DebugManager.sendDebugMessage(source, GET_FIREWORK, EffectText.buildPlayerFireworkTasks(source.getLevel(), playerTasks));
    }
    public void getFirework(CommandSourceStack source, Entity entity) {
        List<PlayerTrackingFireworkTask> playerTasks = new ArrayList<>();
        UUID targetUUID = entity.getUUID();
        for (PlayerTrackingFireworkTask task : FireworkManager.get().getPlayerTrackingTasks()) {
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
        List<EntityMutekiTask> fullMutekiTasks = new ArrayList<>(MutekiManager.get().getMutekiTasks().values());
        fullMutekiTasks.sort(Comparator.comparingLong(EntityMutekiTask::getWorldTime));
        List<EntityMutekiTask> mutekiTasks = getSubListSafely(fullMutekiTasks, min, max);

        DebugManager.sendDebugMessage(source, GET_MUTEKIS, EffectText.buildMutekiTasks(source.getLevel(), mutekiTasks));
    }

    /**
     * [调试]getMuteki
     */
    public static final String GET_MUTEKI = "getMuteki";
    public void getMuteki(CommandSourceStack source, int singleId) {
        GamePlayer gamePlayer = GameTeamManager.getGamePlayerBySingleId(singleId);
        EntityMutekiTask mutekiTask = gamePlayer != null ? MutekiManager.get().getMutekiTasks().get(gamePlayer.getPlayerUUID()) : null;

        DebugManager.sendDebugMessage(source, GET_MUTEKI, EffectText.buildMutekiTask(source.getLevel(), mutekiTask));
    }
    public void getMuteki(CommandSourceStack source, Entity entity) {
        EntityMutekiTask mutekiTask = MutekiManager.get().getMutekiTasks().get(entity.getUUID());

        DebugManager.sendDebugMessage(source, GET_MUTEKI, EffectText.buildMutekiTask(source.getLevel(), mutekiTask));
    }

    /**
     * [调试]getBoosts
     */
    public static final String GET_BOOSTS = "getBoosts";
    public void getBoosts(CommandSourceStack source, int min, int max) {
        List<BoostData> fullBoostData = new ArrayList<>(BoostManager.get().getBoostData().values());
        fullBoostData.sort(Comparator.comparingLong(data -> data.worldTime));
        List<BoostData> boostData = getSubListSafely(fullBoostData, min, max);

        DebugManager.sendDebugMessage(source, GET_BOOSTS, EffectText.buildBoostData(source.getLevel(), boostData));
    }

    /**
     * [调试]getBoost
     */
    public static final String GET_BOOST = "getBoost";
    public void getBoost(CommandSourceStack source, int singleId) {
        GamePlayer gamePlayer = GameTeamManager.getGamePlayerBySingleId(singleId);
        BoostData data = gamePlayer != null ? BoostManager.get().getBoostData(gamePlayer.getPlayerUUID()) : null;

        DebugManager.sendDebugMessage(source, GET_BOOST, EffectText.buildBoost(source.getLevel(), data));
    }
    public void getBoost(CommandSourceStack source, Entity entity) {
        BoostData data = BoostManager.get().getBoostData(entity.getUUID());

        DebugManager.sendDebugMessage(source, GET_BOOST, EffectText.buildBoost(source.getLevel(), data));
    }
}
