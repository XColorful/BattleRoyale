package xiao.battleroyale.compat.neoforge.test;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import xiao.battleroyale.BattleRoyale;

/**
 * 同 {@link xiao.battleroyale.developer.debug.command.sub.TestCommand}
 */
@EventBusSubscriber(modid = BattleRoyale.MOD_ID)
public class TestCommand {

    @SubscribeEvent
    public static void onServerCommandsRegister(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("cbr")
                .then(Commands.literal("db")
                        .then(Commands.literal("testNeo")
                                .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                        .executes(TestCommand::testNeoPostEvent)
                                )
                        )
                )
        );
    }

    private static int testNeoPostEvent(CommandContext<CommandSourceStack> context) {
        if (BattleRoyale.getMinecraftServer().getPlayerCount() > 3) {
            context.getSource().sendFailure(Component.literal("Reject to benchmark for above 3 players in server"));
            return 0;
        }

        CommandSourceStack source = context.getSource();
        int count = 0;
        int total = IntegerArgumentType.getInteger(context, "count");

        NeoForge.EVENT_BUS.register(_Handler.INSTANCE);

        // 记录时间
        long startTime = System.nanoTime();

        for (int i = 0; i < total; i++) {
            NeoForge.EVENT_BUS.post(new NeoTriggerTestEvent(source, ""));
            count++;
        }

        // 记录时间
        long endTime = System.nanoTime();

        long durationNs = endTime - startTime;
        double durationUs = durationNs / 1_000.0;     // 微秒
        double durationMs = durationNs / 1_000_000.0; // 毫秒

        final int _count = count;
        source.sendSuccess(() -> Component.literal(String.format("----NeoForge Native Perf test (%s TriggerEvent)----", _count))
                .withStyle(ChatFormatting.LIGHT_PURPLE), false);

        MutableComponent timeLine = Component.literal("Timeline: ")
                .append(Component.literal("[Start]").withStyle(s -> s.withColor(ChatFormatting.GRAY)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Start: " + startTime + " ns")))))
                .append(Component.literal(" -> ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal("[End]").withStyle(s -> s.withColor(ChatFormatting.GRAY)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("End: " + endTime + " ns")))));

        source.sendSuccess(() -> timeLine, false);
        source.sendSuccess(() -> Component.literal("Duration: ")
                .append(Component.literal(String.format("%.3f", durationMs)).withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" ms / "))
                .append(Component.literal(String.format("%.3f", durationUs)).withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(" us / "))
                .append(Component.literal(String.valueOf(durationNs)).withStyle(ChatFormatting.AQUA))
                .append(Component.literal(" ns")), false);

        NeoForge.EVENT_BUS.unregister(_Handler.INSTANCE);

        return 1;
    }

    public static class _Handler {
        public static final _Handler INSTANCE = new _Handler();
        public static volatile String blackhole;

        @SubscribeEvent
        public void onNeoTest(NeoTriggerTestEvent event) {
            // 强制逃逸
            blackhole = event.getTriggerString();
            event.setCanceled(true);
        }
    }
}