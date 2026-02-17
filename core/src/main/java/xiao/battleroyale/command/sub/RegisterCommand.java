package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiao.battleroyale.BattleRoyale;

import static xiao.battleroyale.command.CommandArg.*;

public class RegisterCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(REGISTER)
                .then(Commands.literal(MANAGER)
                                .then(Commands.argument(PROTOCOL, StringArgumentType.string())
                                        .executes(RegisterCommand::registerManager)
                                )
                );

    }

    public static int registerManager(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String protocol = StringArgumentType.getString(context, PROTOCOL);
        if (BattleRoyale.registerManager(source, protocol)) {
            source.sendSuccess(() -> Component.translatable("battleroyale.message.register_manager_success", protocol), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.translatable("battleroyale.message.register_manager_failed", protocol));
            return 0;
        }
    }
}
