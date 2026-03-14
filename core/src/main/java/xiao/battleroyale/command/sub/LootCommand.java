package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ICommonInventoryManager;
import xiao.battleroyale.api.minecraft.InventoryIndex;
import xiao.battleroyale.common.loot.LootStatus;
import xiao.battleroyale.util.StringUtils;

import java.util.List;

import static xiao.battleroyale.command.CommandArg.*;
import static xiao.battleroyale.util.StringUtils.buildCommandString;

public class LootCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        LiteralArgumentBuilder<CommandSourceStack> loot = Commands.literal(LOOT);

        // 执行刷新

        // loot generate
        loot.then(Commands.literal(GENERATE)
                .executes(LootCommand::lootGeneration)
        );
        // loot stop
        loot.then(Commands.literal(STOP)
                .executes(LootCommand::stopLootGeneration));
        // loot chunk [<x> <y> <z>]
        loot.then(Commands.literal(CHUNK)
                .executes(context -> lootChunk(context, context.getSource().getPosition()))
                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                        .executes(context -> lootChunk(context, Vec3Argument.getVec3(context, XYZ))))
        );
        // loot pos [<x> <y> <z>]
        loot.then(Commands.literal(POS)
                .executes(context -> lootPos(context, context.getSource().getPosition()))
                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                        .executes(context -> lootPos(context, Vec3Argument.getVec3(context, XYZ))))
        );

        // 背包刷新

        // loot player lootId generate
        // loot player lootId skipNonEmpty dropBeforeReplace first last
        loot.then(Commands.argument(PLAYER, EntityArgument.players())
                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                        .then(Commands.literal(RESET)
                                .executes(LootCommand::resetInventoryWithLoot)
                        )
                        .then(Commands.literal(GENERATE)
                                .then(Commands.argument(SKIP_NON_EMPTY, BoolArgumentType.bool())
                                        .then(Commands.argument(DROP_BEFORE_REPLACE, BoolArgumentType.bool())
                                                .then(Commands.argument(FIRST_SLOT_INDEX, IntegerArgumentType.integer(InventoryIndex.HOTBAR_START, InventoryIndex.OFFHAND_END))
                                                        .then(Commands.argument(LAST_SLOT_INDEX, IntegerArgumentType.integer(InventoryIndex.HOTBAR_START, InventoryIndex.OFFHAND_END))
                                                                .executes(LootCommand::generatePlayerInventoryLoot)
                                                        )
                                                )
                                        )
                                )
                        )

                )
        );

        return loot;
    }

    public static int lootGeneration(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        int totalChunks = BattleRoyale.getCommonLootManager().lootGeneration(source, source.getLevel());
        if (totalChunks > 0) {
            source.sendSuccess(() -> Component.translatable("battleroyale.message.loot_generation_started", totalChunks), true);
            return Command.SINGLE_SUCCESS;
        } else {
            return 0;
        }
    }
    public static int stopLootGeneration(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (BattleRoyale.getCommonLootManager().stopLootGeneration(source)) {
            source.sendSuccess(() -> Component.translatable("battleroyale.message.loot_generation_stopped"), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.translatable("battleroyale.message.loot_unavailable"));
            return 0;
        }
    }

    public static int lootChunk(CommandContext<CommandSourceStack> context, Vec3 pos) {
        CommandSourceStack source = context.getSource();
        int lootCount = BattleRoyale.getCommonLootManager().lootChunk(source, source.getLevel(), pos);

        if (lootCount >= 0) {
            source.sendSuccess(() -> Component.translatable("battleroyale.message.loot_generation_finished", lootCount), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.translatable("battleroyale.message.loot_unavailable"));
            return 0;
        }
    }

    public static int lootPos(CommandContext<CommandSourceStack> context, Vec3 pos) {
        CommandSourceStack source = context.getSource();
        LootStatus result = BattleRoyale.getCommonLootManager().lootPos(source, source.getLevel(), pos);
        if (result == LootStatus.AVAILABLE) {
            source.sendSuccess(() -> Component.translatable("battleroyale.message.loot_pos_success", StringUtils.vectorTo2fString(pos)), true);
            return Command.SINGLE_SUCCESS;
        } else if (result == LootStatus.UNAVAILABLE) {
            source.sendFailure(Component.translatable("battleroyale.message.loot_unavailable"));
        }
        return 0;
    }

    public static int resetInventoryWithLoot(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        List<ServerPlayer> players = net.minecraft.commands.arguments.EntityArgument.getPlayers(context, PLAYER).stream().toList();
        int lootId = IntegerArgumentType.getInteger(context, ID);

        ICommonInventoryManager inventoryManager = BattleRoyale.getCommonInventoryManager();

        int count = inventoryManager.resetInventoryWithLoot(source, source.getLevel(), players, lootId);
        if (count >= 0) {
            source.sendSuccess(() -> Component.translatable("battleroyale.message.reset_inventory_finished", count), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.translatable("battleroyale.message.inventory_loot_failed"));
            return 0;
        }
    }

    public static int generatePlayerInventoryLoot(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        List<ServerPlayer> players = net.minecraft.commands.arguments.EntityArgument.getPlayers(context, PLAYER).stream().toList();
        int lootId = IntegerArgumentType.getInteger(context, ID);
        boolean skipNonEmpty = BoolArgumentType.getBool(context, SKIP_NON_EMPTY);
        boolean dropBeforeReplace = BoolArgumentType.getBool(context, DROP_BEFORE_REPLACE);
        int firstIndex = IntegerArgumentType.getInteger(context, FIRST_SLOT_INDEX);
        int lastIndex = IntegerArgumentType.getInteger(context, LAST_SLOT_INDEX);

        ICommonInventoryManager inventoryManager = BattleRoyale.getCommonInventoryManager();

        int count = inventoryManager.inventoryLootGeneration(source, source.getLevel(),
                players, lootId, firstIndex, lastIndex, skipNonEmpty, dropBeforeReplace);
        if (count >= 0) {
            source.sendSuccess(() -> Component.translatable("battleroyale.message.inventory_loot_generation_finished", count), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.translatable("battleroyale.message.inventory_loot_failed"));
            return 0;
        }
    }

    public static String getLootPlayerResetCommand(String player, int lootId) {
        return buildCommandString(
                MOD_ID,
                LOOT,
                player,
                Integer.toString(lootId),
                RESET
        );
    }
}