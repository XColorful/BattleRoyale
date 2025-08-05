package xiao.battleroyale.developer.gm.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import static xiao.battleroyale.developer.gm.command.CommandArg.*;

public class EffectManager {
    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> gmCommand, boolean useFullName) {
        // 删除粒子队列
        // /battleroyale gamemaster delete particle [entity / all]
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? PARTICLE : PARTICLE_SHORT)
                        .then(Commands.literal(ALL)
                                .executes(context -> deleteParticle(context, null)))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .executes(context -> deleteParticle(context, EntityArgument.getEntity(context, ENTITY))))));
        // 删除烟花队列
        // /battleroyale gamemaster delete firework [entity / min max / all]
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? FIREWORK : FIREWORK_SHORT)
                        .then(Commands.literal(ALL)
                                .executes(context -> deleteFirework(context, null, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .executes(context -> deleteFirework(context, EntityArgument.getEntity(context, ENTITY), Integer.MIN_VALUE, Integer.MAX_VALUE)))
                        .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                                .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                        .executes(context -> deleteFirework(context, null,
                                                IntegerArgumentType.getInteger(context, ID_MIN),
                                                IntegerArgumentType.getInteger(context, ID_MAX)))))));
        // 修改无敌时间上限
        // /battleroyale gamemaster change mutekitime [amount]
        gmCommand.then(Commands.literal(useFullName ? CHANGE : CHANGE_SHORT)
                .then(Commands.literal(useFullName ? MUTEKI_TIME : MUTEKI_TIME_SHORT)
                        .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                .executes(EffectManager::changeMutekiTime))));
        // 修改玩家能量
        // /battleroyale gamemaster change boost [id / entity] [amount]
        gmCommand.then(Commands.literal(useFullName ? CHANGE : CHANGE_SHORT)
                .then(Commands.literal(useFullName ? BOOST : BOOST_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                        .executes(EffectManager::changeBoost)))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                        .executes(EffectManager::changeBoostByEntity)))));
        // 修改能量恢复频率
        // /battleroyale gamemaster change boostheal [amount]
        gmCommand.then(Commands.literal(useFullName ? CHANGE : CHANGE_SHORT)
                .then(Commands.literal(useFullName ? BOOST_HEAL : BOOST_HEAL_SHORT)
                        .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                .executes(EffectManager::changeBoostHeal))));
        // 修改能量效果频率
        // /battleroyale gamemaster change boosteffect [amount]
        gmCommand.then(Commands.literal(useFullName ? CHANGE : CHANGE_SHORT)
                .then(Commands.literal(useFullName ? BOOST_EFFECT : BOOST_EFFECT_SHORT)
                        .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                .executes(EffectManager::changeBoostEffect))));
    }

    /**
     * 删除粒子队列
     */
    private static int deleteParticle(CommandContext<CommandSourceStack> context, Entity entity) throws CommandSyntaxException {
        if (entity == null) {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete all particles"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete particles for entity: " + entity.getName().getString()), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 删除烟花队列
     */
    private static int deleteFirework(CommandContext<CommandSourceStack> context, Entity entity, int min, int max) throws CommandSyntaxException {
        if (entity != null) {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete fireworks for entity: " + entity.getName().getString()), false);
        } else if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete all fireworks"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete fireworks from " + min + " to " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 修改无敌时间上限
     */
    private static int changeMutekiTime(CommandContext<CommandSourceStack> context) {
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing change muteki time to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 修改玩家能量
     */
    private static int changeBoost(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing change boost for ID " + id + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 修改玩家能量
     */
    private static int changeBoostByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing change boost for entity " + entity.getName().getString() + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 修改能量恢复频率
     */
    private static int changeBoostHeal(CommandContext<CommandSourceStack> context) {
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing change boost heal frequency to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 修改能量效果频率
     */
    private static int changeBoostEffect(CommandContext<CommandSourceStack> context) {
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing change boost effect frequency to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }
}