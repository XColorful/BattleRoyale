package xiao.battleroyale.developer.debug.command.sub.get;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import static xiao.battleroyale.developer.debug.command.CommandArg.*;

public class GetWorld {

    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> getCommand, boolean useFullName) {

        // get blockentitiesnbt [xyz]
        getCommand.then(Commands.literal(useFullName ? BLOCK_ENTITIES_NBT : BLOCK_ENTITIES_NBT_SHORT)
                .executes(context -> executeGetBlockEntitiesNBT(context, context.getSource().getPosition()))
                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                        .executes(context -> executeGetBlockEntitiesNBT(context, Vec3Argument.getVec3(context, XYZ)))));

        // get blockentitynbt [xyz]
        getCommand.then(Commands.literal(useFullName ? BLOCK_ENTITY_NBT : BLOCK_ENTITY_NBT_SHORT)
                .executes(context -> executeGetBlockEntityNBT(context, context.getSource().getPosition()))
                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                        .executes(context -> executeGetBlockEntityNBT(context, Vec3Argument.getVec3(context, XYZ)))));


        // get biome [xyz]
        getCommand.then(Commands.literal(useFullName ? BIOME : BIOME_SHORT)
                .executes(context -> executeGetBiome(context, context.getSource().getPosition()))
                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                        .executes(context -> executeGetBiome(context, Vec3Argument.getVec3(context, XYZ)))));

        // get structures [xyz]
        getCommand.then(Commands.literal(useFullName ? STRUCTURES : STRUCTURES_SHORT)
                .executes(context -> executeGetStructures(context, context.getSource().getPosition()))
                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                        .executes(context -> executeGetStructures(context, Vec3Argument.getVec3(context, XYZ)))));

    }

    /**
     * 获取方块实体的NBT
     */
    private static int executeGetBlockEntitiesNBT(CommandContext<CommandSourceStack> context, Vec3 pos) {
        context.getSource().sendSuccess(() -> Component.literal("Executing get blockentitiesnbt at: " + pos.toString()), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetBlockEntityNBT(CommandContext<CommandSourceStack> context, Vec3 pos) {
        context.getSource().sendSuccess(() -> Component.literal("Executing get blockentitynbt at: " + pos.toString()), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取群系/建筑信息（用于写物资刷新配置）
     */
    private static int executeGetBiome(CommandContext<CommandSourceStack> context, Vec3 pos) {
        context.getSource().sendSuccess(() -> Component.literal("Executing get biomes at: " + pos.toString()), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeGetStructures(CommandContext<CommandSourceStack> context, Vec3 pos) {
        context.getSource().sendSuccess(() -> Component.literal("Executing get structures at: " + pos.toString()), false);
        return Command.SINGLE_SUCCESS;
    }
}
