package xiao.battleroyale.api.minecraft;

import net.minecraft.commands.CommandSourceStack;

import java.util.function.Predicate;

public class CommandLevel {

    /**
     * 手动检查是否有足够权限
     */
    public static boolean hasPermission(CommandSourceStack sourceStack, int level) {
        return sourceStack.hasPermission(level);
    }

    public static Predicate<CommandSourceStack> hasPermission(int level) {
        return source -> source.hasPermission(level);
    }
}