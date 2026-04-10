package xiao.battleroyale.compat.forge.test;

import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

/**
 * 模拟原生的 Forge 事件，用于性能对比测试
 */
@Cancelable
public class ForgeTriggerTestEvent extends Event {
    private final @Nullable CommandSourceStack source;
    private final String triggerString;

    public ForgeTriggerTestEvent(@Nullable CommandSourceStack source, String triggerString) {
        this.source = source;
        this.triggerString = triggerString;
    }

    public @Nullable CommandSourceStack getSource() { return source; }
    public String getTriggerString() { return triggerString; }
}