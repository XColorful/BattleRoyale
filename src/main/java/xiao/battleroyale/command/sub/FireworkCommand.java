package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.common.game.effect.EffectManager;
import xiao.battleroyale.common.game.effect.firework.FireworkManager;

import static xiao.battleroyale.command.CommandArg.*;

public class FireworkCommand {

    private static final int DEFAULT_AMOUNT = 5;
    private static final int DEFAULT_INTERVAL = 4;
    private static final float DEFAULT_V_RANGE = 1.0F;
    private static final float DEFAULT_H_RANGE = 1.0F;

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        // 倒序，不然无法执行

        LiteralArgumentBuilder<CommandSourceStack> fireworkCommand = Commands.literal(FIREWORK);
        RequiredArgumentBuilder<CommandSourceStack, Coordinates> coordBase = Commands.argument(XYZ, Vec3Argument.vec3());
        RequiredArgumentBuilder<CommandSourceStack, EntitySelector> playerBase = Commands.argument(PLAYER, EntityArgument.player());

        // firework <x> <y> <z>
        coordBase.executes(FireworkCommand::executeFixedFirework_XYZ);
        RequiredArgumentBuilder<CommandSourceStack, Integer> amountArg_fixed = Commands.argument(AMOUNT, IntegerArgumentType.integer(1));
        RequiredArgumentBuilder<CommandSourceStack, Integer> intervalArg_fixed = Commands.argument(INTERVAL, IntegerArgumentType.integer(0));
        RequiredArgumentBuilder<CommandSourceStack, Float> vRangeArg_fixed = Commands.argument(VERTICAL_RANGE, FloatArgumentType.floatArg(0.0F));
        RequiredArgumentBuilder<CommandSourceStack, Float> hRangeArg_fixed = Commands.argument(HORIZONTAL_RANGE, FloatArgumentType.floatArg(0.0F));
        // firework <x> <y> <z> <amount> <interval> <vRange> <hRange>
        hRangeArg_fixed.executes(FireworkCommand::executeFixedFirework_XYZ_Amount_Interval_VRange_HRange);
        // firework <x> <y> <z> <amount> <interval> <vRange>
        vRangeArg_fixed.then(hRangeArg_fixed).executes(FireworkCommand::executeFixedFirework_XYZ_Amount_Interval_VRange);
        // firework <x> <y> <z> <amount> <interval>
        intervalArg_fixed.then(vRangeArg_fixed).executes(FireworkCommand::executeFixedFirework_XYZ_Amount_Interval);
        // firework <x> <y> <z> <amount>
        amountArg_fixed.then(intervalArg_fixed).executes(FireworkCommand::executeFixedFirework_XYZ_Amount);
        coordBase.then(amountArg_fixed);

        // firework <player>
        playerBase.executes(FireworkCommand::executePlayerTrackingFirework_Player);
        RequiredArgumentBuilder<CommandSourceStack, Integer> amountArg_player = Commands.argument(AMOUNT, IntegerArgumentType.integer(1));
        RequiredArgumentBuilder<CommandSourceStack, Integer> intervalArg_player = Commands.argument(INTERVAL, IntegerArgumentType.integer(0));
        RequiredArgumentBuilder<CommandSourceStack, Float> vRangeArg_player = Commands.argument(VERTICAL_RANGE, FloatArgumentType.floatArg(0.0F));
        RequiredArgumentBuilder<CommandSourceStack, Float> hRangeArg_player = Commands.argument(HORIZONTAL_RANGE, FloatArgumentType.floatArg(0.0F));
        // firework <player> <amount> <interval> <vRange> <hRange>
        hRangeArg_player.executes(FireworkCommand::executePlayerTrackingFirework_Player_Amount_Interval_VRange_HRange);
        // firework <player> <amount> <interval> <vRange>
        vRangeArg_player.then(hRangeArg_player).executes(FireworkCommand::executePlayerTrackingFirework_Player_Amount_Interval_VRange);
        // firework <player> <amount> <interval>
        intervalArg_player.then(vRangeArg_player).executes(FireworkCommand::executePlayerTrackingFirework_Player_Amount_Interval);
        // firework <player> <amount>
        amountArg_player.then(intervalArg_player).executes(FireworkCommand::executePlayerTrackingFirework_Player_Amount);
        playerBase.then(amountArg_player);

        fireworkCommand.then(coordBase);
        fireworkCommand.then(playerBase);
        fireworkCommand.then(Commands.literal(CLEAR)
                .executes(FireworkCommand::executeClearFireworks));

