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

public class GetGame {

    public static void addGame(LiteralArgumentBuilder<CommandSourceStack> getCommand,  boolean useFullName) {

        // 获取游戏玩家
        // get gameplayers [min max / all]
        getCommand.then(Commands.literal(useFullName ? GAME_PLAYERS : GAME_PLAYERS_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> executeGetGamePlayers(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> executeGetGamePlayers(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get gameplayer [id/name/entity]
        getCommand.then(Commands.literal(useFullName ? GAME_PLAYER : GAME_PLAYER_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetGame::executeGetGamePlayer))
                .then(Commands.argument(NAME, StringArgumentType.string())
                        .executes(GetGame::executeGetGamePlayerByName))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .executes(GetGame::executeGetGamePlayerByEntity)));

        // 获取游戏队伍
        // get gameteams [min max / all]
        getCommand.then(Commands.literal(useFullName ? GAME_TEAMS : GAME_TEAMS_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> executeGetGameTeams(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> executeGetGameTeams(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get gameteam [id]
        getCommand.then(Commands.literal(useFullName ? GAME_TEAM : GAME_TEAM_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetGame::executeGetGameTeam)));

        // 获取游戏区域
        // get gamezones [min max / all]
        getCommand.then(Commands.literal(useFullName ? GAME_ZONES : GAME_ZONES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> executeGetGameZones(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> executeGetGameZones(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get gamezone [id / name]
        getCommand.then(Commands.literal(useFullName ? GAME_ZONE : GAME_ZONE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetGame::executeGetGameZone))
                .then(Commands.argument(NAME, StringArgumentType.string())
                        .executes(GetGame::executeGetGameZoneByName)));

        // 获取备份的玩家游戏模式
        // get backupplayermodes [min max / all]
        getCommand.then(Commands.literal(useFullName ? BACKUP_PLAYER_MODES : BACKUP_PLAYER_MODES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> executeGetBackupPlayerModes(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> executeGetBackupPlayerModes(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get backupplayermode [id / name / entity]
        getCommand.then(Commands.literal(useFullName ? BACKUP_PLAYER_MODE : BACKUP_PLAYER_MODE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetGame::executeGetBackupPlayerMode))
                .then(Commands.argument(NAME, StringArgumentType.string())
                        .executes(GetGame::executeGetBackupPlayerModeByName))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .executes(GetGame::executeGetBackupPlayerModeByEntity)));

        // 获取备份的原版规则
        // get backupgamerule
        getCommand.then(Commands.literal(useFullName ? BACKUP_GAMERULE : BACKUP_GAMERULE_SHORT)
                .executes(GetGame::executeGetBackupGamerule));
    }

    /**
     * 获取GamePlayers列表
     */
    private static int executeGetGamePlayers(CommandContext<CommandSourceStack> context, int min, int max) {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get gameplayers (all)"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get gameplayers with min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取GamePlayer详细信息
     */
    public static int executeGetGamePlayer(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing get gameplayer by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    /**
     * 获取GamePlayer详细信息
     */
    public static int executeGetGamePlayerByName(CommandContext<CommandSourceStack> context) {
        final String name = StringArgumentType.getString(context, NAME);
        context.getSource().sendSuccess(() -> Component.literal("Executing get gameplayer by Name: " + name), false);
        return Command.SINGLE_SUCCESS;
    }
    public static int executeGetGamePlayerByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing get gameplayer by Entity: " + entity.getName().getString() + " (UUID: " + entity.getUUID().toString() + ")"), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取GameTeams列表
     */
    private static int executeGetGameTeams(CommandContext<CommandSourceStack> context, int min, int max) {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get gameteams (all)"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get gameteams with min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取GameTeam详细信息
     */
    private static int executeGetGameTeam(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing get gameteam by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取GameZones列表
     */
    private static int executeGetGameZones(CommandContext<CommandSourceStack> context, int min, int max) {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get gamezones (all)"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get gamezones with min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取GameZone详细信息
     */
    private static int executeGetGameZone(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing get gamezone by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetGameZoneByName(CommandContext<CommandSourceStack> context) {
        final String name = StringArgumentType.getString(context, NAME);
        context.getSource().sendSuccess(() -> Component.literal("Executing get gamezone by Name: " + name), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeGetCommonLootManager(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Executing get commonloot"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeGetGameLootManager(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Executing get gameloot"), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取备份的玩家游戏模式
     */
    private static int executeGetBackupPlayerModes(CommandContext<CommandSourceStack> context, int min, int max) {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get backupplayermodes (all)"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get backupplayermodes with min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetBackupPlayerMode(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing get backupplayermode by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetBackupPlayerModeByName(CommandContext<CommandSourceStack> context) {
        final String name = StringArgumentType.getString(context, NAME);
        context.getSource().sendSuccess(() -> Component.literal("Executing get backupplayermode by Name: " + name), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetBackupPlayerModeByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing get backupplayermode by Entity: " + entity.getName().getString() + " (UUID: " + entity.getUUID().toString() + ")"), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取备份的原版gamerule
     */
    private static int executeGetBackupGamerule(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Executing get backupgamerule"), false);
        return Command.SINGLE_SUCCESS;
    }

    public static String getGamePlayerCommandString(int singleId) {
        return buildDebugCommandString(
                GET,
                GAME_PLAYER,
                Integer.toString(singleId)
        );
    }
    public static String getGameTeamCommandString(int teamId) {
        return buildDebugCommandString(
                GET,
                GAME_TEAM,
                Integer.toString(teamId)
        );
    }

    public static String getGameZoneCommandString(int zoneId) {
        return buildDebugCommandString(
                GET,
                GAME_ZONE,
                Integer.toString(zoneId)
        );
    }

    public static String getBackupPlayerModeCommandString(int singleId) {
        return buildDebugCommandString(
                GET,
                BACKUP_PLAYER_MODE,
                Integer.toString(singleId)
        );
    }
}