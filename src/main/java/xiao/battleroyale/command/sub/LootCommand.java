package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.loot.LootGenerationManager;
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
        CommandSourceStack source = context.getSource();
        if (!GameManager.get().isInGame()) {
            GameManager.get().setGameId(UUID.randomUUID());
            BattleRoyale.LOGGER.info("Generated random UUID for GameManager via command");
        }
        UUID currentWorldGameId = GameManager.get().getGameId();

        int totalChunks = LootGenerationManager.get().startGenerationTask(source, currentWorldGameId);

        if (totalChunks > 0) {
            source.sendSuccess(() -> Component.translatable("battleroyale.message.loot_generation_started", totalChunks), true);
            return Command.SINGLE_SUCCESS;
        } else if (totalChunks == 0){
            source.sendFailure(Component.translatable("battleroyale.message.loot_generation_in_progress"));
            return 0;
        } else {
            return 0;
        }
    }
}