package xiao.battleroyale.developer.gm.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import static xiao.battleroyale.developer.debug.command.CommandArg.NAME;
import static xiao.battleroyale.developer.gm.command.CommandArg.*;

public class GameManager {
    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> gmCommand, boolean useFullName) {
        // 解除游戏玩家
        // /battleroyale gamemaster delete gameplayer [id / entity]
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? GAME_PLAYER : GAME_PLAYER_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .executes(GameManager::deleteGamePlayer))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .executes(GameManager::deleteGamePlayerByEntity))));
        // 淘汰游戏玩家
        // /battleroyale gamemaster forceeliminate gameplayer [id / entity]
        gmCommand.then(Commands.literal(useFullName ? FORCE_ELIMINATE : FORCE_ELIMINATE_SHORT)
                .then(Commands.literal(useFullName ? GAME_PLAYER : GAME_PLAYER_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .executes(GameManager::forceEliminateGamePlayer))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .executes(GameManager::forceEliminateGamePlayerByEntity))));
        // 解散游戏队伍
        // /battleroyale gamemaster delete gameteam [id]
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? GAME_TEAM : GAME_TEAM_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .executes(GameManager::deleteGameTeam))));
        // 淘汰游戏队伍
        // /battleroyale gamemaster forceeliminate gameteam [id / entity]
        gmCommand.then(Commands.literal(useFullName ? FORCE_ELIMINATE : FORCE_ELIMINATE_SHORT)
                .then(Commands.literal(useFullName ? GAME_TEAM : GAME_TEAM_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .executes(GameManager::forceEliminateGameTeam))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .executes(GameManager::forceEliminateGameTeamByEntity))));
        // 结束游戏区域
        // /battleroyale gamemaster delete gamezone [id]
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? GAME_ZONE : GAME_ZONE_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .executes(GameManager::deleteGameZone))
                        .then(Commands.argument(NAME, StringArgumentType.string())
                                .executes(GameManager::deleteGameZoneByName))));
        // 添加人机生物
        // /battleroyale gamemaster add bot [entity]
        gmCommand.then(Commands.literal(useFullName ? ADD : ADD_SHORT)
                .then(Commands.literal(useFullName ? BOT : BOT_SHORT)
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .executes(GameManager::addBot))));
        // 更改人机生物
        // /battleroyale gamemaster change bot [id / entity] [entity]
        gmCommand.then(Commands.literal(useFullName ? CHANGE : CHANGE_SHORT)
                .then(Commands.literal(useFullName ? BOT : BOT_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .then(Commands.argument(ENTITY2, EntityArgument.entity())
                                        .executes(GameManager::changeBot)))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .then(Commands.argument(ENTITY2, EntityArgument.entity())
                                        .executes(GameManager::changeBotByEntity)))));
        // 更改最后位置
        // /battleroyale gamemaster change lastpos [id / entity] [xyz]
        gmCommand.then(Commands.literal(useFullName ? CHANGE : CHANGE_SHORT)
                .then(Commands.literal(useFullName ? LAST_POS : LAST_POS_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                        .executes(context -> changeLastPos(context, IntegerArgumentType.getInteger(context, SINGLE_ID), Vec3Argument.getVec3(context, XYZ)))))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                        .executes(context -> changeLastPosByEntity(context, EntityArgument.getEntity(context, ENTITY), Vec3Argument.getVec3(context, XYZ)))))));
        // 更改离线时长
        // /battleroyale gamemaster change invalidtime [id / entity] [amount]
        gmCommand.then(Commands.literal(useFullName ? CHANGE : CHANGE_SHORT)
                .then(Commands.literal(useFullName ? INVALID_TIME : INVALID_TIME_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                        .executes(GameManager::changeInvalidTime)))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                        .executes(GameManager::changeInvalidTimeByEntity)))));
        // 更改最后血量
        // /battleroyale gamemaster change lasthealth [id / entity] [amount]
        gmCommand.then(Commands.literal(useFullName ? CHANGE : CHANGE_SHORT)
                .then(Commands.literal(useFullName ? LAST_HEALTH : LAST_HEALTH_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                        .executes(GameManager::changeLastHealth)))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                        .executes(GameManager::changeLastHealthByEntity)))));
        // 更改队伍队长
        // /battleroyale gamemaster change teamleader [id] [id / entity]
        gmCommand.then(Commands.literal(useFullName ? CHANGE : CHANGE_SHORT)
                .then(Commands.literal(useFullName ? TEAM_LEADER : TEAM_LEADER_SHORT)
                        .then(Commands.argument(TEAM_ID, IntegerArgumentType.integer())
                                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                        .executes(GameManager::changeTeamLeader)))
                        .then(Commands.argument(TEAM_ID, IntegerArgumentType.integer())
                                .then(Commands.argument(ENTITY, EntityArgument.entity())
                                        .executes(GameManager::changeTeamLeaderByPlayerEntity)))));
    }

    /**
     * 删除GamePlayer
     */
    private static int deleteGamePlayer(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing delete gameplayer by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int deleteGamePlayerByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing delete gameplayer by Entity: " + entity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 强制淘汰GamePlayer
     */
    private static int forceEliminateGamePlayer(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing force eliminate gameplayer by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int forceEliminateGamePlayerByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing force eliminate gameplayer by Entity: " + entity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 强制解散GameTeam
     */
    private static int deleteGameTeam(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing delete gameteam by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 强制淘汰GameTeam
     */
    private static int forceEliminateGameTeam(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing force eliminate gameteam by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int forceEliminateGameTeamByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing force eliminate gameteam by Entity: " + entity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 强制结束GameZone
     */
    private static int deleteGameZone(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing delete gamezone by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int deleteGameZoneByName(CommandContext<CommandSourceStack> context) {
        final String name = StringArgumentType.getString(context, NAME);
        context.getSource().sendSuccess(() -> Component.literal("Executing delete gamezone by Name: " + name), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 添加人机GamePlayer
     */
    private static int addBot(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing add bot from entity: " + entity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 更改人机GamePlayer
     */
    private static int changeBot(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        Entity targetEntity = EntityArgument.getEntity(context, ENTITY2);
        context.getSource().sendSuccess(() -> Component.literal("Executing change bot with ID " + id + " to entity: " + targetEntity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int changeBotByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        Entity targetEntity = EntityArgument.getEntity(context, ENTITY2);
        context.getSource().sendSuccess(() -> Component.literal("Executing change bot with entity " + entity.getName().getString() + " to entity: " + targetEntity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 更改GamePlayer.lastPos
     */
    private static int changeLastPos(CommandContext<CommandSourceStack> context, int id, Vec3 pos) {
        context.getSource().sendSuccess(() -> Component.literal("Executing change lastpos for ID " + id + " to: " + pos.toString()), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int changeLastPosByEntity(CommandContext<CommandSourceStack> context, Entity entity, Vec3 pos) {
        context.getSource().sendSuccess(() -> Component.literal("Executing change lastpos for entity " + entity.getName().getString() + " to: " + pos.toString()), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 更改GamePlayer.invalidTime
     */
    private static int changeInvalidTime(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing change invalidtime for ID " + id + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int changeInvalidTimeByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing change invalidtime for entity " + entity.getName().getString() + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 更改GamePlayer.lastHealth
     */
    private static int changeLastHealth(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing change lasthealth for ID " + id + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int changeLastHealthByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing change lasthealth for entity " + entity.getName().getString() + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 强制更改GameTeam队长
     */
    private static int changeTeamLeader(CommandContext<CommandSourceStack> context) {
        final int teamId = IntegerArgumentType.getInteger(context, TEAM_ID);
        final int playerId = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing change teamleader for team ID " + teamId + " to player ID: " + playerId), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int changeTeamLeaderByPlayerEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final int teamId = IntegerArgumentType.getInteger(context, TEAM_ID);
        Entity playerEntity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing change teamleader for team ID " + teamId + " to player entity: " + playerEntity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }
}