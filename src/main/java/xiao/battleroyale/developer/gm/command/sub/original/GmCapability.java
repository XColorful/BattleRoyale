package xiao.battleroyale.developer.gm.command.sub.original;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
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

    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> ogCommand, boolean useFullName) {
        // 事件修改伤害
        // /battleroyale original eventdamage [amount]
        ogCommand.then(Commands.literal(useFullName ? EVENT_DAMAGE : EVENT_DAMAGE_SHORT)
                .then(Commands.argument(AMOUNT, FloatArgumentType.floatArg())
                        .executes(GmCapability::eventDamage)));
        // 最后位置引导
        // /battleroyale original lastpos [id / entity]
        ogCommand.then(Commands.literal(useFullName ? LAST_POS : LAST_POS_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GmCapability::lastPosById))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .executes(GmCapability::lastPosByEntity)));
    }

    /**
     * 事件修改伤害
     */
    private static int eventDamage(CommandContext<CommandSourceStack> context) {
        final float amount = FloatArgumentType.getFloat(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing eventdamage with amount: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 最后位置引导
     */
    private static int lastPosById(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing lastpos for ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 最后位置引导
     */
    private static int lastPosByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing lastpos for entity: " + entity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }
}