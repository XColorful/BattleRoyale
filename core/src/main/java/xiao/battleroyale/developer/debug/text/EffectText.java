package xiao.battleroyale.developer.debug.text;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.command.sub.MutekiCommand;
import xiao.battleroyale.common.effect.boost.BoostData;
import xiao.battleroyale.common.effect.boost.BoostManager;
import xiao.battleroyale.common.effect.firework.AbstractFireworkTask;
import xiao.battleroyale.common.effect.firework.FixedFireworkTask;
import xiao.battleroyale.common.effect.firework.PlayerTrackingFireworkTask;
import xiao.battleroyale.common.effect.muteki.EntityMutekiTask;
import xiao.battleroyale.common.effect.muteki.MutekiManager;
import xiao.battleroyale.common.effect.particle.*;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.effect.particle.ParticleDetailEntry;
import xiao.battleroyale.developer.debug.command.sub.get.GetEffect;
import xiao.battleroyale.developer.debug.command.sub.get.GetGame;
import xiao.battleroyale.util.ColorUtils;

import static xiao.battleroyale.util.CommandUtils.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EffectText {

    public static MutableComponent buildParticles(ServerLevel serverLevel, @NotNull List<FixedParticleChannel> fixedChannelList, @NotNull List<EntityParticleTask> entityTaskList) {
        MutableComponent component = Component.empty();

        int size1 = fixedChannelList.size();
        component.append("FixedParticle")
                .append(Component.literal("["))
                .append(Component.literal(String.valueOf(size1)).withStyle(size1 > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal("]"));

        for (FixedParticleChannel channel : fixedChannelList) {
            component.append(Component.literal(" "))
                    .append(buildFixedParticleChannelSimple(channel));
        }
        component.append(Component.literal("\n"));

        int size2 = entityTaskList.size();
        component.append("EntityTask")
                .append(Component.literal("["))
                .append(Component.literal(String.valueOf(size2)).withStyle(size2 > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal("]"));

        for (EntityParticleTask task : entityTaskList) {
            component.append(Component.literal(" "))
                    .append(buildEntityParticleSimple(serverLevel, task));
        }

        return component;
    }

    public static MutableComponent buildFixedParticleChannelSimple(FixedParticleChannel fixedChannel) {
        MutableComponent component = Component.empty();

        int particleSize = fixedChannel.particles.size();
        String command = GetEffect.getParticleChannelCommand(fixedChannel.channelKey, 0, 10);
        component.append(Component.literal(fixedChannel.channelKey))
                .append(buildRunnableIntBracketWithColor(particleSize, command, particleSize > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY));

        return component;
    }

    public static MutableComponent buildEntityParticleSimple(ServerLevel serverLevel, EntityParticleTask entityTask) {
        MutableComponent component = Component.empty();

        // [channelSize]
        int channelSize = entityTask.channels.size();
        component.append(Component.literal("["))
                .append(Component.literal(String.valueOf(channelSize)).withStyle(channelSize > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal("]"));

        // Name / UUID
        Entity entity = serverLevel.getEntity(entityTask.entityUUID);
        if (entity != null) {
            component.append(buildHoverableText(entity.getName().getString(), entity.getUUID().toString()));
        } else {
            component.append(buildHoverableText("UUID", entityTask.entityUUID.toString()));
        }

        for (Map.Entry<String, EntityParticleChannel> entry : entityTask.channels.entrySet()) {
            String channelName = entry.getKey();
            EntityParticleChannel channel = entry.getValue();
            int particleSize = channel.particles.size();
            String command = entity != null ? GetEffect.getParticleEntityCommand(entity, channelName, 0, 10) : "";
            component.append(buildRunnableIntBracketWithFullColor(particleSize, command, TextColor.fromLegacyFormat(entity != null && particleSize > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY)));
        }

        return component;
    }

    public static MutableComponent buildParticleCommon(ParticleData particleData) {
        MutableComponent component = Component.empty();

        // ParticleData
        component.append(buildHoverableTextWithColor("ParticleData",
                Component.empty()
                        .append(Component.literal("delayRemain"))
                        .append(Component.literal(":" + particleData.delayRemain))
                        .append(Component.literal("\n"))
                        .append(Component.literal("finishedRepeat").withStyle(particleData.finishedRepeat > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                        .append(Component.literal(":" + particleData.finishedRepeat))
                        .append(Component.literal("\n"))
                        .append(Component.literal("worldTime"))
                        .append(Component.literal(":" + particleData.worldTime))
                        .append(Component.literal("\n"))
                        .append(Component.literal("serverLevel"))
                        .append(Component.literal(":" + particleData.serverLevel)),
                ChatFormatting.GRAY
                )
        );

        // JsonConfig
        ParticleDetailEntry detailEntry = particleData.particle;
        JsonObject entryJson = detailEntry.toJson();
        CompoundTag jsonNbt = new CompoundTag();
        for (String key : entryJson.keySet()) {
            JsonElement jsonElement =  entryJson.get(key);
            jsonNbt.putString(key, jsonElement.toString()); // JsonObject不支持直接getAsString
        }
        MutableComponent jsonComponent = buildNbtVerticalList(jsonNbt);

        component.append(Component.literal("|").withStyle(ChatFormatting.WHITE))
                .append(buildHoverableTextWithColor("DetailEntry", jsonComponent, ChatFormatting.AQUA));

        return component;
    }

    public static MutableComponent buildFixedParticle(List<FixedParticleData> fixedParticles) {
        MutableComponent component = Component.empty();

        int size = fixedParticles.size();
        component.append(Component.literal("["))
                .append(Component.literal(String.valueOf(size)).withStyle(size > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal("]"));

        for (FixedParticleData particle : fixedParticles) {
            component.append(" ")
                    .append(buildVanillaTeleport(particle.particlePos))
                    .append(Component.literal("|"))
                    .append(buildParticleCommon(particle));
        }

        BattleRoyale.LOGGER.info("buildFixedParticle");
        return component;
    }
    public static MutableComponent buildEntityParticle(ServerLevel serverLevel, List<ParticleData> particles, Entity entity) {
        MutableComponent component = Component.empty();
        try {
            int size = particles.size();
            component.append(Component.literal("["))
                    .append(Component.literal(String.valueOf(size)).withStyle(size > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                    .append(Component.literal("]"));

            // Name
            if (entity != null && serverLevel.getEntity(entity.getUUID()) != null) {
                component.append(buildHoverableTextWithColor(
                        entity.getName().getString(),
                        entity.getUUID().toString(),
                        ChatFormatting.GRAY));
            }

            for (ParticleData particle : particles) {
                component.append("\n")
                        .append(buildParticleCommon(particle));
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Error in buildEntityParticle", e);
        }
        BattleRoyale.LOGGER.info("buildEntityParticle");
        return component;
    }
    
    public static MutableComponent buildFireworkTasks(ServerLevel serverLevel, List<FixedFireworkTask> fixedTasks, List<PlayerTrackingFireworkTask> playerTasks) {
        MutableComponent component = Component.empty();

        component.append(Component.literal("["))
                .append(Component.literal(String.valueOf(fixedTasks.size())).withStyle(!fixedTasks.isEmpty() ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
                .append(Component.literal("]FixedFireworkTask"));

        for (FixedFireworkTask fixedTask : fixedTasks) {
            component.append(Component.literal(" "))
                    .append(buildFixedFireworkTask(fixedTask));
        }
        component.append(Component.literal("\n"));

        component.append(Component.literal("["))
                .append(Component.literal(String.valueOf(playerTasks.size())).withStyle(!playerTasks.isEmpty() ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
                .append(Component.literal("]PlayerTrackingTask"));

        for (PlayerTrackingFireworkTask playerTask : playerTasks) {
            component.append(Component.literal(" "))
                    .append(buildPlayerFireworkTaskSimple(serverLevel, playerTask));
        }

        return component;
    }

    public static MutableComponent buildPlayerFireworkTaskSimple(ServerLevel serverLevel, PlayerTrackingFireworkTask playerTask) {
        MutableComponent component = Component.empty();

        // [singleId]
        UUID playerUUID = playerTask.getPlayerUUID();
        GamePlayer gamePlayer = GameTeamManager.getGamePlayerByUUID(playerUUID);
        if (gamePlayer != null) {
            int singleId = gamePlayer.getGameSingleId();
            String teamColor = gamePlayer.getGameTeamColor();
            int colorRGB = ColorUtils.parseColorToInt(teamColor) & 0xFFFFFF;
            TextColor textColor = TextColor.fromRgb(colorRGB);

            String command = GetEffect.getFireworkCommand(singleId);
            component.append(buildRunnableIntBracketWithColor(singleId, command, textColor))
                    .append(Component.literal(gamePlayer.getPlayerName()));
        }

        // Name
        Entity entity = serverLevel.getEntity(playerUUID);
        if (entity != null) {
            component.append(buildRunnableText(entity.getName().getString(), GetEffect.getFireworkByEntityCommand(entity), ChatFormatting.GRAY));
        }

        return component;
    }

    public static MutableComponent buildFireworkCommon(AbstractFireworkTask fireworkTask) {
        MutableComponent component = Component.empty();

        component.append(buildHoverableTextWithColor("fireworkTask",
                Component.empty()
                        .append(Component.literal("interval"))
                        .append(Component.literal(":" + fireworkTask.getInterval()))
                        .append(Component.literal("\n"))
                        .append(Component.literal("currentDelay"))
                        .append(Component.literal(":" + fireworkTask.getCurrentDelay()))
                        .append(Component.literal("\n"))
                        .append(Component.literal("verticalRange"))
                        .append(Component.literal(":" + fireworkTask.getVerticalRange()))
                        .append(Component.literal("\n"))
                        .append(Component.literal("horizontalRange"))
                        .append(Component.literal(":" + fireworkTask.getHorizontalRange()))
                        .append(Component.literal("\n"))
                        .append(Component.literal("remain"))
                        .append(Component.literal(":" + fireworkTask.getRemainingAmount()))
                        .append(Component.literal("\n"))
                        .append(Component.literal("worldTime"))
                        .append(Component.literal(":" + fireworkTask.getWorldTime()))
                        .append(Component.literal("\n"))
                        .append(Component.literal("serverLevel"))
                        .append(Component.literal(":" + fireworkTask.getServerLevel())),
                ChatFormatting.GRAY
                )
        );

        return component;
    }

    public static MutableComponent buildFixedFireworkTask(FixedFireworkTask fixedTask) {
        MutableComponent component = Component.empty();
        if (fixedTask == null) {
            return component;
        }

        Vec3 pos = fixedTask.getInitialPos();
        String command = buildVanillaTeleport(pos);
        int remain = fixedTask.getRemainingAmount();
        component.append(buildRunnableIntBracketWithColor(remain, command, remain > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(buildFireworkCommon(fixedTask));

        return component;
    }

    public static MutableComponent buildPlayerFireworkTasks(ServerLevel serverLevel, List<PlayerTrackingFireworkTask> playerTasks) {
        MutableComponent component = Component.empty();

        component.append(Component.literal("["))
                .append(Component.literal(String.valueOf(playerTasks.size())).withStyle(!playerTasks.isEmpty() ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
                .append(Component.literal("]"))
                .append(Component.literal("UUID firework"));

        if (!playerTasks.isEmpty()) {
            UUID playerUUID = playerTasks.get(0).getPlayerUUID();
            Entity entity = serverLevel.getEntity(playerUUID);
            if (entity != null) {
                component.append(Component.literal(" "));
                GamePlayer gamePlayer = GameTeamManager.getGamePlayerByUUID(playerUUID);
                if (gamePlayer != null) {
                    int singleId = gamePlayer.getGameSingleId();
                    String teamColor = gamePlayer.getGameTeamColor();
                    int colorRGB = ColorUtils.parseColorToInt(teamColor) & 0xFFFFFF;
                    TextColor textColor = TextColor.fromRgb(colorRGB);

                    String gamePlayerCommand = GetGame.getGamePlayerCommand(singleId);
                    component.append(buildRunnableIntBracketWithColor(singleId, gamePlayerCommand, textColor))
                            .append(Component.literal(gamePlayer.getPlayerName()).withStyle(ChatFormatting.WHITE));
                } else {
                    component.append(Component.literal(entity.getName().getString()));
                }
            }
        }

        for (PlayerTrackingFireworkTask playerTask : playerTasks) {
            component.append(Component.literal(" "))
                    .append(buildFireworkCommon(playerTask));
        }
        return component;
    }

    public static MutableComponent buildMutekiTasks(ServerLevel serverLevel, List<EntityMutekiTask> mutekiTasks) {
        MutableComponent component = Component.empty();

        component.append(Component.literal("["))
                .append(Component.literal(String.valueOf(mutekiTasks.size())).withStyle(!mutekiTasks.isEmpty() ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
                .append(Component.literal("]MutekiTasks"));

        for (EntityMutekiTask mutekiTask : mutekiTasks) {
            component.append(Component.literal(" "))
                    .append(buildMutekiTaskSimple(serverLevel, mutekiTask));
        }

        return component;
    }

    public static MutableComponent buildMutekiTaskSimple(ServerLevel serverLevel, EntityMutekiTask mutekiTask) {
        MutableComponent component = Component.empty();
        if (mutekiTask == null) {
            return component;
        }

        // [singleId]
        GamePlayer gamePlayer = GameTeamManager.getGamePlayerByUUID(mutekiTask.getEntityUUID());
        if (gamePlayer != null) {
            String command = GetEffect.getMutekiCommand(gamePlayer.getGameSingleId());
            TextColor textColor = TextColor.fromRgb(ColorUtils.parseColorToInt(gamePlayer.getGameTeamColor()));
            component.append(buildRunnableIntBracketWithFullColor(
                    gamePlayer.getGameSingleId(),
                    command,
                    textColor));
        }

        // Name
        Entity entity = serverLevel.getEntity(mutekiTask.getEntityUUID());
        if (entity != null) {
            String command = GetEffect.getMutekiCommand(entity);
            component.append(buildRunnableText(
                    entity.getName().getString(),
                    command,
                    ChatFormatting.WHITE));
        }

        // uuid
        if (gamePlayer == null && entity == null) {
            component.append(buildHoverableTextWithColor("uuid", mutekiTask.getEntityUUID().toString(), ChatFormatting.GRAY));
        }

        return component;
    }

    public static MutableComponent buildMutekiTask(ServerLevel serverLevel, EntityMutekiTask mutekiTask) {
        MutableComponent component = Component.empty();
        if (mutekiTask == null) {
            return component;
        }

        component.append(buildMutekiTaskSimple(serverLevel, mutekiTask))
                .append(Component.literal(" "));

        // config
        int c1 = MutekiManager.getMaxMutekiTime();
        int c1_ = MutekiManager.getMaxMutekiTimeDefault();
        component.append(buildHoverableTextWithColor("mutekiConfig",
                Component.empty()
                        .append(Component.literal("maxMutekiTime").withStyle(c1 > c1_ ? ChatFormatting.AQUA : c1 == c1_ ? ChatFormatting.GRAY : ChatFormatting.RED))
                        .append(Component.literal(":" + c1))
                        .append(Component.literal("\n"))
                        .append(Component.literal("defaultTime"))
                        .append(Component.literal(":" + MutekiCommand.DEFAULT_TIME)),
                ChatFormatting.GRAY
                )
        );
        component.append(Component.literal(" "));

        // muteki
        component.append(buildHoverableTextWithColor("mutekiTask",
                Component.empty()
                        .append(Component.literal("Name"))
                        .append(Component.literal(":" + mutekiTask.getName()))
                        .append(Component.literal("\n"))
                        .append(Component.literal("remainTime"))
                        .append(Component.literal(":" + mutekiTask.getRemainTime()))
                        .append(Component.literal("\n"))
                        .append(Component.literal("notice").withStyle(mutekiTask.isNotice() ? ChatFormatting.AQUA : ChatFormatting.GRAY))
                        .append(Component.literal(":" + mutekiTask.isNotice()))
                        .append(Component.literal("\n"))
                        .append(Component.literal("UUID"))
                        .append(Component.literal(":" + mutekiTask.getEntityUUID()))
                        .append(Component.literal("\n"))
                        .append(Component.literal("worldTime"))
                        .append(Component.literal(":" + mutekiTask.getWorldTime()))
                        .append(Component.literal("\n"))
                        .append(Component.literal("serverLevel"))
                        .append(Component.literal(":" + mutekiTask.getServerLevel())),
                ChatFormatting.GRAY
                )
        );

        return component;
    }

    public static MutableComponent buildBoostData(ServerLevel serverLevel, List<BoostData> boostData) {
        MutableComponent component = Component.empty();

        component.append(Component.literal("["))
                .append(Component.literal(String.valueOf(boostData.size())).withStyle(!boostData.isEmpty() ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
                .append(Component.literal("]BoostData"));

        for (BoostData data : boostData) {
            component.append(Component.literal(" "));
            component.append(buildBoostSimple(serverLevel, data));
        }

        return component;
    }
    public static MutableComponent buildBoostSimple(ServerLevel serverLevel, BoostData data) {
        MutableComponent component = Component.empty();
        if (data == null) {
            return component;
        }

        // [singleId]
        GamePlayer gamePlayer = GameTeamManager.getGamePlayerByUUID(data.uuid);
        if (gamePlayer != null) {
            String command = GetEffect.getBoostCommand(gamePlayer.getGameSingleId());
            TextColor textColor = TextColor.fromRgb(ColorUtils.parseColorToInt(gamePlayer.getGameTeamColor()));
            component.append(buildRunnableIntBracketWithFullColor(
                    gamePlayer.getGameSingleId(),
                    command,
                    textColor));
        }

        // Name
        Entity entity = serverLevel.getEntity(data.uuid);
        if (entity != null) {
            String command = GetEffect.getBoostCommand(entity);
            component.append(buildRunnableText(
                    entity.getName().getString(),
                    command,
                    ChatFormatting.WHITE));
        }

        // uuid
        if (gamePlayer == null && entity == null) {
            component.append(buildHoverableText("uuid", data.uuid.toString()));
        }

        return component;
    }

    public static MutableComponent buildBoost(ServerLevel serverLevel, BoostData data) {
        MutableComponent component = Component.empty();

        component.append(buildBoostSimple(serverLevel, data))
                .append(Component.literal(" "));

        // config
        int c1 = BoostData.getBoostLimit();
        int c1_ = BoostData.boostLimitDefault();
        BoostManager boostManager = BoostManager.get();
        int c2 = boostManager.healCooldown();
        int c2_ = boostManager.healCooldownDefault();
        int c3 = boostManager.effectCooldown();
        int c3_ = boostManager.effectCooldown();
        int c4 = boostManager.syncFrequency();
        component.append(buildHoverableTextWithColor("boostConfig",
                Component.empty()
                        .append(Component.literal("boostLimit").withStyle(c1 > c1_ ? ChatFormatting.AQUA : c1 == c1_ ? ChatFormatting.GRAY : ChatFormatting.RED))
                        .append(Component.literal(":" + c1))
                        .append(Component.literal("\n"))
                        .append(Component.literal("healCooldown").withStyle(c2 > c2_ ? ChatFormatting.AQUA : c2 == c2_ ? ChatFormatting.GRAY : ChatFormatting.RED))
                        .append(Component.literal(":" + c2))
                        .append(Component.literal("\n"))
                        .append(Component.literal("effectCooldown").withStyle(c3 > c3_ ? ChatFormatting.AQUA : c3 == c3_ ? ChatFormatting.GRAY : ChatFormatting.RED))
                        .append(Component.literal(":" + c3))
                        .append(Component.literal("\n"))
                        .append(Component.literal("syncCooldown"))
                        .append(Component.literal(":" + c4)),
                ChatFormatting.GRAY
                )
        );
        component.append(Component.literal(" "));

        // boost
        int b1 = data.boost();
        int b2 = BoostData.getBoostLevel(b1);
        int boostColor = BoostData.getBoostColorInt(b2);
        Style style = Style.EMPTY.withColor(TextColor.fromRgb(boostColor));
        int b3 = data.getHealCooldown();
        int b4 = data.getEffectCooldown();
        int b5 = data.getSyncCooldown();
        component.append(buildHoverableTextWithColor("boostData",
                Component.empty()
                        .append(Component.literal("boost").withStyle(b1 > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                        .append(Component.literal(":" + b1))
                        .append(Component.literal("\n"))
                        .append(Component.literal("serverLevel").withStyle(style))
                        .append(Component.literal(":" + b2))
                        .append(Component.literal("\n"))
                        .append(Component.literal("healCooldown"))
                        .append(Component.literal(":" + b3))
                        .append(Component.literal("\n"))
                        .append(Component.literal("effectCooldown"))
                        .append(Component.literal(":" + b4))
                        .append(Component.literal("\n"))
                        .append(Component.literal("syncCooldown"))
                        .append(Component.literal(":" + b5))
                        .append(Component.literal("\n"))
                        .append(Component.literal("UUID"))
                        .append(Component.literal(":" + data.uuid))
                        .append(Component.literal("\n"))
                        .append(Component.literal("worldTime"))
                        .append(Component.literal(":" + data.worldTime))
                        .append(Component.literal("\n"))
                        .append(Component.literal("serverLevel"))
                        .append(Component.literal(":" + data.serverLevel)),
                b1 > 0 ? ChatFormatting.AQUA : ChatFormatting.GRAY
                )
        );

        return component;
    }
}
