package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.compat.tacz.Tacz;
import xiao.battleroyale.data.io.TempDataManager;

import static xiao.battleroyale.api.data.TempDataTag.*;
import static xiao.battleroyale.command.CommandArg.*;
import static xiao.battleroyale.command.CommandPermission.checkCommandLevel;

public class TempCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(TEMP)
                .then(Commands.literal(PUBGMC)
                        .then(Commands.argument(BOOL, BoolArgumentType.bool())
                                .executes(TempCommand::turnPubgmcCompatibility)
                        )
                )
                .then(Commands.literal(INIT_STACK_ZONE_CONFIG)
                        .then(Commands.argument(BOOL, BoolArgumentType.bool())
                                .executes(TempCommand::turnInitStackZoneConfig)))
                .then(Commands.literal(GAME_STEP)
                        .then(Commands.argument(INTERVAL, IntegerArgumentType.integer())
                                .executes(TempCommand::changeGameStep)))
                .then(Commands.literal(TACZ)
                        .then(Commands.literal(BULLET_HANDLER)
                                .then(Commands.argument(BOOL, BoolArgumentType.bool())
                                        .executes(TempCommand::turnTaczBulletHandler)
                                )
                        )
                )
                .then(Commands.literal(CLEAR)
                        .requires(source -> checkCommandLevel(source, 3))
                        .executes(TempCommand::clearAllTempData)
                );
    }

    private static int turnPubgmcCompatibility(CommandContext<CommandSourceStack> context) {
        boolean turn = BoolArgumentType.getBool(context, BOOL);
        TempDataManager tempDataManager = TempDataManager.get();
        tempDataManager.writeBool(REGISTRY, PUBGMC_COMMAND, turn);
        tempDataManager.saveTempData();
        if (turn) {
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.enable_pubgmc_registry"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.disable_pubgmc_registry"), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int turnInitStackZoneConfig(CommandContext<CommandSourceStack> context) {
        boolean turn = BoolArgumentType.getBool(context, BOOL);
        BattleRoyale.getGameManager().getZoneManager().setStackZoneConfig(turn);
        if (turn) {
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.enable_init_stack_zone_config"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.disable_init_stack_zone_config"), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int changeGameStep(CommandContext<CommandSourceStack> context) {
        int gameStep = IntegerArgumentType.getInteger(context, INTERVAL);
        if (BattleRoyale.getGameManager().setGameStep(gameStep)) {
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.set_game_step_success", gameStep), false);
        } else {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.set_game_step_fail", gameStep));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int turnTaczBulletHandler(CommandContext<CommandSourceStack> context) {
        boolean turn = BoolArgumentType.getBool(context, BOOL);
        TempDataManager tempDataManager = TempDataManager.get();
        tempDataManager.writeBool(FEATURE, TACZ_BULLET_HANDLER, turn);
        tempDataManager.saveTempData();
        if (turn) {
            Tacz.registerBulletEvent();
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.enable_tacz_bullet_handler"), false);
        } else {
            Tacz.unregisterBulletEvent();
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.disable_tacz_bullet_handler"), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int clearAllTempData(CommandContext<CommandSourceStack> context) {
        TempDataManager.get().clearTempData();
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.clear_temp_data"), false);
        return Command.SINGLE_SUCCESS;
    }
}
