package xiao.battleroyale.developer.gm.command.sub.original;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import static xiao.battleroyale.developer.gm.command.CommandArg.*;

public class GmCapability {

    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> gmCommand, boolean useFullName) {
        // 事件修改伤害
        // /battleroyale original eventdamage [amount]
        gmCommand.then(Commands.literal(useFullName ? EVENT_DAMAGE : EVENT_DAMAGE_SHORT)
                .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                        .executes(GmCapability::executeEventDamage)));
        // 最后位置引导
        // /battleroyale original lastpos [id / entity]
        gmCommand.then(Commands.literal(useFullName ? LAST_POS : LAST_POS_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GmCapability::executeLastPosById))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .executes(GmCapability::executeLastPosByEntity)));
    }

    /**
     * 事件修改伤害
     */
    private static int executeEventDamage(CommandContext<CommandSourceStack> context) {
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing eventdamage with amount: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 最后位置引导
     */
    private static int executeLastPosById(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing lastpos for ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 最后位置引导
     */
    private static int executeLastPosByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing lastpos for entity: " + entity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }
}