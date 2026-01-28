package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.loot.LootStatus;
import xiao.battleroyale.util.StringUtils;

import static xiao.battleroyale.command.CommandArg.*;

public class LootCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        LiteralArgumentBuilder<CommandSourceStack> loot = Commands.literal(LOOT);

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

        // loot [@e] lootId skipNonEmpty dropBeforeReplace first last
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
}