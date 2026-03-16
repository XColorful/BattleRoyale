package xiao.battleroyale.command.sub.api;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameMainManager;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.team.GamePlayer;

import static xiao.battleroyale.command.CommandArg.*;

public class ZoneManagerCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(ZONE_MANAGER)
                // IZoneManager
                .then(Commands.literal(HAS_ENOUGH_ZONE_TO_START).executes(ZoneManagerCommand::hasEnoughZoneToStart))
                .then(Commands.literal(RANDOMIZE_ZONE_TICK_OFFSET).executes(ZoneManagerCommand::randomizeZoneTickOffset))
                // IGameZoneReadApi
                .then(Commands.literal(GET_GAME_ZONE)
                        .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                .executes(ZoneManagerCommand::getGameZone)
                                // IGameZone
                                .then(Commands.literal(GET_ZONE_DELAY).executes(ZoneManagerCommand::getZoneDelay))
                                .then(Commands.literal(IS_CREATED).executes(ZoneManagerCommand::isCreated))
                                .then(Commands.literal(IS_PRESENT).executes(ZoneManagerCommand::isPresent))
                                .then(Commands.literal(IS_FINISHED).executes(ZoneManagerCommand::isFinished))
                                .then(Commands.literal(TICKABLE_ZONE)
                                        // ITickableZone
                                        .then(Commands.literal(IS_READY).executes(ZoneManagerCommand::isReady))
                                        .then(Commands.literal(GET_TICK_FREQUENCY).executes(ZoneManagerCommand::getTickFrequency))
                                        .then(Commands.literal(SET_TICK_FREQUENCY)
                                                .then(Commands.argument(TICK_FREQ, IntegerArgumentType.integer(1))
                                                        .executes(ZoneManagerCommand::setTickFrequency)
                                                )
                                        )
                                        .then(Commands.literal(GET_TICK_OFFSET).executes(ZoneManagerCommand::getTickOffset))
                                        .then(Commands.literal(SET_TICK_OFFSET)
                                                .then(Commands.argument(TICK_OFFSET, IntegerArgumentType.integer(0))
                                                        .executes(ZoneManagerCommand::setTickOffset)))
                                        .then(Commands.literal(PLAYER_FUNC)
                                                .then(Commands.argument(PLAYER, EntityArgument.entity())
                                                        .executes(ZoneManagerCommand::playerFunc)))
                                        .then(Commands.literal(GET_SHAPE_MOVE_DELAY).executes(ZoneManagerCommand::getShapeMoveDelay))
                                        .then(Commands.literal(GET_SHAPE_MOVE_TIME).executes(ZoneManagerCommand::getShapeMoveTime))
                                )
                                .then(Commands.literal(SPATIAL_ZONE)
                                        // ISpatialZone
                                        .then(Commands.literal(IS_WITHIN_ZONE)
                                                .executes(ZoneManagerCommand::isWithinZoneAtExecute)
                                                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                                        .executes(ZoneManagerCommand::isWithinZoneAtPos)
                                                        .then(Commands.argument(PROGRESS, DoubleArgumentType.doubleArg(0, 1))
                                                                .executes(ZoneManagerCommand::isWithinZone)
                                                        )
                                                )
                                        )
                                        .then(Commands.literal(IS_DETERMINED).executes(ZoneManagerCommand::isDetermined))
                                        .then(Commands.literal(HAS_BAD_SHAPE).executes(ZoneManagerCommand::hasBadShape))
                                        .then(Commands.literal(GET_SEGMENTS).executes(ZoneManagerCommand::getSegments))
                                )
                        )
                );
    }

    // --------IZoneManager--------

    private static int hasEnoughZoneToStart(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getZoneManager().hasEnoughZoneToStart() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int randomizeZoneTickOffset(CommandContext<CommandSourceStack> context) {
        BattleRoyale.getGameManager().getZoneManager().randomizeZoneTickOffset();
        return Command.SINGLE_SUCCESS;
    }

    // --------IGameZoneReadApi--------

    // IGameZone
    private static @Nullable IGameZone getGameZone(int zoneId) {
        return BattleRoyale.getGameManager().getZoneManager().getGameZone(zoneId);
    }
    private static int getZoneDelay(CommandContext<CommandSourceStack> context) {
        @Nullable IGameZone gameZone = getGameZone(IntegerArgumentType.getInteger(context, ID));
        if (gameZone == null) return -1;
        return gameZone.getZoneDelay();
    }
    private static int getGameZone(CommandContext<CommandSourceStack> context) {
        @Nullable IGameZone gameZone = getGameZone(IntegerArgumentType.getInteger(context, ID));
        return gameZone != null ? Command.SINGLE_SUCCESS : 0;
    }
    private static int isCreated(CommandContext<CommandSourceStack> context) {
        @Nullable IGameZone gameZone = getGameZone(IntegerArgumentType.getInteger(context, ID));
        if (gameZone == null) return -1;
        return gameZone.isCreated() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int isPresent(CommandContext<CommandSourceStack> context) {
        @Nullable IGameZone gameZone = getGameZone(IntegerArgumentType.getInteger(context, ID));
        if (gameZone == null) return -1;
        return gameZone.isPresent() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int isFinished(CommandContext<CommandSourceStack> context) {
        @Nullable IGameZone gameZone = getGameZone(IntegerArgumentType.getInteger(context, ID));
        if (gameZone == null) return -1;
        return gameZone.isFinished() ? Command.SINGLE_SUCCESS : 0;
    }
    // ITickableZone
    private static @Nullable ITickableZone getTickableZone(int zoneId) {
        return BattleRoyale.getGameManager().getZoneManager().getGameZone(zoneId);
    }
    private static int isReady(CommandContext<CommandSourceStack> context) {
        @Nullable ITickableZone tickableZone = getTickableZone(IntegerArgumentType.getInteger(context, ID));
        if (tickableZone == null) return -1;
        return tickableZone.isReady() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int getTickFrequency(CommandContext<CommandSourceStack> context) {
        @Nullable ITickableZone tickableZone = getTickableZone(IntegerArgumentType.getInteger(context, ID));
        if (tickableZone == null) return -1;
        return tickableZone.getTickFrequency();
    }
    private static int setTickFrequency(CommandContext<CommandSourceStack> context) {
        @Nullable ITickableZone tickableZone = getTickableZone(IntegerArgumentType.getInteger(context, ID));
        if (tickableZone == null) return -1;
        tickableZone.setTickFrequency(IntegerArgumentType.getInteger(context, TICK_FREQ));
        return Command.SINGLE_SUCCESS;
    }
    private static int getTickOffset(CommandContext<CommandSourceStack> context) {
        @Nullable ITickableZone tickableZone = getTickableZone(IntegerArgumentType.getInteger(context, ID));
        if (tickableZone == null) return -1;
        return tickableZone.getTickOffset();
    }
    private static int setTickOffset(CommandContext<CommandSourceStack> context) {
        @Nullable ITickableZone tickableZone = getTickableZone(IntegerArgumentType.getInteger(context, ID));
        if (tickableZone == null) return -1;
        tickableZone.setTickOffset(IntegerArgumentType.getInteger(context, TICK_OFFSET));
        return Command.SINGLE_SUCCESS;
    }
    private static int playerFunc(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        @Nullable ITickableZone tickableZone = getTickableZone(IntegerArgumentType.getInteger(context, ID));
        if (tickableZone == null) return -1;
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        IGameMainManager gameManager = BattleRoyale.getGameManager();
        @Nullable GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerByUUID(entity.getUUID());
        if (gamePlayer == null) return -2;
        @Nullable ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null) return -3;
        tickableZone.playerFunc(gameManager.getServerLevel(), gamePlayer);
        return Command.SINGLE_SUCCESS;
    }
    private static int getShapeMoveDelay(CommandContext<CommandSourceStack> context) {
        @Nullable ITickableZone tickableZone = getTickableZone(IntegerArgumentType.getInteger(context, ID));
        if (tickableZone == null) return -1;
        return tickableZone.getShapeMoveDelay();
    }
    private static int getShapeMoveTime(CommandContext<CommandSourceStack> context) {
        @Nullable ITickableZone tickableZone = getTickableZone(IntegerArgumentType.getInteger(context, ID));
        if (tickableZone == null) return -1;
        return tickableZone.getShapeMoveTime();
    }
    // ISpatialZone
    private static @Nullable ISpatialZone getSpatialZone(int zoneId) {
        return BattleRoyale.getGameManager().getZoneManager().getGameZone(zoneId);
    }
    private static int isWithinZoneAtExecute(CommandContext<CommandSourceStack> context) {
        @Nullable IGameZone gameZone = getGameZone(IntegerArgumentType.getInteger(context, ID));
        if (gameZone == null) return -1;
        return gameZone.isWithinZone(context.getSource().getPosition(), gameZone.getShapeProgress(BattleRoyale.getGameManager().getGameTime()))
                ? Command.SINGLE_SUCCESS : 0;
    }
    private static int isWithinZoneAtPos(CommandContext<CommandSourceStack> context) {
        @Nullable IGameZone gameZone = getGameZone(IntegerArgumentType.getInteger(context, ID));
        if (gameZone == null) return -1;
        return gameZone.isWithinZone(Vec3Argument.getVec3(context, XYZ), gameZone.getShapeProgress(BattleRoyale.getGameManager().getGameTime()))
                ? Command.SINGLE_SUCCESS : 0;
    }
    private static int isWithinZone(CommandContext<CommandSourceStack> context) {
        @Nullable ISpatialZone spatialZone = getSpatialZone(IntegerArgumentType.getInteger(context, ID));
        if (spatialZone == null) return -1;
        return spatialZone.isWithinZone(Vec3Argument.getVec3(context, XYZ), DoubleArgumentType.getDouble(context, PROGRESS))
                ? Command.SINGLE_SUCCESS : 0;
    }
    private static int isDetermined(CommandContext<CommandSourceStack> context) {
        @Nullable ISpatialZone spatialZone = getSpatialZone(IntegerArgumentType.getInteger(context, ID));
        if (spatialZone == null) return -1;
        return spatialZone.isDetermined() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int hasBadShape(CommandContext<CommandSourceStack> context) {
        @Nullable ISpatialZone spatialZone = getSpatialZone(IntegerArgumentType.getInteger(context, ID));
        if (spatialZone == null) return -1;
        return spatialZone.hasBadShape() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int getSegments(CommandContext<CommandSourceStack> context) {
        @Nullable ISpatialZone spatialZone = getSpatialZone(IntegerArgumentType.getInteger(context, ID));
        if (spatialZone == null) return -1;
        return spatialZone.getSegments();
    }
}