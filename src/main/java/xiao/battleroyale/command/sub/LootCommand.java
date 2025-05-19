package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class LootCommand {
    private static final String LOOT_NAME = "loot";

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        LiteralArgumentBuilder<CommandSourceStack> loot = Commands.literal(LOOT_NAME);
        loot.executes(LootCommand::generate);
        return loot;
    }

    private static int generate(CommandContext<CommandSourceStack> loot) {
        return Command.SINGLE_SUCCESS;
    }
}
