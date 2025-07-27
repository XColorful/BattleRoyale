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

import static xiao.battleroyale.developer.debug.command.CommandArg.*;
import static xiao.battleroyale.developer.debug.command.sub.GetCommand.buildDebugCommandString;

public class GetEffect {

    public static void addEffect(LiteralArgumentBuilder<CommandSourceStack> getCommand, boolean useFullName) {

        // 获取粒子队列
        // get particles [min max / all]
        getCommand.then(Commands.literal(useFullName ? PARTICLES : PARTICLES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> executeGetParticles(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> executeGetParticles(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get particle [channel / entity channel] [min max / all]
        getCommand.then(Commands.literal(useFullName ? PARTICLE : PARTICLE_SHORT)
                .then(Commands.argument(CHANNEL, StringArgumentType.string())
                        .then(Commands.literal(ALL)
                                .executes(context -> executeGetParticleByChannel(context, StringArgumentType.getString(context, CHANNEL), Integer.MIN_VALUE, Integer.MAX_VALUE)))
                        .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                                .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                        .executes(context -> executeGetParticleByChannel(context,
                                                StringArgumentType.getString(context, CHANNEL),
                                                IntegerArgumentType.getInteger(context, ID_MIN),
                                                IntegerArgumentType.getInteger(context, ID_MAX))))))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .then(Commands.argument(CHANNEL, StringArgumentType.string())
                                .then(Commands.literal(ALL)
                                        .executes(context -> executeGetParticleByEntity(context,
                                                EntityArgument.getEntity(context, ENTITY),
                                                StringArgumentType.getString(context, CHANNEL),
                                                Integer.MIN_VALUE, Integer.MAX_VALUE)))
                                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                                .executes(context -> executeGetParticleByEntity(context,
                                                        EntityArgument.getEntity(context, ENTITY),
                                                        StringArgumentType.getString(context, CHANNEL),
                                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                                        IntegerArgumentType.getInteger(context, ID_MAX))))))));


        // 获取烟花队列
        // get fireworks [min max / all]
        getCommand.then(Commands.literal(useFullName ? FIREWORKS : FIREWORKS_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> executeGetFireworks(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> executeGetFireworks(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get firework [id / entity]
        getCommand.then(Commands.literal(useFullName ? FIREWORK : FIREWORK_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetEffect::executeGetFirework))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .executes(GetEffect::executeGetFireworkByEntity)));

        // 获取无敌队列
        // get mutekis [min max / all]
        getCommand.then(Commands.literal(useFullName ? MUTEKIS : MUTEKIS_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> executeGetMutekis(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> executeGetMutekis(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get muteki [id / entity]
        getCommand.then(Commands.literal(useFullName ? MUTEKI : MUTEKI_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetEffect::executeGetMutekiById))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .executes(GetEffect::executeGetMutekiByEntity)));

        // 获取能量队列
        // get boosts [min max / all]
        getCommand.then(Commands.literal(useFullName ? BOOSTS : BOOSTS_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> executeGetBoosts(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> executeGetBoosts(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get boost [id / entity]
        getCommand.then(Commands.literal(useFullName ? BOOST : BOOST_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetEffect::executeGetBoostById))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .executes(GetEffect::executeGetBoostByEntity)));
    }

    /**
     * 获取粒子队列
     */
    private static int executeGetParticles(CommandContext<CommandSourceStack> context, int min, int max) {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get particles (all)"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get particles with min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetParticleByChannel(CommandContext<CommandSourceStack> context, String channel, int min, int max) {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get particle (all) with channel: " + channel), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get particle with channel: " + channel + ", min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetParticleByEntity(CommandContext<CommandSourceStack> context, Entity entity, String channel, int min, int max) throws CommandSyntaxException {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get particle (all) for entity: " + entity.getName().getString() + " with channel: " + channel), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get particle for entity: " + entity.getName().getString() + ", channel: " + channel + ", min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取烟花队列
     */
    private static int executeGetFireworks(CommandContext<CommandSourceStack> context, int min, int max) {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get fireworks (all)"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get fireworks with min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetFirework(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing get firework by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetFireworkByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing get firework by Entity: " + entity.getName().getString() + " (UUID: " + entity.getUUID().toString() + ")"), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取无敌队列
     */
    private static int executeGetMutekis(CommandContext<CommandSourceStack> context, int min, int max) {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get mutekis (all)"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get mutekis with min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetMutekiById(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing get muteki by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetMutekiByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing get muteki by Entity: " + entity.getName().getString() + " (UUID: " + entity.getUUID().toString() + ")"), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取能量队列
     */
    private static int executeGetBoosts(CommandContext<CommandSourceStack> context, int min, int max) {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get boosts (all)"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get boosts with min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetBoostById(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing get boost by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetBoostByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing get boost by Entity: " + entity.getName().getString() + " (UUID: " + entity.getUUID().toString() + ")"), false);
        return Command.SINGLE_SUCCESS;
    }

    public static String getParticleChannelCommandString(String channel, int min, int max) {
        return buildDebugCommandString(
                GET,
                PARTICLE,
                channel,
                Integer.toString(min),
                Integer.toString(max)
        );
    }
    public static String getParticleEntityCommandString(Entity entity, String channel, int min, int max) {
        return buildDebugCommandString(
                GET,
                PARTICLE,
                entity.getName().getString(),
                channel,
                Integer.toString(min),
                Integer.toString(max)
        );
    }

    public static String getFireworkCommandString(int id) {
        return buildDebugCommandString(
                GET,
                FIREWORK,
                Integer.toString(id)
        );
    }
    public static String getFireworkByEntityCommandString(Entity entity) {
        return buildDebugCommandString(
                GET,
                FIREWORK,
                entity.getName().getString()
        );
    }
    public static String getFireworksCommandString(int min, int max) {
        return buildDebugCommandString(
                GET,
                FIREWORKS,
                Integer.toString(min),
                Integer.toString(max)
        );
    }
}