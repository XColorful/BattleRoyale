package xiao.battleroyale.developer.gm.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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

import static xiao.battleroyale.developer.gm.command.CommandArg.*;

public class GameManager {
    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> gmCommand, boolean useFullName) {
        // 解除游戏玩家
        // /battleroyale gamemaster delete gameplayer [id / entity]
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? GAME_PLAYER : GAME_PLAYER_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .executes(GameManager::executeDeleteGamePlayer))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .executes(GameManager::executeDeleteGamePlayerByEntity))));
        // 淘汰游戏玩家
        // /battleroyale gamemaster forceeliminate gameplayer [id / entity]
        gmCommand.then(Commands.literal(useFullName ? FORCE_ELIMINATE : FORCE_ELIMINATE_SHORT)
                .then(Commands.literal(useFullName ? GAME_PLAYER : GAME_PLAYER_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .executes(GameManager::executeForceEliminateGamePlayer))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .executes(GameManager::executeForceEliminateGamePlayerByEntity))));
        // 解散游戏队伍
        // /battleroyale gamemaster delete gameteam [id]
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? GAME_TEAM : GAME_TEAM_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .executes(GameManager::executeDeleteGameTeam))));
        // 淘汰游戏队伍
        // /battleroyale gamemaster forceeliminate gameteam [id / entity]
        gmCommand.then(Commands.literal(useFullName ? FORCE_ELIMINATE : FORCE_ELIMINATE_SHORT)
                .then(Commands.literal(useFullName ? GAME_TEAM : GAME_TEAM_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .executes(GameManager::executeForceEliminateGameTeam))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .executes(GameManager::executeForceEliminateGameTeamByEntity))));
        // 结束游戏区域
        // /battleroyale gamemaster delete gamezone [id]
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? GAME_ZONE : GAME_ZONE_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .executes(GameManager::executeDeleteGameZone))));
        // 添加人机生物
        // /battleroyale gamemaster add bot [entity]
        gmCommand.then(Commands.literal(useFullName ? ADD : ADD_SHORT)
                .then(Commands.literal(useFullName ? BOT : BOT_SHORT)
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .executes(GameManager::executeAddBot))));
        // 更改人机生物
        // /battleroyale gamemaster change bot [id / entity] [entity]
        gmCommand.then(Commands.literal(useFullName ? CHANGE : CHANGE_SHORT)
                .then(Commands.literal(useFullName ? BOT : BOT_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .then(Commands.argument(ENTITY2, EntityArgument.entity())
                                        .executes(GameManager::executeChangeBot)))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .then(Commands.argument(ENTITY2, EntityArgument.entity())
                                        .executes(GameManager::executeChangeBotByEntity)))));
        // 更改最后位置
        // /battleroyale gamemaster change lastpos [id / entity] [xyz]
        gmCommand.then(Commands.literal(useFullName ? CHANGE : CHANGE_SHORT)
                .then(Commands.literal(useFullName ? LAST_POS : LAST_POS_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                        .executes(context -> executeChangeLastPos(context, IntegerArgumentType.getInteger(context, SINGLE_ID), Vec3Argument.getVec3(context, XYZ)))))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                        .executes(context -> executeChangeLastPosByEntity(context, EntityArgument.getEntity(context, ENTITY), Vec3Argument.getVec3(context, XYZ)))))));
        // 更改离线时长
        // /battleroyale gamemaster change invalidtime [id / entity] [amount]
        gmCommand.then(Commands.literal(useFullName ? CHANGE : CHANGE_SHORT)
                .then(Commands.literal(useFullName ? INVALID_TIME : INVALID_TIME_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                        .executes(GameManager::executeChangeInvalidTime)))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                        .executes(GameManager::executeChangeInvalidTimeByEntity)))));
        // 更改最后血量
        // /battleroyale gamemaster change lasthealth [id / entity] [amount]
        gmCommand.then(Commands.literal(useFullName ? CHANGE : CHANGE_SHORT)
                .then(Commands.literal(useFullName ? LAST_HEALTH : LAST_HEALTH_SHORT)
                        .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                        .executes(GameManager::executeChangeLastHealth)))
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                        .executes(GameManager::executeChangeLastHealthByEntity)))));
        // 更改队伍队长
        // /battleroyale gamemaster change teamleader [id] [id / entity]
        gmCommand.then(Commands.literal(useFullName ? CHANGE : CHANGE_SHORT)
                .then(Commands.literal(useFullName ? TEAM_LEADER : TEAM_LEADER_SHORT)
                        .then(Commands.argument(TEAM_ID, IntegerArgumentType.integer())
                                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                                        .executes(GameManager::executeChangeTeamLeader)))
                        .then(Commands.argument(TEAM_ID, IntegerArgumentType.integer())
                                .then(Commands.argument(ENTITY, EntityArgument.entity())
                                        .executes(GameManager::executeChangeTeamLeaderByPlayerEntity)))));
    }

    /**
     * 删除GamePlayer
     */
    private static int executeDeleteGamePlayer(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing delete gameplayer by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeDeleteGamePlayerByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing delete gameplayer by Entity: " + entity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 强制淘汰GamePlayer
     */
    private static int executeForceEliminateGamePlayer(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing force eliminate gameplayer by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeForceEliminateGamePlayerByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing force eliminate gameplayer by Entity: " + entity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 强制解散GameTeam
     */
    private static int executeDeleteGameTeam(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing delete gameteam by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 强制淘汰GameTeam
     */
    private static int executeForceEliminateGameTeam(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing force eliminate gameteam by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeForceEliminateGameTeamByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing force eliminate gameteam by Entity: " + entity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 强制结束GameZone
     */
    private static int executeDeleteGameZone(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing delete gamezone by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 添加人机GamePlayer
     */
    private static int executeAddBot(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing add bot from entity: " + entity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 更改人机GamePlayer
     */
    private static int executeChangeBot(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        Entity targetEntity = EntityArgument.getEntity(context, ENTITY2);
        context.getSource().sendSuccess(() -> Component.literal("Executing change bot with ID " + id + " to entity: " + targetEntity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeChangeBotByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        Entity targetEntity = EntityArgument.getEntity(context, ENTITY2);
        context.getSource().sendSuccess(() -> Component.literal("Executing change bot with entity " + entity.getName().getString() + " to entity: " + targetEntity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 更改GamePlayer.lastPos
     */
    private static int executeChangeLastPos(CommandContext<CommandSourceStack> context, int id, Vec3 pos) {
        context.getSource().sendSuccess(() -> Component.literal("Executing change lastpos for ID " + id + " to: " + pos.toString()), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeChangeLastPosByEntity(CommandContext<CommandSourceStack> context, Entity entity, Vec3 pos) {
        context.getSource().sendSuccess(() -> Component.literal("Executing change lastpos for entity " + entity.getName().getString() + " to: " + pos.toString()), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 更改GamePlayer.invalidTime
     */
    private static int executeChangeInvalidTime(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing change invalidtime for ID " + id + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeChangeInvalidTimeByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing change invalidtime for entity " + entity.getName().getString() + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 更改GamePlayer.lastHealth
     */
    private static int executeChangeLastHealth(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing change lasthealth for ID " + id + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeChangeLastHealthByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing change lasthealth for entity " + entity.getName().getString() + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 强制更改GameTeam队长
     */
    private static int executeChangeTeamLeader(CommandContext<CommandSourceStack> context) {
        final int teamId = IntegerArgumentType.getInteger(context, TEAM_ID);
        final int playerId = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing change teamleader for team ID " + teamId + " to player ID: " + playerId), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeChangeTeamLeaderByPlayerEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final int teamId = IntegerArgumentType.getInteger(context, TEAM_ID);
        Entity playerEntity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing change teamleader for team ID " + teamId + " to player entity: " + playerEntity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }
}