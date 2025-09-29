package xiao.battleroyale.api.init;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public interface ICommandRegistry {

    void registerServerCommands(CommandDispatcher<CommandSourceStack> dispatcher);

    void registerClientCommands(CommandDispatcher<CommandSourceStack> dispatcher);
}