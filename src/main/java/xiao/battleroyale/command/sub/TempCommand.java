package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiao.battleroyale.common.game.tempdata.TempDataManager;

import static xiao.battleroyale.command.CommandArg.*;
import static xiao.battleroyale.api.game.tempdata.TempDataTag.*;

public class TempCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(TEMP)
                .then(Commands.literal(PUBGMC)
                        .then(Commands.argument(BOOL, BoolArgumentType.bool())
                                .executes(TempCommand::turnPubgmcCompatibility)
                        )
                )
                .requires(source -> source.hasPermission(3))
                .then(Commands.literal(CLEAR)
                                .executes(TempCommand::clearAllTempData)
                );
    }

    private static int turnPubgmcCompatibility(CommandContext<CommandSourceStack> context) {
        boolean turn = BoolArgumentType.getBool(context, BOOL);
        TempDataManager.get().writeBool(REGISTRY, PUBGMC_COMMAND, turn);
        TempDataManager.get().saveTempData();
        if (turn) {
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.enable_pubgmc_registry"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.disable_pubgmc_registry"), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int clearAllTempData(CommandContext<CommandSourceStack> context) {
        TempDataManager.get().clearTempData();
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.clear_temp_data"), false);
        return Command.SINGLE_SUCCESS;
    }
}
