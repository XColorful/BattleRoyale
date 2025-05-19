package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xiao.battleroyale.config.common.loot.LootConfigManager;

public class ReloadCommand {
    private static final String RELOAD_NAME = "reload";
    private static final String LOOT_CONFIG_NAME = "loot";

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(RELOAD_NAME)
                .then(Commands.literal(LOOT_CONFIG_NAME)
                        .executes(ReloadCommand::reloadLootConfigs));
    }

    private static int reloadLootConfigs(CommandContext<CommandSourceStack> context) {
        LootConfigManager.get().reloadConfigs();
        context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Battle Royale loot configurations reloaded."), true);
        return Command.SINGLE_SUCCESS;
    }
}