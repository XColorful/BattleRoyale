package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.loot.LootGenerator;
import java.util.UUID;

public class LootCommand {
    private static final String LOOT_NAME = "loot";
    private static final String GENERATE_NAME = "generate";

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        LiteralArgumentBuilder<CommandSourceStack> loot = Commands.literal(LOOT_NAME);
        loot.then(Commands.literal(GENERATE_NAME)
                .executes(LootCommand::generateAllLoadedLoot)
        );
        return loot;
    }

    private static int generateAllLoadedLoot(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        UUID currentWorldGameId = null; // TODO: 大逃杀模式完成后修改，从游戏状态管理器获取

        if (currentWorldGameId == null) {
            currentWorldGameId = UUID.randomUUID();
            BattleRoyale.LOGGER.warn("No active game ID found, using a random UUID for loot generation.");
        }

        int count = LootGenerator.refreshAllLoadedLoot(level, currentWorldGameId);
        context.getSource().sendSuccess(() -> Component.translatable("battleroyale.message.loot_generation_finished", count), true);
        return Command.SINGLE_SUCCESS;
    }
}