        return fireworkCommand;
    }

    private static int executeFixedFirework_XYZ(CommandContext<CommandSourceStack> context) {
        Vec3 pos = Vec3Argument.getVec3(context, XYZ);
        EffectManager.get().spawnFirework(context.getSource().getLevel(), pos, DEFAULT_AMOUNT, DEFAULT_INTERVAL, DEFAULT_V_RANGE, DEFAULT_H_RANGE);
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.add_fixed_firework", String.format("%.2f", pos.x), String.format("%.2f", pos.y), String.format("%.2f", pos.z)), true);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeFixedFirework_XYZ_Amount(CommandContext<CommandSourceStack> context) {
        Vec3 pos = Vec3Argument.getVec3(context, XYZ);
        int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        EffectManager.get().spawnFirework(context.getSource().getLevel(), pos, amount, DEFAULT_INTERVAL, DEFAULT_V_RANGE, DEFAULT_H_RANGE);
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.add_fixed_firework", String.format("%.2f", pos.x), String.format("%.2f", pos.y), String.format("%.2f", pos.z)), true);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeFixedFirework_XYZ_Amount_Interval(CommandContext<CommandSourceStack> context) {
        Vec3 pos = Vec3Argument.getVec3(context, XYZ);
        int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        int interval = IntegerArgumentType.getInteger(context, INTERVAL);
        EffectManager.get().spawnFirework(context.getSource().getLevel(), pos, amount, interval, DEFAULT_V_RANGE, DEFAULT_H_RANGE);
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.add_fixed_firework", String.format("%.2f", pos.x), String.format("%.2f", pos.y), String.format("%.2f", pos.z)), true);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeFixedFirework_XYZ_Amount_Interval_VRange(CommandContext<CommandSourceStack> context) {
        Vec3 pos = Vec3Argument.getVec3(context, XYZ);
        int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        int interval = IntegerArgumentType.getInteger(context, INTERVAL);
        float vRange = FloatArgumentType.getFloat(context, VERTICAL_RANGE);
        EffectManager.get().spawnFirework(context.getSource().getLevel(), pos, amount, interval, vRange, DEFAULT_H_RANGE);
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.add_fixed_firework", String.format("%.2f", pos.x), String.format("%.2f", pos.y), String.format("%.2f", pos.z)), true);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeFixedFirework_XYZ_Amount_Interval_VRange_HRange(CommandContext<CommandSourceStack> context) {
        Vec3 pos = Vec3Argument.getVec3(context, XYZ);
        int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        int interval = IntegerArgumentType.getInteger(context, INTERVAL);
        float vRange = FloatArgumentType.getFloat(context, VERTICAL_RANGE);
        float hRange = FloatArgumentType.getFloat(context, HORIZONTAL_RANGE);
        EffectManager.get().spawnFirework(context.getSource().getLevel(), pos, amount, interval, vRange, hRange);
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.add_fixed_firework", String.format("%.2f", pos.x), String.format("%.2f", pos.y), String.format("%.2f", pos.z)), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executePlayerTrackingFirework_Player(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, PLAYER);
        EffectManager.get().spawnPlayerFirework(player, DEFAULT_AMOUNT, DEFAULT_INTERVAL, DEFAULT_V_RANGE, DEFAULT_H_RANGE);
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.add_player_firework", player.getName()), true);
        return Command.SINGLE_SUCCESS;
    }
    private static int executePlayerTrackingFirework_Player_Amount(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, PLAYER);
        int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        EffectManager.get().spawnPlayerFirework(player, amount, DEFAULT_INTERVAL, DEFAULT_V_RANGE, DEFAULT_H_RANGE);
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.add_player_firework", player.getName()), true);
        return Command.SINGLE_SUCCESS;
    }
    private static int executePlayerTrackingFirework_Player_Amount_Interval(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, PLAYER);
        int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        int interval = IntegerArgumentType.getInteger(context, INTERVAL);
        EffectManager.get().spawnPlayerFirework(player, amount, interval, DEFAULT_V_RANGE, DEFAULT_H_RANGE);
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.add_player_firework", player.getName()), true);
        return Command.SINGLE_SUCCESS;
    }
    private static int executePlayerTrackingFirework_Player_Amount_Interval_VRange(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, PLAYER);
        int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        int interval = IntegerArgumentType.getInteger(context, INTERVAL);
        float vRange = FloatArgumentType.getFloat(context, VERTICAL_RANGE);
        EffectManager.get().spawnPlayerFirework(player, amount, interval, vRange, DEFAULT_H_RANGE);
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.add_player_firework", player.getName()), true);
        return Command.SINGLE_SUCCESS;
    }
    private static int executePlayerTrackingFirework_Player_Amount_Interval_VRange_HRange(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, PLAYER);
        int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        int interval = IntegerArgumentType.getInteger(context, INTERVAL);
        float vRange = FloatArgumentType.getFloat(context, VERTICAL_RANGE);
        float hRange = FloatArgumentType.getFloat(context, HORIZONTAL_RANGE);
        EffectManager.get().spawnPlayerFirework(player, amount, interval, vRange, hRange);
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.add_player_firework", player.getName()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeClearFireworks(CommandContext<CommandSourceStack> context) {
        EffectManager.get().clearFirework();
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.clear_firework_tracker"), true);
        return Command.SINGLE_SUCCESS;
    }
}