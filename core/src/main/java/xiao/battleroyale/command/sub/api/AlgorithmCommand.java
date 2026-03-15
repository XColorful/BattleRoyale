package xiao.battleroyale.command.sub.api;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.algorithm.ICircleGrid;
import xiao.battleroyale.api.algorithm.IGoldenSpiral;
import xiao.battleroyale.api.algorithm.IRectangleGrid;
import xiao.battleroyale.util.ListUtils;

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
                // IAlgorithmApi
                .then(Commands.literal(RECTANGLE_GRID)
                        // IRectangleGrid
                        .then(Commands.literal(SHUFFLE).executes(AlgorithmCommand::rectangleGridShuffle))
                        .then(Commands.literal(BOUND)
                                .then(Commands.argument(MIN_POINT, IntegerArgumentType.integer(1))
                                        .then(Commands.argument(MAX_POINT, IntegerArgumentType.integer(MAX_DISTRIBUTED_COUNT))
                                                .executes(AlgorithmCommand::rectangleGridBound)
                                        )
                                )
                        )
                        .then(Commands.argument(POS, Vec3Argument.vec3())
                                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                        .then(Commands.argument(COUNT, IntegerArgumentType.integer(1, MAX_DISTRIBUTED_COUNT))
                                                .then(Commands.argument(ALLOW_ON_BORDER, BoolArgumentType.bool())
                                                        .then(Commands.argument(GLOBAL_SHRINK_RATIO, DoubleArgumentType.doubleArg())
                                                                .executes(AlgorithmCommand::rectangleGrid)
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                .then(Commands.argument(COUNT, IntegerArgumentType.integer(1, MAX_DISTRIBUTED_COUNT))
                                        .then(Commands.argument(ALLOW_ON_BORDER, BoolArgumentType.bool())
                                                .then(Commands.argument(GLOBAL_SHRINK_RATIO, DoubleArgumentType.doubleArg())
                                                        .executes(AlgorithmCommand::rectangleGridAtExecute)
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal(GOLDEN_SPIRAL)
                        // IGoldenSpiral
                        .then(Commands.literal(SHUFFLE).executes(AlgorithmCommand::goldenSpiralShuffle))
                        .then(Commands.literal(BOUND)
                                .then(Commands.argument(MIN_POINT, IntegerArgumentType.integer(1))
                                        .then(Commands.argument(MAX_POINT, IntegerArgumentType.integer(MAX_DISTRIBUTED_COUNT))
                                                .executes(AlgorithmCommand::goldenSpiralBound)
                                        )
                                )
                        )
                        .then(Commands.argument(POS, Vec3Argument.vec3())
                                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                        .then(Commands.argument(COUNT, IntegerArgumentType.integer(1, MAX_DISTRIBUTED_COUNT))
                                                .then(Commands.argument(ALLOW_ON_BORDER, BoolArgumentType.bool())
                                                        .then(Commands.argument(GLOBAL_SHRINK_RATIO, DoubleArgumentType.doubleArg())
                                                                .executes(AlgorithmCommand::goldenSpiral)
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                .then(Commands.argument(COUNT, IntegerArgumentType.integer(1, MAX_DISTRIBUTED_COUNT))
                                        .then(Commands.argument(ALLOW_ON_BORDER, BoolArgumentType.bool())
                                                .then(Commands.argument(GLOBAL_SHRINK_RATIO, DoubleArgumentType.doubleArg())
                                                        .executes(AlgorithmCommand::goldenSpiralAtExecute)
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal(CIRCLE_GRID)
                        // ICircleGrid
                        .then(Commands.literal(SHUFFLE).executes(AlgorithmCommand::circleGridShuffle))
                        .then(Commands.literal(BOUND)
                                .then(Commands.argument(MIN_POINT, IntegerArgumentType.integer(1))
                                        .then(Commands.argument(MAX_POINT, IntegerArgumentType.integer(MAX_DISTRIBUTED_COUNT))
                                                .executes(AlgorithmCommand::circleGridBound)
                                        )
                                )
                        )
                        .then(Commands.argument(POS, Vec3Argument.vec3())
                                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                        .then(Commands.argument(COUNT, IntegerArgumentType.integer(1, MAX_DISTRIBUTED_COUNT))
                                                .then(Commands.argument(ALLOW_ON_BORDER, BoolArgumentType.bool())
                                                        .then(Commands.argument(GLOBAL_SHRINK_RATIO, DoubleArgumentType.doubleArg())
                                                                .executes(AlgorithmCommand::circleGrid)
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                .then(Commands.argument(COUNT, IntegerArgumentType.integer(1, MAX_DISTRIBUTED_COUNT))
                                        .then(Commands.argument(ALLOW_ON_BORDER, BoolArgumentType.bool())
                                                .then(Commands.argument(GLOBAL_SHRINK_RATIO, DoubleArgumentType.doubleArg())
                                                        .executes(AlgorithmCommand::circleGridAtExecute)
                                                )
                                        )
                                )
                        )
                );
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
