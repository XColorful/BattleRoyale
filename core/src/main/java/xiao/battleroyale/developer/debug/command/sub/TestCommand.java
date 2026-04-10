package xiao.battleroyale.developer.debug.command.sub;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.api.event.special.TriggerEvent;

public class TestCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> getServer(boolean useFullName) {
        return Commands.literal("test")
                .then(Commands.literal("postEvent")
                        .then(Commands.argument("count", IntegerArgumentType.integer())
                                .then(Commands.argument("postOnly", BoolArgumentType.bool())
                                        .executes(TestCommand::testPostEvent)
                                )
                        )
                );
    }

    public static int testPostEvent(CommandContext<CommandSourceStack> context) {
        if (BattleRoyale.getMinecraftServer().getPlayerCount() > 3) {
            context.getSource().sendFailure(Component.literal("Reject to benckmark for above 3 players in server"));
            return 0;
        }

        CommandSourceStack source = context.getSource();
        int count = 0;
        int total = IntegerArgumentType.getInteger(context, "count");
        boolean postOnly = BoolArgumentType.getBool(context, "postOnly");

        BattleRoyale.getEventRegister().register(_Handler.INSTANCE, CustomEventType.TRIGGER_EVENT, EventPriority.NORMAL, false);

        // 记录时间
        long startTime = System.nanoTime();

        TriggerEvent event = new TriggerEvent(source, "");
        ICustomEventPoster eventPoster = BattleRoyale.getEventPoster();
        if (postOnly) {
            for (int i = 0; i < total; i++) {
                event.getEventDispatcher().dispatch( // post性能
                        event,
                        (handler, customEvent) -> handler.handleEvent(customEvent.getEventType(), customEvent));
                count++;
            }
        } else {
            for (int i = 0; i < total; i++) {
                BattleRoyale.getEventPoster().postCustomEvent(new TriggerEvent(source, "")); // 实际性能
                count++;
            }
        }

        // 记录时间
        long endTime = System.nanoTime();

        long durationNs = endTime - startTime;
        double durationUs = durationNs / 1_000.0;     // 微秒
        double durationMs = durationNs / 1_000_000.0; // 毫秒

        // 输出结果
        final int _count = count;
        source.sendSuccess(() -> Component.literal(String.format("----Perf test (%s TriggerEvent)----", _count))
                .withStyle(ChatFormatting.GOLD), false);
        MutableComponent timeLine = Component.literal("Timeline: ")
                .append(Component.literal("[Start]")
                        .withStyle(style -> style.withColor(ChatFormatting.GRAY)
                                .withHoverEvent(new HoverEvent.ShowText(
                                        Component.literal("Start: " + startTime + " ns"))
                                )))
                .append(Component.literal(" -> ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal("[End]")
                        .withStyle(style -> style.withColor(ChatFormatting.GRAY)
                                .withHoverEvent(new HoverEvent.ShowText(
                                        Component.literal("End: " + endTime + " ns"))
                                )));

        source.sendSuccess(() -> timeLine, false);
        source.sendSuccess(() -> Component.literal("Duration: ")
                .append(Component.literal(String.format("%.3f", durationMs))
                        .withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" ms / "))
                .append(Component.literal(String.format("%.3f", durationUs))
                        .withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(" us / "))
                .append(Component.literal(String.valueOf(durationNs))
                        .withStyle(ChatFormatting.AQUA))
                .append(Component.literal(" ns")), false);

        BattleRoyale.getEventRegister().unregister(_Handler.INSTANCE, CustomEventType.TRIGGER_EVENT, EventPriority.NORMAL, false);

        return 1;
    }

    private static class _Handler implements ICustomEventHandler {
        public static final _Handler INSTANCE = new _Handler();
        public static volatile String blackhole;

        @Override
        public String getEventHandlerName() {
            return "";
        }

        @Override
        public void handleEvent(CustomEventType type, ICustomEvent event) {
            if (type == CustomEventType.TRIGGER_EVENT) {
                // 强制逃逸
                blackhole = ((TriggerEvent) event).getTriggerString();
                event.setCanceled(true);
            }
        }
    }
}
