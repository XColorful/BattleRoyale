package xiao.battleroyale.compat.neoforge.test;

import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 模拟原生的 NeoForge 事件，用于性能对比测试
 */
public class NeoTriggerTestEvent extends Event implements ICancellableEvent {
    private final @Nullable CommandSourceStack source;
    private final String triggerString;

    public NeoTriggerTestEvent(@Nullable CommandSourceStack source, String triggerString) {
        this.source = source;
        this.triggerString = triggerString;
    }

    public @Nullable CommandSourceStack getSource() { return source; }
    public String getTriggerString() { return triggerString; }
}