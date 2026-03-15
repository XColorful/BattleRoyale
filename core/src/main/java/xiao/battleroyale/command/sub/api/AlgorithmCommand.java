package xiao.battleroyale.command.sub.api;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.algorithm.ICircleGrid;
import xiao.battleroyale.api.algorithm.IGoldenSpiral;
import xiao.battleroyale.api.algorithm.IRectangleGrid;
import xiao.battleroyale.util.ListUtils;
import xiao.battleroyale.util.Vec3Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static xiao.battleroyale.command.CommandArg.*;

public class AlgorithmCommand {

    public static final int MAX_DISTRIBUTED_COUNT = 10000;
    public static @NotNull List<Vec3> LAST_RECTANGLE_GRID = new ArrayList<>();
    public static @NotNull List<Vec3> LAST_GOLDEN_SPIRAL = new ArrayList<>();
    public static @NotNull List<Vec3> LAST_CIRCLE_GRID = new ArrayList<>();

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(ALGORITHM)
                .then(getDistributionNode(RECTANGLE_GRID,
                        AlgorithmCommand::rectangleGridShuffle,
                        AlgorithmCommand::rectangleGridBound,
                        AlgorithmCommand::rectangleGridRandomRange,
                        AlgorithmCommand::rectangleGrid,
                        AlgorithmCommand::rectangleGridAtExecute))
                .then(getDistributionNode(GOLDEN_SPIRAL,
                        AlgorithmCommand::goldenSpiralShuffle,
                        AlgorithmCommand::goldenSpiralBound,
                        AlgorithmCommand::goldenSpiralRandomRange,
                        AlgorithmCommand::goldenSpiral,
                        AlgorithmCommand::goldenSpiralAtExecute))
                .then(getDistributionNode(CIRCLE_GRID,
                        AlgorithmCommand::circleGridShuffle,
                        AlgorithmCommand::circleGridBound,
                        AlgorithmCommand::circleGridRandomRange,
                        AlgorithmCommand::circleGrid,
                        AlgorithmCommand::circleGridAtExecute));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> getDistributionNode(
            String name,
            Command<CommandSourceStack> shuffle,
            Command<CommandSourceStack> bound,
            Command<CommandSourceStack> randomRange,
            Command<CommandSourceStack> distribute,
            Command<CommandSourceStack> distributeAt) {

        return Commands.literal(name)
                .then(Commands.literal(SHUFFLE).executes(shuffle))
                .then(Commands.literal(BOUND)
                        .then(Commands.argument(MIN_POINT, IntegerArgumentType.integer(1))
                                .then(Commands.argument(MAX_POINT, IntegerArgumentType.integer(MAX_DISTRIBUTED_COUNT))
                                        .executes(bound)
                                )
                        )
                )
                .then(Commands.literal(RANDOM_RANGE)
                        .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                .then(Commands.argument(RANGE_TYPE, StringArgumentType.word())
                                        .suggests(RANGE_TYPE_SUGGESTS)
                                        .executes(randomRange)
                                )
                        )
                )
                .then(Commands.argument(POS, Vec3Argument.vec3())
                        .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                .then(Commands.argument(COUNT, IntegerArgumentType.integer(1, MAX_DISTRIBUTED_COUNT))
                                        .then(Commands.argument(ALLOW_ON_BORDER, BoolArgumentType.bool())
                                                .then(Commands.argument(GLOBAL_SHRINK_RATIO, DoubleArgumentType.doubleArg())
                                                        .executes(distribute)
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                        .then(Commands.argument(COUNT, IntegerArgumentType.integer(1, MAX_DISTRIBUTED_COUNT))
                                .then(Commands.argument(ALLOW_ON_BORDER, BoolArgumentType.bool())
                                        .then(Commands.argument(GLOBAL_SHRINK_RATIO, DoubleArgumentType.doubleArg())
                                                .executes(distributeAt)
                                        )
                                )
                        )
                );
    }

    private static final SuggestionProvider<CommandSourceStack> RANGE_TYPE_SUGGESTS = (context, builder) ->
            SharedSuggestionProvider.suggest(new String[]{
                    RANDOM_ADJUST_XYZ,
                    RANDOM_ADJUST_XZ_EXPAND_Y,
                    SCALE_XYZ,
                    RANDOM_CIRCLE_XZ_EXPAND_Y,
                    RANDOM_SPHERE_XYZ
            }, builder);

    private static void applyRandomRange(List<Vec3> vec3List, Vec3 xyz, String rangeType) {
        switch (rangeType) {
            case RANDOM_ADJUST_XYZ -> vec3List.replaceAll(vec3 -> Vec3Utils.randomAdjustXYZ(vec3, xyz, BattleRoyale.COMMON_RANDOM::nextFloat));
            case RANDOM_ADJUST_XZ_EXPAND_Y -> vec3List.replaceAll(vec3 -> Vec3Utils.randomAdjustXZExpandY(vec3, xyz, BattleRoyale.COMMON_RANDOM::nextFloat));
            case SCALE_XYZ -> vec3List.replaceAll(vec3 -> Vec3Utils.scaleXYZ(vec3, xyz.x, xyz.y, xyz.z));
            case RANDOM_CIRCLE_XZ_EXPAND_Y -> vec3List.replaceAll(vec3 -> Vec3Utils.randomCircleXZExpandY(vec3, xyz, BattleRoyale.COMMON_RANDOM::nextFloat));
            case RANDOM_SPHERE_XYZ -> vec3List.replaceAll(vec3 -> Vec3Utils.randomSphereXYZ(vec3, xyz, BattleRoyale.COMMON_RANDOM::nextFloat));
        }
    }

    // --------IRectangleGrid--------

    private static int rectangleGridShuffle(CommandContext<CommandSourceStack> context) {
        Collections.shuffle(LAST_RECTANGLE_GRID, BattleRoyale.COMMON_RANDOM);
        return LAST_RECTANGLE_GRID.size();
    }
    private static int rectangleGridBound(CommandContext<CommandSourceStack> context) {
        int minIndex = IntegerArgumentType.getInteger(context, MIN_POINT) - 1;
        int maxIndex = IntegerArgumentType.getInteger(context, MAX_POINT) + 1;
        LAST_RECTANGLE_GRID = ListUtils.getSubListSafely(LAST_RECTANGLE_GRID, minIndex, maxIndex);
        return LAST_RECTANGLE_GRID.size();
    }
    private static int rectangleGridRandomRange(CommandContext<CommandSourceStack> context) {
        if (LAST_RECTANGLE_GRID.isEmpty()) return -1;
        applyRandomRange(LAST_RECTANGLE_GRID, Vec3Argument.getVec3(context, XYZ), StringArgumentType.getString(context, RANGE_TYPE));
        return LAST_RECTANGLE_GRID.size();
    }
    private static int rectangleGrid(CommandContext<CommandSourceStack> context) {
        IRectangleGrid rectangleGrid = BattleRoyale.getAlgorithmApi().rectangleGrid();
        LAST_RECTANGLE_GRID = rectangleGrid.distributed(Vec3Argument.getVec3(context, POS),
                Vec3Argument.getVec3(context, XYZ),
                IntegerArgumentType.getInteger(context, COUNT),
                BoolArgumentType.getBool(context, ALLOW_ON_BORDER),
                DoubleArgumentType.getDouble(context, GLOBAL_SHRINK_RATIO)
        );
        return LAST_RECTANGLE_GRID.size();
    }
    private static int rectangleGridAtExecute(CommandContext<CommandSourceStack> context) {
        IRectangleGrid rectangleGrid = BattleRoyale.getAlgorithmApi().rectangleGrid();
        LAST_RECTANGLE_GRID = rectangleGrid.distributed(context.getSource().getPosition(),
                Vec3Argument.getVec3(context, XYZ),
                IntegerArgumentType.getInteger(context, COUNT),
                BoolArgumentType.getBool(context, ALLOW_ON_BORDER),
                DoubleArgumentType.getDouble(context, GLOBAL_SHRINK_RATIO)
        );
        return LAST_RECTANGLE_GRID.size();
    }

    // --------IGoldenSpiral--------

    private static int goldenSpiralShuffle(CommandContext<CommandSourceStack> context) {
        Collections.shuffle(LAST_GOLDEN_SPIRAL, BattleRoyale.COMMON_RANDOM);
        return LAST_GOLDEN_SPIRAL.size();
    }
    private static int goldenSpiralBound(CommandContext<CommandSourceStack> context) {
        int minIndex = IntegerArgumentType.getInteger(context, MIN_POINT) - 1;
        int maxIndex = IntegerArgumentType.getInteger(context, MAX_POINT) + 1;
        LAST_GOLDEN_SPIRAL = ListUtils.getSubListSafely(LAST_GOLDEN_SPIRAL, minIndex, maxIndex);
        return LAST_GOLDEN_SPIRAL.size();
    }
    private static int goldenSpiralRandomRange(CommandContext<CommandSourceStack> context) {
        if (LAST_GOLDEN_SPIRAL.isEmpty()) return -1;
        applyRandomRange(LAST_GOLDEN_SPIRAL, Vec3Argument.getVec3(context, XYZ), StringArgumentType.getString(context, RANGE_TYPE));
        return LAST_GOLDEN_SPIRAL.size();
    }
    private static int goldenSpiral(CommandContext<CommandSourceStack> context) {
        IGoldenSpiral algorithm = BattleRoyale.getAlgorithmApi().goldenSpiral();
        LAST_GOLDEN_SPIRAL = algorithm.distributed(Vec3Argument.getVec3(context, POS),
                Vec3Argument.getVec3(context, XYZ),
                IntegerArgumentType.getInteger(context, COUNT),
                BoolArgumentType.getBool(context, ALLOW_ON_BORDER),
                DoubleArgumentType.getDouble(context, GLOBAL_SHRINK_RATIO)
        );
        return LAST_GOLDEN_SPIRAL.size();
    }
    private static int goldenSpiralAtExecute(CommandContext<CommandSourceStack> context) {
        IGoldenSpiral algorithm = BattleRoyale.getAlgorithmApi().goldenSpiral();
        LAST_GOLDEN_SPIRAL = algorithm.distributed(context.getSource().getPosition(),
                Vec3Argument.getVec3(context, XYZ),
                IntegerArgumentType.getInteger(context, COUNT),
                BoolArgumentType.getBool(context, ALLOW_ON_BORDER),
                DoubleArgumentType.getDouble(context, GLOBAL_SHRINK_RATIO)
        );
        return LAST_GOLDEN_SPIRAL.size();
    }

    // --------ICircleGrid--------

    private static int circleGridShuffle(CommandContext<CommandSourceStack> context) {
        Collections.shuffle(LAST_CIRCLE_GRID, BattleRoyale.COMMON_RANDOM);
        return LAST_CIRCLE_GRID.size();
    }
    private static int circleGridBound(CommandContext<CommandSourceStack> context) {
        int minIndex = IntegerArgumentType.getInteger(context, MIN_POINT) - 1;
        int maxIndex = IntegerArgumentType.getInteger(context, MAX_POINT) + 1;
        LAST_CIRCLE_GRID = ListUtils.getSubListSafely(LAST_CIRCLE_GRID, minIndex, maxIndex);
        return LAST_CIRCLE_GRID.size();
    }
    private static int circleGridRandomRange(CommandContext<CommandSourceStack> context) {
        if (LAST_CIRCLE_GRID.isEmpty()) return -1;
        applyRandomRange(LAST_CIRCLE_GRID, Vec3Argument.getVec3(context, XYZ), StringArgumentType.getString(context, RANGE_TYPE));
        return LAST_CIRCLE_GRID.size();
    }
    private static int circleGrid(CommandContext<CommandSourceStack> context) {
        ICircleGrid algorithm = BattleRoyale.getAlgorithmApi().circleGrid();
        LAST_CIRCLE_GRID = algorithm.distributed(Vec3Argument.getVec3(context, POS),
                Vec3Argument.getVec3(context, XYZ),
                IntegerArgumentType.getInteger(context, COUNT),
                BoolArgumentType.getBool(context, ALLOW_ON_BORDER),
                DoubleArgumentType.getDouble(context, GLOBAL_SHRINK_RATIO)
        );
        return LAST_CIRCLE_GRID.size();
    }
    private static int circleGridAtExecute(CommandContext<CommandSourceStack> context) {
        ICircleGrid algorithm = BattleRoyale.getAlgorithmApi().circleGrid();
        LAST_CIRCLE_GRID = algorithm.distributed(context.getSource().getPosition(),
                Vec3Argument.getVec3(context, XYZ),
                IntegerArgumentType.getInteger(context, COUNT),
                BoolArgumentType.getBool(context, ALLOW_ON_BORDER),
                DoubleArgumentType.getDouble(context, GLOBAL_SHRINK_RATIO)
        );
        return LAST_CIRCLE_GRID.size();
    }
}
