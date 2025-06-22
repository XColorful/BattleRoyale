package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.effect.EffectManager;

import static xiao.battleroyale.command.CommandArg.*;

public class ParticleCommand {

    private static final int DEFAULT_PARTICLE_ID = 0;
    private static final int DEFAULT_CHANNEL_COOLDOWN = 20;

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        LiteralArgumentBuilder<CommandSourceStack> particleCommand = Commands.literal(PARTICLE);

        // /battleroyale particle [~ ~ ~] [ID] [COOLDOWN]
        RequiredArgumentBuilder<CommandSourceStack, Integer> cooldownArg_pos = Commands.argument(COOLDOWN, IntegerArgumentType.integer(0));
        cooldownArg_pos.requires(source -> source.hasPermission(3))
                .executes(ParticleCommand::executeParticleAtPosWithIdAndCooldown);

        RequiredArgumentBuilder<CommandSourceStack, Integer> particleIdArg_pos = Commands.argument(ID, IntegerArgumentType.integer(0));
        particleIdArg_pos.executes(ParticleCommand::executeParticleAtPosWithId);
        particleIdArg_pos.then(cooldownArg_pos);

        RequiredArgumentBuilder<CommandSourceStack, Coordinates> coordArg = Commands.argument(XYZ, Vec3Argument.vec3());
        coordArg.then(particleIdArg_pos);

        // /battleroyale particle [ID] [COOLDOWN]
        RequiredArgumentBuilder<CommandSourceStack, Integer> cooldownArg = Commands.argument(COOLDOWN, IntegerArgumentType.integer(0));
        cooldownArg.requires(source -> source.hasPermission(3))
                .executes(ParticleCommand::executeParticleWithIdAndCooldown);

        RequiredArgumentBuilder<CommandSourceStack, Integer> particleIdArg = Commands.argument(ID, IntegerArgumentType.integer(0));
        particleIdArg.executes(ParticleCommand::executeParticleWithId);
        particleIdArg.then(cooldownArg);

        particleCommand.then(coordArg);
        particleCommand.then(particleIdArg);
        particleCommand.executes(ParticleCommand::executeParticleDefault);

        // /battleroyale particle clear
        LiteralArgumentBuilder<CommandSourceStack> clearCommand = Commands.literal(CLEAR);
        clearCommand.executes(ParticleCommand::executeClearParticles);

        // /battleroyale particle clear all
        clearCommand.then(Commands.literal(ALL)
                .executes(ParticleCommand::executeClearAllParticles));

        particleCommand.then(clearCommand);

        return particleCommand;
    }

    private static int executeParticleDefault(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Vec3 spawnPos = source.getPosition();

        return spawnParticle(source, spawnPos, DEFAULT_PARTICLE_ID, DEFAULT_CHANNEL_COOLDOWN);
    }
    private static int executeParticleWithId(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int particleId = IntegerArgumentType.getInteger(context, ID);
        CommandSourceStack source = context.getSource();
        Vec3 spawnPos = source.getPosition();

        return spawnParticle(source, spawnPos, particleId, DEFAULT_CHANNEL_COOLDOWN);
    }
    private static int executeParticleWithIdAndCooldown(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int particleId = IntegerArgumentType.getInteger(context, ID);
        int cooldown = IntegerArgumentType.getInteger(context, COOLDOWN);
        CommandSourceStack source = context.getSource();
        Vec3 spawnPos = source.getPosition();

        return spawnParticle(source, spawnPos, particleId, cooldown);
    }
    private static int executeParticleAtPosWithId(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Vec3 spawnPos = Vec3Argument.getVec3(context, XYZ);
        int particleId = IntegerArgumentType.getInteger(context, ID);
        CommandSourceStack source = context.getSource();

        return spawnParticle(source, spawnPos, particleId, DEFAULT_CHANNEL_COOLDOWN);
    }
    private static int executeParticleAtPosWithIdAndCooldown(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Vec3 spawnPos = Vec3Argument.getVec3(context, XYZ);
        int particleId = IntegerArgumentType.getInteger(context, ID);
        int cooldown = IntegerArgumentType.getInteger(context, COOLDOWN);
        CommandSourceStack source = context.getSource();

        return spawnParticle(source, spawnPos, particleId, cooldown);
    }

    private static int spawnParticle(CommandSourceStack source, Vec3 spawnPos, int particleId, int cooldown) {
        if (source.getEntity() instanceof ServerPlayer player) {
            String channelKey = player.getName().getString();
            if (EffectManager.get().addParticle(source.getLevel(), spawnPos, channelKey, particleId, cooldown)) {
                source.sendSuccess(() -> Component.translatable("battleroyale.message.add_particle", particleId, String.format("%.2f", spawnPos.x), String.format("%.2f", spawnPos.y), String.format("%.2f", spawnPos.z)), true);
                return Command.SINGLE_SUCCESS;
            } else {
                source.sendFailure(Component.translatable("battleroyale.message.failed_add_particle", particleId));
                return 0;
            }
        } else {
            if (EffectManager.get().addCommandParticle(source.getLevel(), spawnPos, particleId, cooldown)) {
                BattleRoyale.LOGGER.info("Add particle {} at {}", particleId, spawnPos);
                return Command.SINGLE_SUCCESS;
            } else {
                BattleRoyale.LOGGER.info("Failed to add particle {}, either missing config or in cooldown", particleId);
                return 0;
            }
        }
    }

    private static int executeClearParticles(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            EffectManager.get().clearParticle(player.getUUID());
        } else {
            EffectManager.get().clearCommandParticle();
        }
        source.sendSuccess(() -> Component.translatable("battleroyale.message.clear_particle"), true);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeClearAllParticles(CommandContext<CommandSourceStack> context) {
        EffectManager.get().clearParticle();
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.clear_all_particle"), true);
        return Command.SINGLE_SUCCESS;
    }
}