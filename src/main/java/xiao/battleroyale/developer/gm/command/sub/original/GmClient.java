package xiao.battleroyale.developer.gm.command.sub.original;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static xiao.battleroyale.developer.gm.command.CommandArg.*;

public class GmClient {

    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> ogCommand, boolean useFullName) {
        // 本地实体光柱
        // /battleroyale original localentity [amount]
        ogCommand.then(Commands.literal(useFullName ? LOCAL_ENTITY : LOCAL_ENTITY_SHORT)
                .then(Commands.argument(AMOUNT, FloatArgumentType.floatArg())
                        .executes(GmClient::localEntity)));
    }

    /**
     * 本地实体光柱
     */
    private static int localEntity(CommandContext<CommandSourceStack> context) {
        final float amount = FloatArgumentType.getFloat(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing localentity with amount: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }
}