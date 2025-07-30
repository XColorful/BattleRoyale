package xiao.battleroyale.developer.debug.command.sub.get;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import xiao.battleroyale.developer.debug.DebugEffect;
import xiao.battleroyale.developer.debug.DebugManager;

import static xiao.battleroyale.developer.debug.command.CommandArg.*;
import static xiao.battleroyale.developer.debug.command.sub.GetCommand.buildDebugCommandString;
import static xiao.battleroyale.util.CommandUtils.buildNearestEntitySelect;

public class GetEffect {

    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> getCommand, boolean useFullName) {

        // 获取粒子队列
        // get particles [min max / all]
        getCommand.then(Commands.literal(useFullName ? PARTICLES : PARTICLES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> getParticles(context, Integer.MIN_VALUE, Integer.MAX_VALUE - 1)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> getParticles(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get particle [channel / entity channel] [min max / all]
        getCommand.then(Commands.literal(useFullName ? PARTICLE : PARTICLE_SHORT)
                .then(Commands.argument(CHANNEL, StringArgumentType.string())
                        .then(Commands.literal(ALL)
                                .executes(context -> getParticleByChannel(context, StringArgumentType.getString(context, CHANNEL), Integer.MIN_VALUE, Integer.MAX_VALUE - 1)))
                        .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                                .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                        .executes(context -> getParticleByChannel(context,
                                                StringArgumentType.getString(context, CHANNEL),
                                                IntegerArgumentType.getInteger(context, ID_MIN),
                                                IntegerArgumentType.getInteger(context, ID_MAX))))))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .then(Commands.argument(CHANNEL, StringArgumentType.string())
                                .then(Commands.literal(ALL)
                                        .executes(context -> getParticleByEntity(context,
                                                EntityArgument.getEntity(context, ENTITY),
                                                StringArgumentType.getString(context, CHANNEL),
                                                Integer.MIN_VALUE, Integer.MAX_VALUE - 1)))
                                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                                .executes(context -> getParticleByEntity(context,
                                                        EntityArgument.getEntity(context, ENTITY),
                                                        StringArgumentType.getString(context, CHANNEL),
                                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                                        IntegerArgumentType.getInteger(context, ID_MAX))))))));


        // 获取烟花队列
        // get fireworks [min max / all]
        getCommand.then(Commands.literal(useFullName ? FIREWORKS : FIREWORKS_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> getFireworks(context, Integer.MIN_VALUE, Integer.MAX_VALUE - 1)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> getFireworks(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get firework [id / entity]
        getCommand.then(Commands.literal(useFullName ? FIREWORK : FIREWORK_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetEffect::getFirework))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .executes(GetEffect::getFireworkByEntity)));

        // 获取无敌队列
        // get mutekis [min max / all]
        getCommand.then(Commands.literal(useFullName ? MUTEKIS : MUTEKIS_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> getMutekis(context, Integer.MIN_VALUE, Integer.MAX_VALUE - 1)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> getMutekis(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get muteki [id / entity]
        getCommand.then(Commands.literal(useFullName ? MUTEKI : MUTEKI_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetEffect::getMutekiById))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .executes(GetEffect::getMutekiByEntity)));

        // 获取能量队列
        // get boosts [min max / all]
        getCommand.then(Commands.literal(useFullName ? BOOSTS : BOOSTS_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> getBoosts(context, Integer.MIN_VALUE, Integer.MAX_VALUE - 1)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> getBoosts(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get boost [id / entity]
        getCommand.then(Commands.literal(useFullName ? BOOST : BOOST_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetEffect::getBoostById))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .executes(GetEffect::getBoostByEntity)));
    }

    /**
     * 获取粒子队列
     */
    private static int getParticles(CommandContext<CommandSourceStack> context, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugEffect.get().getParticles(source, min, max);
        return Command.SINGLE_SUCCESS;
    }
    private static int getParticleByChannel(CommandContext<CommandSourceStack> context, String channel, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugEffect.get().getParticle(source, channel, min, max);
        return Command.SINGLE_SUCCESS;
    }
    private static int getParticleByEntity(CommandContext<CommandSourceStack> context, Entity entity, String channel, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugEffect.get().getParticle(source, entity, channel, min, max);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取烟花队列
     */
    private static int getFireworks(CommandContext<CommandSourceStack> context, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugEffect.get().getFireworks(source, min, max);
        return Command.SINGLE_SUCCESS;
    }
    private static int getFirework(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugEffect.get().getFirework(source, IntegerArgumentType.getInteger(context, SINGLE_ID));
        return Command.SINGLE_SUCCESS;
    }
    private static int getFireworkByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugEffect.get().getFirework(source, EntityArgument.getEntity(context, ENTITY));
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取无敌队列
     */
    private static int getMutekis(CommandContext<CommandSourceStack> context, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugEffect.get().getMutekis(source, min, max);
        return Command.SINGLE_SUCCESS;
    }
    private static int getMutekiById(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugEffect.get().getMuteki(source, IntegerArgumentType.getInteger(context, SINGLE_ID));
        return Command.SINGLE_SUCCESS;
    }
    private static int getMutekiByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugEffect.get().getMuteki(source, EntityArgument.getEntity(context, ENTITY));
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取能量队列
     */
    private static int getBoosts(CommandContext<CommandSourceStack> context, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugEffect.get().getBoosts(source, min, max);
        return Command.SINGLE_SUCCESS;
    }
    private static int getBoostById(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugEffect.get().getBoost(source, IntegerArgumentType.getInteger(context, SINGLE_ID));
        return Command.SINGLE_SUCCESS;
    }
    private static int getBoostByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugEffect.get().getBoost(source, EntityArgument.getEntity(context, ENTITY));
        return Command.SINGLE_SUCCESS;
    }

    public static String getParticleChannelCommand(String channel, int min, int max) {
        return buildDebugCommandString(
                GET,
                PARTICLE,
                channel,
                Integer.toString(min),
                Integer.toString(max)
        );
    }
    public static String getParticleEntityCommand(Entity entity, String channel, int min, int max) {
        return buildDebugCommandString(
                GET,
                PARTICLE,
                buildNearestEntitySelect(entity),
                channel,
                Integer.toString(min),
                Integer.toString(max)
        );
    }

    public static String getFireworkCommand(int id) {
        return buildDebugCommandString(
                GET,
                FIREWORK,
                Integer.toString(id)
        );
    }
    public static String getFireworkByEntityCommand(Entity entity) {
        return buildDebugCommandString(
                GET,
                FIREWORK,
                buildNearestEntitySelect(entity)
        );
    }
    public static String getFireworksCommand(int min, int max) {
        return buildDebugCommandString(
                GET,
                FIREWORKS,
                Integer.toString(min),
                Integer.toString(max)
        );
    }

    public static String getMutekiCommand(int singleId) {
        return buildDebugCommandString(
                GET,
                MUTEKI,
                Integer.toString(singleId)
        );
    }
    public static String getMutekiCommand(Entity entity) {
        return buildDebugCommandString(
                GET,
                MUTEKI,
                buildNearestEntitySelect(entity)
        );
    }

    public static String getBoostCommand(int singleId) {
        return buildDebugCommandString(
                GET,
                BOOST,
                Integer.toString(singleId)
        );
    }
    public static String getBoostCommand(Entity entity) {
        return buildDebugCommandString(
                GET,
                BOOST,
                buildNearestEntitySelect(entity)
        );
    }
}