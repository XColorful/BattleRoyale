package xiao.battleroyale.developer.debug.command.sub.get;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.developer.debug.DebugManager;
import xiao.battleroyale.developer.debug.DebugWorld;

import static xiao.battleroyale.developer.debug.command.CommandArg.*;

public class GetWorld {

    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> getCommand, boolean useFullName) {

        // get blockentitiesnbt [xyz]
        getCommand.then(Commands.literal(useFullName ? BLOCK_ENTITIES_NBT : BLOCK_ENTITIES_NBT_SHORT)
                .executes(context -> getBlockEntitiesNBT(context, context.getSource().getPosition()))
                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                        .executes(context -> getBlockEntitiesNBT(context, Vec3Argument.getVec3(context, XYZ)))));

        // get blockentitynbt [xyz]
        getCommand.then(Commands.literal(useFullName ? BLOCK_ENTITY_NBT : BLOCK_ENTITY_NBT_SHORT)
                .executes(context -> getBlockEntityNBT(context, context.getSource().getPosition()))
                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                        .executes(context -> getBlockEntityNBT(context, Vec3Argument.getVec3(context, XYZ)))));


        // get biome [xyz]
        getCommand.then(Commands.literal(useFullName ? BIOME : BIOME_SHORT)
                .executes(context -> getBiome(context, context.getSource().getPosition()))
                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                        .executes(context -> getBiome(context, Vec3Argument.getVec3(context, XYZ)))));

        // get structures [xyz]
        getCommand.then(Commands.literal(useFullName ? STRUCTURES : STRUCTURES_SHORT)
                .executes(context -> getStructures(context, context.getSource().getPosition()))
                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                        .executes(context -> getStructures(context, Vec3Argument.getVec3(context, XYZ)))));

    }

    /**
     * 获取方块实体的NBT
     */
    private static int getBlockEntitiesNBT(CommandContext<CommandSourceStack> context, Vec3 pos) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugWorld.get().getBlockEntitiesNbt(source, pos);
        return Command.SINGLE_SUCCESS;
    }
    private static int getBlockEntityNBT(CommandContext<CommandSourceStack> context, Vec3 pos) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugWorld.get().getBLockENtityNbt(source, pos);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取群系/建筑信息（用于写物资刷新配置）
     */
    private static int getBiome(CommandContext<CommandSourceStack> context, Vec3 pos) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugWorld.get().getBiome(source, pos);
        return Command.SINGLE_SUCCESS;
    }

    private static int getStructures(CommandContext<CommandSourceStack> context, Vec3 pos) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugWorld.get().getStructures(source, pos);
        return Command.SINGLE_SUCCESS;
    }
}
