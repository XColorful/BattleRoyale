package xiao.battleroyale.command.sub.api;

import com.google.common.base.Function;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.CommandStorage;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameMainManager;
import xiao.battleroyale.api.game.team.IGameTeamReadApi;
import xiao.battleroyale.api.game.team.ITeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import java.util.List;

import static xiao.battleroyale.command.CommandArg.*;

public class TeamManagerCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(TEAM_MANAGER)
                // ITeamManager
                .then(Commands.literal(SHOULD_AUTO_JOIN).executes(TeamManagerCommand::shouldAutoJoin))
                .then(Commands.literal(FIND_NOT_FULL_TEAM_ID).executes(TeamManagerCommand::findNotFullTeamId))
                .then(Commands.literal(HAS_ENOUGH_PLAYER_TEAM_TO_START).executes(TeamManagerCommand::hasEnoughPlayerTeamToStart))
                // IGameTeamReadApi
                .then(Commands.literal(GET_PLAYER_LIMIT).executes(TeamManagerCommand::getPlayerLimit))
                .then(Commands.literal(GET_GAME_PLAYER_ID)
                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                .executes(TeamManagerCommand::getGamePlayerId)
                        )
                )
                .then(Commands.literal(GET_GAME_PLAYER)
                        .then(Commands.argument(RESOURCE_LOCATION, IdentifierArgument.id())
                                .then(Commands.argument(STORAGE_PATH, NbtPathArgument.nbtPath())
                                        .then(Commands.argument(DETAIL_LEVEL, IntegerArgumentType.integer(0))
                                                .suggests(DETAIL_LEVEL_SUGGESTS)
                                                .then(Commands.literal(BY_PLAYER)
                                                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                                                .executes(TeamManagerCommand::getGamePlayerByPlayer)
                                                        )
                                                )
                                                .then(Commands.literal(BY_ID)
                                                        .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                                                .executes(TeamManagerCommand::getGamePlayerByGamePlayerId)
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal(HAS_STANDING_GAMEPLAYER)
                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                .executes(TeamManagerCommand::hasStandingGamePlayer)
                        )
                )
                .then(Commands.literal(ONLY_REMAIN_BOT_TEAM).executes(TeamManagerCommand::onlyRemainBotTeam))
                .then(Commands.literal(GET_GAME_TEAM_ID)
                        .then(Commands.literal(BY_PLAYER)
                                .then(Commands.argument(PLAYER, EntityArgument.entity())
                                        .executes(TeamManagerCommand::getGameTeamIdByPlayer)
                                )
                        )
                        .then(Commands.literal(BY_ID)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(TeamManagerCommand::getGameTeamIdByGamePlayerId)
                                )
                        )
                )
                .then(Commands.literal(GET_GAME_TEAM)
                        .then(Commands.argument(RESOURCE_LOCATION, IdentifierArgument.id())
                                .then(Commands.argument(STORAGE_PATH, NbtPathArgument.nbtPath())
                                        .then(Commands.argument(DETAIL_LEVEL, IntegerArgumentType.integer(0))
                                                .suggests(DETAIL_LEVEL_SUGGESTS)
                                                .then(Commands.literal(BY_PLAYER)
                                                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                                                .executes(TeamManagerCommand::getGameTeamByPlayer)
                                                        )
                                                )
                                                .then(Commands.literal(BY_ID)
                                                        .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                                                .executes(TeamManagerCommand::getGameTeamByGameTeamId)
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal(GET_GAME_PLAYERS_TOTAL).executes(TeamManagerCommand::getGamePlayersTotal))
                .then(Commands.literal(GET_GAME_PLAYERS)
                        .then(Commands.argument(RESOURCE_LOCATION, IdentifierArgument.id())
                                .then(Commands.argument(STORAGE_PATH, NbtPathArgument.nbtPath())
                                        .then(Commands.argument(DETAIL_LEVEL, IntegerArgumentType.integer(0))
                                                .suggests(DETAIL_LEVEL_SUGGESTS)
                                                .executes(TeamManagerCommand::getGamePlayers)
                                        )
                                )
                        )
                )
                .then(Commands.literal(GET_GAME_TEAMS_TOTAL).executes(TeamManagerCommand::getGameTeamsTotal))
                .then(Commands.literal(GET_GAME_TEAMS)
                        .then(Commands.argument(RESOURCE_LOCATION, IdentifierArgument.id())
                                .then(Commands.argument(STORAGE_PATH, NbtPathArgument.nbtPath())
                                        .then(Commands.argument(DETAIL_LEVEL, IntegerArgumentType.integer(0))
                                                .suggests(DETAIL_LEVEL_SUGGESTS)
                                                .executes(TeamManagerCommand::getGameTeams)
                                        )
                                )
                        )
                )
                .then(Commands.literal(GET_STANDING_GAME_PLAYERS_TOTAL).executes(TeamManagerCommand::getStandingGamePlayersTotal))
                .then(Commands.literal(GET_STANDING_GAME_PLAYERS)
                        .then(Commands.argument(RESOURCE_LOCATION, IdentifierArgument.id())
                                .then(Commands.argument(STORAGE_PATH, NbtPathArgument.nbtPath())
                                        .then(Commands.argument(DETAIL_LEVEL, IntegerArgumentType.integer(0))
                                                .suggests(DETAIL_LEVEL_SUGGESTS)
                                                .executes(TeamManagerCommand::getStandingGamePlayers)
                                        )
                                )
                        )
                )
                .then(Commands.literal(GET_STANDING_GAME_TEAMS_TOTAL).executes(TeamManagerCommand::getStandingGameTeamsTotal))
                .then(Commands.literal(GET_STANDING_GAME_TEAMS)
                        .then(Commands.argument(RESOURCE_LOCATION, IdentifierArgument.id())
                                .then(Commands.argument(STORAGE_PATH, NbtPathArgument.nbtPath())
                                        .then(Commands.argument(DETAIL_LEVEL, IntegerArgumentType.integer(0))
                                                .suggests(DETAIL_LEVEL_SUGGESTS)
                                                .executes(TeamManagerCommand::getStandingGameTeams)
                                        )
                                )
                        )
                )
                .then(Commands.literal(GET_RANDOM_STANDING_GAME_PLAYER_ID).executes(TeamManagerCommand::getRandomStandingGamePlayerId))
                .then(Commands.literal(GET_NON_BOT_TEAM_COUNT).executes(TeamManagerCommand::getNonBotTeamCount))
                .then(Commands.literal(GET_STANDING_PLAYER_TEAM_COUNT).executes(TeamManagerCommand::getStandingPlayerTeamCount))
                // ITeamManagement
                .then(Commands.literal(FORCE_ELIMINATE_PLAYER_SILENCE)
                        .then(Commands.literal(BY_PLAYER)
                                .then(Commands.argument(PLAYER, EntityArgument.entity())
                                        .executes(TeamManagerCommand::forceEliminatePlayerSilenceByPlayer)
                                )
                        )
                        .then(Commands.literal(BY_ID)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(TeamManagerCommand::forceEliminatePlayerSilenceByGamePlayerId)
                                )
                        )
                )
                .then(Commands.literal(FORCE_ELIMINATE_PLAYER_FROM_TEAM)
                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                .executes(TeamManagerCommand::forceEliminatePlayerFromTeam)
                        )
                )
                // ITeamPreManagement
                .then(Commands.literal(FORCE_JOIN_TEAM)
                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                .executes(TeamManagerCommand::forceJoinTeam)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(TeamManagerCommand::forceJoinSpecificTeam)
                                )
                        )
                )
                .then(Commands.literal(REMOVE_PLAYER_FROM_TEAM)
                        .then(Commands.literal(BY_PLAYER)
                                .then(Commands.argument(PLAYER, EntityArgument.entity())
                                        .executes(TeamManagerCommand::removePlayerFromTeamByPlayer)
                                )
                        )
                        .then(Commands.literal(BY_ID)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(TeamManagerCommand::removePlayerFromTeamByGamePlayerId)
                                )
                        )
                )
                // ITeamNotification
                .then(Commands.literal(SEND_PLAYER_TEAM_ID)
                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                .executes(TeamManagerCommand::sendPlayerTeamId)
                        )
                )
                // IVanillaTeam
                .then(Commands.literal(CLEAR_VANILLA_TEAM).executes(TeamManagerCommand::clearVanillaTeam));
    }

    private static final SuggestionProvider<CommandSourceStack> DETAIL_LEVEL_SUGGESTS = (context, builder) ->
            SharedSuggestionProvider.suggest(new String[]{
                    "0", // basic
                    "1", // simple
                    "2", // game
                    "3" // full
            }, builder);

    // --------ITeamManager--------

    private static int shouldAutoJoin(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getTeamManager().shouldAutoJoin() ? Command.SINGLE_SUCCESS : 0;
    }

    private static int findNotFullTeamId(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getTeamManager().findNotFullTeamId();
    }

    private static int hasEnoughPlayerTeamToStart(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getTeamManager().hasEnoughPlayerTeamToStart() ? Command.SINGLE_SUCCESS : 0;
    }

    // --------IGameTeamReadApi--------

    private static int getPlayerLimit(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getTeamManager().getPlayerLimit();
    }
    private static int getGamePlayerId(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(context, PLAYER);
        @Nullable GamePlayer gamePlayer = BattleRoyale.getGameManager().getTeamManager().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) return 0;
        return gamePlayer.getGameSingleId();
    }
    private static int getGamePlayerByPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(context, PLAYER);
        @Nullable GamePlayer gamePlayer = BattleRoyale.getGameManager().getTeamManager().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) return 0;
        return getGamePlayerByGamePlayer(context, gamePlayer,
                IdentifierArgument.getId(context, RESOURCE_LOCATION),
                IntegerArgumentType.getInteger(context, DETAIL_LEVEL));
    }
    private static int getGamePlayerByGamePlayerId(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int playerId = IntegerArgumentType.getInteger(context, ID);
        @Nullable GamePlayer gamePlayer = BattleRoyale.getGameManager().getTeamManager().getGamePlayerBySingleId(playerId);
        if (gamePlayer == null) return 0;
        return getGamePlayerByGamePlayer(context, gamePlayer,
                IdentifierArgument.getId(context, RESOURCE_LOCATION),
                IntegerArgumentType.getInteger(context, DETAIL_LEVEL));
    }
    private static int getGamePlayerByGamePlayer(CommandContext<CommandSourceStack> context, GamePlayer gamePlayer, Identifier storageId, int detailLevel) throws CommandSyntaxException {
        CompoundTag gamePlayerTag = detailLevel <= 0
                ? gamePlayer.toBasicTag()
                : switch (detailLevel) {
            case 1 -> gamePlayer.toSimpleTag();
            case 2 -> gamePlayer.toGameTag();
            default -> gamePlayer.toFullTag();
        };
        CommandStorage storage = context.getSource().getServer().getCommandStorage();
        CompoundTag compoundtag = storage.get(storageId);
        NbtPathArgument.NbtPath path = NbtPathArgument.getPath(context, STORAGE_PATH);
        path.getOrCreate(compoundtag, CompoundTag::new);
        path.set(compoundtag, gamePlayerTag);
        storage.set(storageId, compoundtag);
        return Command.SINGLE_SUCCESS;
    }
    private static int hasStandingGamePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        IGameTeamReadApi teamManager = BattleRoyale.getGameManager().getTeamManager();
        return teamManager.hasStandingGamePlayer(entity.getUUID()) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int onlyRemainBotTeam(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getTeamManager().onlyRemainBotTeam() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int getGameTeamIdByPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(context, PLAYER);
        @Nullable GamePlayer gamePlayer = BattleRoyale.getGameManager().getTeamManager().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) return 0;
        return gamePlayer.getGameTeamId();
    }
    private static int getGameTeamIdByGamePlayerId(CommandContext<CommandSourceStack> context) {
        int playerId = IntegerArgumentType.getInteger(context, ID);
        @Nullable GamePlayer gamePlayer = BattleRoyale.getGameManager().getTeamManager().getGamePlayerBySingleId(playerId);
        if (gamePlayer == null) return 0;
        return gamePlayer.getGameTeamId();
    }
    private static int getGameTeamByPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(context, PLAYER);
        @Nullable GamePlayer gamePlayer = BattleRoyale.getGameManager().getTeamManager().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) return 0;
        return getGameTeamByGameTeam(context, gamePlayer.getTeam(),
                IdentifierArgument.getId(context, RESOURCE_LOCATION),
                IntegerArgumentType.getInteger(context, DETAIL_LEVEL));
    }
    private static int getGameTeamByGameTeamId(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int teamId = IntegerArgumentType.getInteger(context, ID);
        @Nullable GameTeam gameTeam = BattleRoyale.getGameManager().getTeamManager().getGameTeamById(teamId);
        if (gameTeam == null) return 0;
        return getGameTeamByGameTeam(context, gameTeam,
                IdentifierArgument.getId(context, RESOURCE_LOCATION),
                IntegerArgumentType.getInteger(context, DETAIL_LEVEL));
    }
    private static int getGameTeamByGameTeam(CommandContext<CommandSourceStack> context, GameTeam gameTeam, Identifier storageId, int detailLevel) throws CommandSyntaxException {
        CompoundTag gameTeamTag = detailLevel <= 0
                ? gameTeam.toBasicTag()
                : switch (detailLevel) {
            case 1 -> gameTeam.toSimpleTag();
            case 2 -> gameTeam.toGameTag();
            default -> gameTeam.toFullTag();
        };
        CommandStorage storage = context.getSource().getServer().getCommandStorage();
        CompoundTag compoundtag = storage.get(storageId);
        NbtPathArgument.NbtPath path = NbtPathArgument.getPath(context, STORAGE_PATH);
        path.getOrCreate(compoundtag, CompoundTag::new);
        path.set(compoundtag, gameTeamTag);
        storage.set(storageId, compoundtag);
        return Command.SINGLE_SUCCESS;
    }
    private static int getGamePlayersTotal(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getTeamManager().getGamePlayersTotal();
    }
    private static int getGamePlayers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return getGamePlayersInternal(context, BattleRoyale.getGameManager().getTeamManager().getGamePlayers(),
                IdentifierArgument.getId(context, RESOURCE_LOCATION),
                IntegerArgumentType.getInteger(context, DETAIL_LEVEL)
        );
    }
    private static int getGameTeamsTotal(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getTeamManager().getGameTeams().size();
    }
    private static int getGameTeams(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return getGameTeamsInternal(context, BattleRoyale.getGameManager().getTeamManager().getGameTeams(),
                IdentifierArgument.getId(context, RESOURCE_LOCATION),
                IntegerArgumentType.getInteger(context, DETAIL_LEVEL)
        );
    }
    private static int getStandingGamePlayersTotal(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getTeamManager().getStandingGamePlayerSize();
    }
    private static int getStandingGamePlayers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return getGamePlayersInternal(context, BattleRoyale.getGameManager().getTeamManager().getStandingGamePlayers(),
                IdentifierArgument.getId(context, RESOURCE_LOCATION),
                IntegerArgumentType.getInteger(context, DETAIL_LEVEL)
        );
    }
    private static int getStandingGameTeamsTotal(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getTeamManager().getStandingGameTeams().size();
    }
    private static int getStandingGameTeams(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return getGameTeamsInternal(context, BattleRoyale.getGameManager().getTeamManager().getStandingGameTeams(),
                IdentifierArgument.getId(context, RESOURCE_LOCATION),
                IntegerArgumentType.getInteger(context, DETAIL_LEVEL)
        );
    }
    private static int getRandomStandingGamePlayerId(CommandContext<CommandSourceStack> context) {
        GamePlayer player = BattleRoyale.getGameManager().getTeamManager().getRandomStandingGamePlayer();
        return player != null ? player.getGameSingleId() : 0;
    }
    private static int getNonBotTeamCount(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getTeamManager().getNonBotTeamCount();
    }
    private static int getStandingPlayerTeamCount(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getTeamManager().getStandingPlayerTeamCount();
    }

    private static int getGamePlayersInternal(CommandContext<CommandSourceStack> context, List<GamePlayer> players, Identifier storageId, int detailLevel) throws CommandSyntaxException {
        Function<GamePlayer, CompoundTag> toTag = detailLevel <= 0 ? GamePlayer::toBasicTag
                : switch (detailLevel) {
            case 1 -> GamePlayer::toSimpleTag;
            case 2 -> GamePlayer::toGameTag;
            default -> GamePlayer::toFullTag;
        };

        int[] playerIds = new int[players.size()];
        ListTag playersTag = new ListTag();
        for (int i = 0; i < players.size(); i++) {
            GamePlayer player = players.get(i);
            playerIds[i] = player.getGameSingleId();
            playersTag.add(toTag.apply(player));
        }

        CompoundTag tag = new CompoundTag();
        tag.put("playerIds", new IntArrayTag(playerIds));
        tag.put("players", playersTag);

        CommandStorage storage = context.getSource().getServer().getCommandStorage();
        CompoundTag compoundtag = storage.get(storageId);
        NbtPathArgument.NbtPath path = NbtPathArgument.getPath(context, STORAGE_PATH);
        path.getOrCreate(compoundtag, CompoundTag::new);
        path.set(compoundtag, tag);
        storage.set(storageId, compoundtag);
        return Command.SINGLE_SUCCESS;
    }

    private static int getGameTeamsInternal(CommandContext<CommandSourceStack> context, List<GameTeam> teams, Identifier storageId, int detailLevel) throws CommandSyntaxException {
        Function<GameTeam, CompoundTag> toTag = detailLevel <= 0 ? GameTeam::toBasicTag
                : switch (detailLevel) {
            case 1 -> GameTeam::toSimpleTag;
            case 2 -> GameTeam::toGameTag;
            default -> GameTeam::toFullTag;
        };

        int[] teamIds = new int[teams.size()];
        ListTag teamsTag = new ListTag();
        for (int i = 0; i < teams.size(); i++) {
            GameTeam team = teams.get(i);
            teamIds[i] = team.getGameTeamId();
            teamsTag.add(toTag.apply(team));
        }

        CompoundTag tag = new CompoundTag();
        tag.put("teamIds", new IntArrayTag(teamIds));
        tag.put("teams", teamsTag);

        CommandStorage storage = context.getSource().getServer().getCommandStorage();
        CompoundTag compoundtag = storage.get(storageId);
        NbtPathArgument.NbtPath path = NbtPathArgument.getPath(context, STORAGE_PATH);
        path.getOrCreate(compoundtag, CompoundTag::new);
        path.set(compoundtag, tag);
        storage.set(storageId, compoundtag);
        return Command.SINGLE_SUCCESS;
    }

    // --------ITeamManagement--------

    private static int forceEliminatePlayerSilenceByPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        @Nullable GamePlayer gamePlayer = BattleRoyale.getGameManager().getTeamManager().getGamePlayerByUUID(entity.getUUID());
        if (gamePlayer == null) return 0;
        return BattleRoyale.getGameManager().getTeamManager().forceEliminatePlayerSilence(gamePlayer) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int forceEliminatePlayerSilenceByGamePlayerId(CommandContext<CommandSourceStack> context) {
        int playerId = IntegerArgumentType.getInteger(context, ID);
        @Nullable GamePlayer gamePlayer = BattleRoyale.getGameManager().getTeamManager().getGamePlayerBySingleId(playerId);
        if (gamePlayer == null) return 0;
        return BattleRoyale.getGameManager().getTeamManager().forceEliminatePlayerSilence(gamePlayer) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int forceEliminatePlayerFromTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        if (!(entity instanceof LivingEntity livingEntity)) return 0;
        @Nullable GamePlayer gamePlayer = BattleRoyale.getGameManager().getTeamManager().getGamePlayerByUUID(livingEntity.getUUID());
        if (gamePlayer == null) return 0;
        BattleRoyale.getGameManager().getTeamManager().forceEliminatePlayerFromTeam(livingEntity);
        return Command.SINGLE_SUCCESS;
    }

    // --------ITeamPreManagement--------

    private static int forceJoinTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ITeamManager teamManager = BattleRoyale.getGameManager().getTeamManager();
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        if (!(entity instanceof LivingEntity player)) return 0;
        teamManager.forceJoinTeam(player);
        return teamManager.getGamePlayerByUUID(player.getUUID()) != null ? Command.SINGLE_SUCCESS : 0;
    }
    private static int forceJoinSpecificTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ITeamManager teamManager = BattleRoyale.getGameManager().getTeamManager();
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        if (!(entity instanceof LivingEntity player)) return 0;
        teamManager.forceJoinTeam(player, IntegerArgumentType.getInteger(context, ID));
        return teamManager.getGamePlayerByUUID(player.getUUID()) != null ? Command.SINGLE_SUCCESS : 0;
    }
    private static int removePlayerFromTeamByPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        return BattleRoyale.getGameManager().getTeamManager().removePlayerFromTeam(entity.getUUID()) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int removePlayerFromTeamByGamePlayerId(CommandContext<CommandSourceStack> context) {
        int playerId = IntegerArgumentType.getInteger(context, ID);
        ITeamManager teamManager = BattleRoyale.getGameManager().getTeamManager();
        @Nullable GamePlayer gamePlayer = teamManager.getGamePlayerBySingleId(playerId);
        if (gamePlayer == null) return 0;
        return teamManager.removePlayerFromTeam(gamePlayer.getPlayerUUID()) ? Command.SINGLE_SUCCESS : 0;
    }

    // --------ITeamNotification--------

    private static int sendPlayerTeamId(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        if (entity instanceof ServerPlayer serverPlayer) {
            BattleRoyale.getGameManager().getTeamManager().sendPlayerTeamId(serverPlayer);
        }
        @Nullable GamePlayer gamePlayer = BattleRoyale.getGameManager().getTeamManager().getGamePlayerByUUID(entity.getUUID());
        return gamePlayer != null ? Command.SINGLE_SUCCESS : 0;
    }

    // --------IVanillaTeam--------

    private static int clearVanillaTeam(CommandContext<CommandSourceStack> context) {
        IGameMainManager gameManager = BattleRoyale.getGameManager();
        gameManager.getTeamManager().clearVanillaTeam(gameManager.getServerLevel());
        return Command.SINGLE_SUCCESS;
    }
}
