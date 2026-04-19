package xiao.battleroyale.command.sub.api;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.CommandStorage;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.*;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.util.NBTUtils;

import static xiao.battleroyale.command.CommandArg.*;

public class GameManagerCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(GAME_MANAGER)
                // IGameInfoGetter
                .then(Commands.literal(GET_GAME_TIME).executes(GameManagerCommand::getGameTime))
                .then(Commands.literal(IS_IN_GAME).executes(GameManagerCommand::isInGame))
                .then(Commands.literal(GET_GLOBAL_CENTER_OFFSET)
                        .then(Commands.argument(RESOURCE_LOCATION, ResourceLocationArgument.id())
                                .then(Commands.argument(STORAGE_PATH, NbtPathArgument.nbtPath())
                                        .executes(GameManagerCommand::getGlobalCenterOffset)
                                )
                        )
                )
                .then(Commands.literal(GET_MAX_GAME_TIME).executes(GameManagerCommand::getMaxGameTime))
                .then(Commands.literal(GET_WINNER_TEAM_TOTAL).executes(GameManagerCommand::getWinnerTeamTotal))
                .then(Commands.literal(GET_REQUIRED_GAME_TEAM).executes(GameManagerCommand::getRequiredGameTeam))
                .then(Commands.literal(HAS_WINNER).executes(GameManagerCommand::hasWinner))
                .then(Commands.literal(GET_REMAIN_RESTART_TIME).executes(GameManagerCommand::getRemainRestartTime))
                // IGameFunc
                .then(Commands.literal(SEND_GAME_SPECTATE_MESSAGE)
                        .then(Commands.argument(PLAYER, EntityArgument.player())
                                .executes(GameManagerCommand::sendGameSpectateMessage))
                )
                .then(Commands.literal(FINISH_GAME)
                        .then(Commands.argument(HAS_WINNER, BoolArgumentType.bool())
                                .executes(GameManagerCommand::finishGame)))
                .then(Commands.literal(ADD_GAME_TIME_AND_TICK).executes(GameManagerCommand::addGameTimeAndTick))
                // IGameStatusSetter
                .then(Commands.literal(ADD_FINISH_CHECK_AFTER_DEATH_EVENT).executes(GameManagerCommand::addFinishCheckAfterDeathEvent))
                .then(Commands.literal(SET_HAS_WINNER)
                        .then(Commands.argument(HAS_WINNER, BoolArgumentType.bool())
                                .executes(GameManagerCommand::setHasWinner)
                        )
                )
                .then(Commands.literal(CLEAR_WINNER_GAME_PLAYERS).executes(GameManagerCommand::clearWinnerGamePlayers))
                .then(Commands.literal(CLEAR_WINNER_GAME_TEAMS).executes(GameManagerCommand::clearWinnerGameTeams))
                .then(Commands.literal(ADD_WINNER_GAME_PLAYER)
                        .then(Commands.literal(BY_PLAYER)
                                .then(Commands.argument(PLAYER, EntityArgument.entity())
                                        .then(Commands.argument(WITH_MEMBERS, BoolArgumentType.bool())
                                                .then(Commands.argument(WITH_TEAM, BoolArgumentType.bool())
                                                        .executes(GameManagerCommand::addWinnerGamePlayerByPlayer)
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal(BY_ID)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .then(Commands.argument(WITH_MEMBERS, BoolArgumentType.bool())
                                                .then(Commands.argument(WITH_TEAM, BoolArgumentType.bool())
                                                        .executes(GameManagerCommand::addWinnerGamePlayerById)
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal(ADD_WINNER_GAME_TEAM)
                        .then(Commands.literal(BY_PLAYER)
                                .then(Commands.argument(PLAYER, EntityArgument.entity())
                                        .then(Commands.argument(WITH_MEMBERS, BoolArgumentType.bool())
                                                .executes(GameManagerCommand::addWinnerGameTeamByPlayer)
                                        )
                                )
                        )
                        .then(Commands.literal(BY_ID)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .then(Commands.argument(WITH_MEMBERS, BoolArgumentType.bool())
                                                .executes(GameManagerCommand::addWinnerGameTeamById)
                                        )
                                )
                        )
                )
                .then(Commands.literal(SET_REMAIN_RESTART_TIME)
                        .then(Commands.argument(TIME, IntegerArgumentType.integer(0))
                                .executes(GameManagerCommand::setRemainRestartTime)
                        )
                )
                // IGameConfigGetter
                .then(Commands.literal(GET_GAMERULE_CONFIG_ID).executes(GameManagerCommand::getGameruleConfigId))
                .then(Commands.literal(GET_SPAWN_CONFIG_ID).executes(GameManagerCommand::getSpawnConfigId))
                .then(Commands.literal(GET_STATS_CONFIG_ID).executes(GameManagerCommand::getStatsConfigId))
                .then(Commands.literal(GET_BOT_CONFIG_ID).executes(GameManagerCommand::getBotConfigId))
                // IGameSaveTeleporter
                .then(Commands.literal(SAVE_TELEPORT)
                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                .then(Commands.argument(POS, Vec3Argument.vec3())
                                        .executes(GameManagerCommand::safeTeleport)
                                        .then(Commands.argument(ROTATION, RotationArgument.rotation())
                                                .executes(GameManagerCommand::safeTeleportFull)
                                        )
                                )
                        )
                );
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
    private static int getGlobalCenterOffset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ResourceLocation storageId = ResourceLocationArgument.getId(context, RESOURCE_LOCATION);
        CommandStorage storage = context.getSource().getServer().getCommandStorage();
        CompoundTag compoundtag = storage.get(storageId);
        NbtPathArgument.NbtPath path = NbtPathArgument.getPath(context, STORAGE_PATH);
        path.getOrCreate(compoundtag, CompoundTag::new);
        path.set(compoundtag, NBTUtils.buildVec3Nbt(BattleRoyale.getGameManager().getGlobalCenterOffset()));
        storage.set(storageId, compoundtag);
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

    // --------IGameFunc--------

    private static int sendGameSpectateMessage(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        IGameFunc gameManager = BattleRoyale.getGameManager();
        ServerPlayer player = EntityArgument.getPlayer(context, PLAYER);
        gameManager.sendGameSpectateMessage(player);
        return Command.SINGLE_SUCCESS;
    }
    private static int finishGame(CommandContext<CommandSourceStack> context) {
        IGameFunc gameManager = BattleRoyale.getGameManager();
        boolean hasWinner = BoolArgumentType.getBool(context, HAS_WINNER);
        gameManager.finishGame(hasWinner);
        return Command.SINGLE_SUCCESS;
    }
    private static int addGameTimeAndTick(CommandContext<CommandSourceStack> context) {
        IGameFunc gameManager = BattleRoyale.getGameManager();
        gameManager.addGameTimeAndTick();
        return Command.SINGLE_SUCCESS;
    }

    // --------IGameStatusSetter--------

    private static int addFinishCheckAfterDeathEvent(CommandContext<CommandSourceStack> context) {
        IGameStatusSetter gameManager = BattleRoyale.getGameManager();
        return gameManager.addFinishCheckAfterDeathEvent() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int setHasWinner(CommandContext<CommandSourceStack> context) {
        IGameStatusSetter gameManager = BattleRoyale.getGameManager();
        boolean hasWinner = BoolArgumentType.getBool(context, HAS_WINNER);
        return gameManager.setHasWinner(hasWinner) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int clearWinnerGamePlayers(CommandContext<CommandSourceStack> context) {
        IGameStatusSetter gameManager = BattleRoyale.getGameManager();
        return gameManager.clearWinnerGamePlayers() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int clearWinnerGameTeams(CommandContext<CommandSourceStack> context) {
        IGameStatusSetter gameManager = BattleRoyale.getGameManager();
        return gameManager.clearWinnerGameTeams() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int addWinnerGamePlayerByPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(context, PLAYER);
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) return 0;
        boolean withMembers = BoolArgumentType.getBool(context, WITH_MEMBERS);
        boolean withTeam = BoolArgumentType.getBool(context, WITH_TEAM);
        return addWinnerGamePlayerByGamePlayer(gameManager, gamePlayer, withMembers, withTeam);
    }
    private static int addWinnerGamePlayerById(CommandContext<CommandSourceStack> context) {
        int playerId = IntegerArgumentType.getInteger(context, ID);
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerBySingleId(playerId);
        if (gamePlayer == null) return 0;
        boolean withMembers = BoolArgumentType.getBool(context, WITH_MEMBERS);
        boolean withTeam = BoolArgumentType.getBool(context, WITH_TEAM);
        return addWinnerGamePlayerByGamePlayer(gameManager, gamePlayer, withMembers, withTeam);
    }
    private static int addWinnerGamePlayerByGamePlayer(IGameStatusSetter gameManager, GamePlayer gamePlayer, boolean withMembers, boolean withTeam) {
        int cnt = 0;
        if (gameManager.addWinnerGamePlayer(gamePlayer)) cnt++;
        GameTeam gameTeam = gamePlayer.getTeam();
        if (withTeam) {
            gameManager.addWinnerGameTeam(gameTeam);
        }
        if (withMembers) {
            for (GamePlayer member : gameTeam.getTeamMembers()) {
                if (member.getGameSingleId() == gamePlayer.getGameSingleId()) {
                    continue;
                }
                if (gameManager.addWinnerGamePlayer(member)) {
                    cnt++;
                }
            }
        }
        return cnt;
    }
    private static int addWinnerGameTeamByPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(context, PLAYER);
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) return 0;
        boolean withMembers = BoolArgumentType.getBool(context, WITH_MEMBERS);
        return addWinnerGameTeamByGameTeam(gameManager, gamePlayer.getTeam(), withMembers);
    }
    private static int addWinnerGameTeamById(CommandContext<CommandSourceStack> context) {
        int teamId = IntegerArgumentType.getInteger(context, ID);
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable GameTeam gameTeam = gameManager.getTeamManager().getGameTeamById(teamId);
        if (gameTeam == null) return 0;
        boolean withMembers = BoolArgumentType.getBool(context, WITH_MEMBERS);
        return addWinnerGameTeamByGameTeam(gameManager, gameTeam, withMembers);
    }
    private static int addWinnerGameTeamByGameTeam(IGameStatusSetter gameManager, GameTeam gameTeam, boolean withMembers) {
        int cnt = 0;
        if (gameManager.addWinnerGameTeam(gameTeam)) cnt++;
        if (withMembers) {
            for (GamePlayer member : gameTeam.getTeamMembers()) {
                if (gameManager.addWinnerGamePlayer(member)) {
                    cnt++;
                }
            }
        }
        return cnt;
    }
    private static int setRemainRestartTime(CommandContext<CommandSourceStack> context) {
        IGameStatusSetter gameManager = BattleRoyale.getGameManager();
        int remainTime = IntegerArgumentType.getInteger(context, TIME);
        return gameManager.setRemainRestartTime(remainTime) ? Command.SINGLE_SUCCESS : 0;
    }

    // --------IGameConfigGetter--------

    private static int getGameruleConfigId(CommandContext<CommandSourceStack> context) {
        IGameConfigGetter gameManager = BattleRoyale.getGameManager();
        return gameManager.getGameruleConfigId();
    }
    private static int getSpawnConfigId(CommandContext<CommandSourceStack> context) {
        IGameConfigGetter gameManager = BattleRoyale.getGameManager();
        return gameManager.getSpawnConfigId();
    }
    private static int getStatsConfigId(CommandContext<CommandSourceStack> context) {
        IGameConfigGetter gameManager = BattleRoyale.getGameManager();
        return gameManager.getStatsConfigId();
    }
    private static int getBotConfigId(CommandContext<CommandSourceStack> context) {
        IGameConfigGetter gameManager = BattleRoyale.getGameManager();
        return gameManager.getBotConfigId();
    }

    // --------IGameSaveTeleport--------

    private static int safeTeleport(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        if (!(entity instanceof LivingEntity livingEntity)) return 0;
        IGameManager gameManager = BattleRoyale.getGameManager();
        gameManager.safeTeleport(livingEntity,
                Vec3Argument.getVec3(context, POS)
        );
        return Command.SINGLE_SUCCESS;
    }
    private static int safeTeleportFull(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        if (!(entity instanceof LivingEntity livingEntity)) return 0;
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null) return -1;
        Vec2 rotation = RotationArgument.getRotation(context, ROTATION)
                .getRotation(context.getSource());
        gameManager.safeTeleport(livingEntity,
                serverLevel,
                Vec3Argument.getVec3(context, POS),
                rotation.x,
                rotation.y
        );
        return Command.SINGLE_SUCCESS;
    }
}