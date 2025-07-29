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
import xiao.battleroyale.developer.debug.DebugGame;
import xiao.battleroyale.developer.debug.DebugManager;

import static xiao.battleroyale.developer.debug.command.CommandArg.*;
import static xiao.battleroyale.developer.debug.command.sub.GetCommand.buildDebugCommandString;

public class GetGame {

    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> getCommand, boolean useFullName) {

        // 获取游戏玩家
        // get gameplayers [min max / all]
        getCommand.then(Commands.literal(useFullName ? GAME_PLAYERS : GAME_PLAYERS_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> getGamePlayers(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> getGamePlayers(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get gameplayer [id/name/entity]
        getCommand.then(Commands.literal(useFullName ? GAME_PLAYER : GAME_PLAYER_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetGame::getGamePlayer))
                .then(Commands.argument(NAME, StringArgumentType.string())
                        .executes(GetGame::getGamePlayerByName))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .executes(GetGame::getGamePlayerByEntity)));

        // 获取游戏队伍
        // get gameteams [min max / all]
        getCommand.then(Commands.literal(useFullName ? GAME_TEAMS : GAME_TEAMS_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> getGameTeams(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> getGameTeams(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get gameteam [id]
        getCommand.then(Commands.literal(useFullName ? GAME_TEAM : GAME_TEAM_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetGame::getGameTeam)));

        // 获取游戏区域
        // get gamezones [min max / all]
        getCommand.then(Commands.literal(useFullName ? GAME_ZONES : GAME_ZONES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> getGameZones(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> getGameZones(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get gamezone [id / name]
        getCommand.then(Commands.literal(useFullName ? GAME_ZONE : GAME_ZONE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetGame::getGameZone))
                .then(Commands.argument(NAME, StringArgumentType.string())
                        .executes(GetGame::getGameZoneByName)));

        // 获取备份的玩家游戏模式
        // get backupplayermodes [min max / all]
        getCommand.then(Commands.literal(useFullName ? BACKUP_PLAYER_MODES : BACKUP_PLAYER_MODES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> getBackupPlayerModes(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> getBackupPlayerModes(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get backupplayermode [id / name / entity]
        getCommand.then(Commands.literal(useFullName ? BACKUP_PLAYER_MODE : BACKUP_PLAYER_MODE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetGame::getBackupPlayerMode))
                .then(Commands.argument(NAME, StringArgumentType.string())
                        .executes(GetGame::getBackupPlayerModeByName))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .executes(GetGame::getBackupPlayerModeByEntity)));

        // 获取备份的原版规则
        // get backupgamerule
        getCommand.then(Commands.literal(useFullName ? BACKUP_GAMERULE : BACKUP_GAMERULE_SHORT)
                .executes(GetGame::getBackupGamerule));
    }

    /**
     * 获取GamePlayers列表
     */
    private static int getGamePlayers(CommandContext<CommandSourceStack> context, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugGame.get().getGamePlayers(source, min, max);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取GamePlayer详细信息
     */
    public static int getGamePlayer(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugGame.get().getGamePlayer(source, IntegerArgumentType.getInteger(context, SINGLE_ID));
        return Command.SINGLE_SUCCESS;
    }
    public static int getGamePlayerByName(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugGame.get().getGamePlayer(source, StringArgumentType.getString(context, NAME));
        return Command.SINGLE_SUCCESS;
    }
    public static int getGamePlayerByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugGame.get().getGamePlayer(source, EntityArgument.getEntity(context, ENTITY));
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取GameTeams列表
     */
    private static int getGameTeams(CommandContext<CommandSourceStack> context, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugGame.get().getGameTeams(source, min, max);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取GameTeam详细信息
     */
    private static int getGameTeam(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugGame.get().getGameTeam(source, IntegerArgumentType.getInteger(context, SINGLE_ID));
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取GameZones列表
     */
    private static int getGameZones(CommandContext<CommandSourceStack> context, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugGame.get().getGameZones(source, min, max);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取GameZone详细信息
     */
    private static int getGameZone(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugGame.get().getGameZone(source, IntegerArgumentType.getInteger(context, SINGLE_ID));
        return Command.SINGLE_SUCCESS;
    }
    private static int getGameZoneByName(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugGame.get().getGameZone(source, StringArgumentType.getString(context, NAME));
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取备份的玩家游戏模式
     */
    private static int getBackupPlayerModes(CommandContext<CommandSourceStack> context, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get backupplayermodes (all)"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get backupplayermodes with min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int getBackupPlayerMode(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing get backupplayermode by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int getBackupPlayerModeByName(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        final String name = StringArgumentType.getString(context, NAME);
        context.getSource().sendSuccess(() -> Component.literal("Executing get backupplayermode by Name: " + name), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int getBackupPlayerModeByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing get backupplayermode by Entity: " + entity.getName().getString() + " (UUID: " + entity.getUUID().toString() + ")"), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取备份的原版gamerule
     */
    private static int getBackupGamerule(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("Executing get backupgamerule"), false);
        return Command.SINGLE_SUCCESS;
    }

    public static String getGamePlayerCommand(int singleId) {
        return buildDebugCommandString(
                GET,
                GAME_PLAYER,
                Integer.toString(singleId)
        );
    }
    public static String getGameTeamCommand(int teamId) {
        return buildDebugCommandString(
                GET,
                GAME_TEAM,
                Integer.toString(teamId)
        );
    }

    public static String getGameZoneCommand(int zoneId) {
        return buildDebugCommandString(
                GET,
                GAME_ZONE,
                Integer.toString(zoneId)
        );
    }

    public static String getBackupPlayerModeCommand(int singleId) {
        return buildDebugCommandString(
                GET,
                BACKUP_PLAYER_MODE,
                Integer.toString(singleId)
        );
    }
}