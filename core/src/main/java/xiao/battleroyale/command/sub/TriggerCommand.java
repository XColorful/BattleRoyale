package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.special.TriggerEvent;

import static xiao.battleroyale.command.CommandArg.*;

public class TriggerCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(TRIGGER)
                .then(Commands.argument(PROTOCOL, StringArgumentType.string())
                        .executes(TriggerCommand::trigger)
                        .then(Commands.argument(POS, Vec3Argument.vec3())
                                .then(Commands.argument(INT, IntegerArgumentType.integer())
                                        .then(Commands.argument(DOUBLE, DoubleArgumentType.doubleArg())
                                                .then(Commands.argument(BOOL, BoolArgumentType.bool())
                                                        .executes(TriggerCommand::triggerFull)
                                                )
                                        )
                                )
                        )
                );
    }

    public static int trigger(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getEventPoster().postCustomEvent(
                new TriggerEvent(
                        context.getSource(),
                        StringArgumentType.getString(context, PROTOCOL)
                )
        ) ? Command.SINGLE_SUCCESS : 0;
    }

    public static int triggerFull(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getEventPoster().postCustomEvent(
                new TriggerEvent(
                        context.getSource(),
                        StringArgumentType.getString(context, PROTOCOL),
                        Vec3Argument.getVec3(context, POS),
                        IntegerArgumentType.getInteger(context, INT),
                        DoubleArgumentType.getDouble(context, DOUBLE),
                        BoolArgumentType.getBool(context, BOOL)
                )
        ) ? Command.SINGLE_SUCCESS : 0;
    }
}
