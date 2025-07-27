package xiao.battleroyale.developer.gm.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static xiao.battleroyale.developer.gm.command.CommandArg.*;

public class LootManager {
    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> gmCommand, boolean useFullName) {
        // 中止物资刷新
        // /battleroyale gamemaster delete commonloot
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? COMMON_LOOT : COMMON_LOOT_SHORT)
                        .executes(LootManager::executeDeleteCommonLoot)));
        // 清理游戏刷新队列
        // /battleroyale gamemaster delete queuedchunk [amount / all]
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? QUEUED_CHUNK : QUEUED_CHUNK_SHORT)
                        .then(Commands.literal(ALL)
                                .executes(context -> executeDeleteQueuedChunk(context, Integer.MAX_VALUE)))
                        .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                .executes(context -> executeDeleteQueuedChunk(context, IntegerArgumentType.getInteger(context, AMOUNT))))));
        // 清理区块缓存
        // /battleroyale gamemaster delete processedchunk [amount / all]
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? PROCESSED_CHUNK : PROCESSED_CHUNK_SHORT)
                        .then(Commands.literal(ALL)
                                .executes(context -> executeDeleteProcessedChunk(context, Integer.MAX_VALUE)))
                        .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                .executes(context -> executeDeleteProcessedChunk(context, IntegerArgumentType.getInteger(context, AMOUNT))))));
        // 清理中心缓存
        // /battleroyale gamemaster delete cachedcenter [amount / all]
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? CACHED_CENTER : CACHED_CENTER_SHORT)
                        .then(Commands.literal(ALL)
                                .executes(context -> executeDeleteCachedCenter(context, Integer.MAX_VALUE)))
                        .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                .executes(context -> executeDeleteCachedCenter(context, IntegerArgumentType.getInteger(context, AMOUNT))))));
    }

    /**
     * 中止物资刷新
     */
    private static int executeDeleteCommonLoot(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Executing delete commonloot"), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 清理游戏刷新队列
     */
    private static int executeDeleteQueuedChunk(CommandContext<CommandSourceStack> context, int amount) {
        if (amount == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete all queued chunks"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete " + amount + " queued chunks"), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 清理区块缓存
     * */
    private static int executeDeleteProcessedChunk(CommandContext<CommandSourceStack> context, int amount) {
        if (amount == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete all processed chunks"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete " + amount + " processed chunks"), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 清理中心缓存
     */
    private static int executeDeleteCachedCenter(CommandContext<CommandSourceStack> context, int amount) {
        if (amount == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete all cached centers"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete " + amount + " cached centers"), false);
        }
        return Command.SINGLE_SUCCESS;
    }
}