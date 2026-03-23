package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.data.TempDataTag;
import xiao.battleroyale.api.minecraft.CommandLevel;
import xiao.battleroyale.data.io.TempDataManager;
import xiao.battleroyale.init.CommandSelector;

import static xiao.battleroyale.command.CommandArg.*;

public class TempCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(TEMP)
                .then(Commands.literal(ENTITY_SELECTOR)
                        .then(Commands.argument(BOOL, BoolArgumentType.bool())
                                .executes(TempCommand::turnAllEntitySelector)
                        )
                        .then(Commands.argument(TYPE, StringArgumentType.word())
                                .suggests(TempDataTag.SELECTOR_TYPE_SUGGESTS)
                                .then(Commands.argument(BOOL, BoolArgumentType.bool())
                                        .executes(TempCommand::turnEntitySelector)
                                )
                        )
                )
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
                .then(Commands.literal(CLEAR)
                        .requires(CommandLevel.hasPermission(3))
                        .executes(TempCommand::clearAllTempData)
                );
    }

    private static int turnAllEntitySelector(CommandContext<CommandSourceStack> context) {
        boolean turn = BoolArgumentType.getBool(context, BOOL);
        TempDataManager tempDataManager = TempDataManager.get();
        tempDataManager.writeBool(TempDataTag.REGISTRY, TempDataTag.ENTITY_SELECTOR, turn);
        tempDataManager.saveTempData();
        if (turn) {
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.enable_entity_selector"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.disable_entity_selector"), false);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int turnEntitySelector(CommandContext<CommandSourceStack> context) {
        String selectorType = StringArgumentType.getString(context, TYPE);
        if (!TempDataTag.selectorTypes.contains(selectorType)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_entity_selector_type", selectorType));
            return 0;
        }

        boolean turn = BoolArgumentType.getBool(context, BOOL);
        TempDataManager tempDataManager = TempDataManager.get();
        tempDataManager.writeBool(TempDataTag.REGISTRY, selectorType, turn);
        if (turn) {
            if (CommandSelector.SelectorRegister.register(BattleRoyale.getSelectorRegistry(), selectorType)) {
                // 没启用就先启用总开关
                Boolean all = tempDataManager.getBool(TempDataTag.REGISTRY, TempDataTag.ENTITY_SELECTOR);
                if (all != null && !all) {
                    tempDataManager.writeBool(TempDataTag.REGISTRY, TempDataTag.ENTITY_SELECTOR, true);
                    context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.enable_entity_selector"), false);
                }
                // 启用消息
                context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.enable_entity_selector_type", TempDataTag.getSelectorString(selectorType)), false);
            } else {
                BattleRoyale.LOGGER.warn("Failed to register selector {}", selectorType);
                context.getSource().sendFailure(Component.translatable("battleroyale.message.no_entity_selector_type", selectorType));
                return 0;
            }
        } else {
            // 取消启用消息
            context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.disable_entity_selector_type", TempDataTag.getSelectorString(selectorType)), false);
        }
        tempDataManager.saveTempData();

        return Command.SINGLE_SUCCESS;
    }

    private static int turnPubgmcCompatibility(CommandContext<CommandSourceStack> context) {
        boolean turn = BoolArgumentType.getBool(context, BOOL);
        TempDataManager tempDataManager = TempDataManager.get();
        tempDataManager.writeBool(TempDataTag.REGISTRY, TempDataTag.PUBGMC_COMMAND, turn);
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
    
    private static int clearAllTempData(CommandContext<CommandSourceStack> context) {
        TempDataManager.get().clearTempData();
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.clear_temp_data"), false);
        return Command.SINGLE_SUCCESS;
    }
}
