package xiao.battleroyale.command.sub.api;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameInfoGetter;
import xiao.battleroyale.util.NBTUtils;

import static xiao.battleroyale.command.CommandArg.*;

public class GameManagerCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(GAME_MANAGER)
                // IGameInfoGetter
                .then(Commands.literal(GET_GAME_TIME).executes(GameManagerCommand::getGameTime))
                .then(Commands.literal(IS_IN_GAME).executes(GameManagerCommand::isInGame))
                .then(Commands.literal(GET_GLOBAL_CENTER_OFFSET)
                        .then(Commands.argument(NAMESPACE, StringArgumentType.string())
                                .executes(GameManagerCommand::getGlobalCenterOffset)
                        )
                )
                .then(Commands.literal(GET_MAX_GAME_TIME).executes(GameManagerCommand::getMaxGameTime))
                .then(Commands.literal(GET_WINNER_TEAM_TOTAL).executes(GameManagerCommand::getWinnerTeamTotal))
                .then(Commands.literal(GET_REQUIRED_GAME_TEAM).executes(GameManagerCommand::getRequiredGameTeam))
                .then(Commands.literal(HAS_WINNER).executes(GameManagerCommand::hasWinner))
                .then(Commands.literal(GET_REMAIN_RESTART_TIME).executes(GameManagerCommand::getRemainRestartTime));
    }

    // --------IGameInfoGetter--------

    private static int getGameTime(CommandContext<CommandSourceStack> context) {
        IGameInfoGetter gameManager = BattleRoyale.getGameManager();
        return gameManager.getGameTime();
    }
    private static int isInGame(CommandContext<CommandSourceStack> context) {
        IGameInfoGetter gameManager = BattleRoyale.getGameManager();
        return gameManager.isInGame() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int getGlobalCenterOffset(CommandContext<CommandSourceStack> context) {
        IGameInfoGetter gameManager = BattleRoyale.getGameManager();
        ResourceLocation nameSpace = BattleRoyale.getMcRegistry().createResourceLocation(StringArgumentType.getString(context, NAMESPACE));
        CompoundTag posTag = NBTUtils.buildVec3Nbt(gameManager.getGlobalCenterOffset());
        context.getSource().getServer().getCommandStorage().set(nameSpace, posTag);
        return Command.SINGLE_SUCCESS;
    }
    private static int getMaxGameTime(CommandContext<CommandSourceStack> context) {
        IGameInfoGetter gameManager = BattleRoyale.getGameManager();
        return gameManager.getMaxGameTime();
    }
    private static int getWinnerTeamTotal(CommandContext<CommandSourceStack> context) {
        IGameInfoGetter gameManager = BattleRoyale.getGameManager();
        return gameManager.getWinnerTeamTotal();
    }
    private static int getRequiredGameTeam(CommandContext<CommandSourceStack> context) {
        IGameInfoGetter gameManager = BattleRoyale.getGameManager();
        return gameManager.getRequiredGameTeam();
    }
    private static int hasWinner(CommandContext<CommandSourceStack> context) {
        IGameInfoGetter gameManager = BattleRoyale.getGameManager();
        return gameManager.hasWinner() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int getRemainRestartTime(CommandContext<CommandSourceStack> context) {
        IGameInfoGetter gameManager = BattleRoyale.getGameManager();
        return gameManager.getRemainRestartTime();
    }
}