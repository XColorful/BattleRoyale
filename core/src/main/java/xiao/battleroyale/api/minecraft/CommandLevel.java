package xiao.battleroyale.api.minecraft;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.permissions.PermissionSet;

import java.util.function.Predicate;

public class CommandLevel {

    /**
     * 手动检查是否有足够权限
     */
    public static boolean hasPermission(CommandSourceStack sourceStack, int level) {
        return sourceStack.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.byId(level)));
    }

    public static Predicate<CommandSourceStack> hasPermission(int level) {
        return source -> hasPermission(source, level);
    }

    /**
     * 1.21.11 改为 net.minecraft.server.permissions.PermissionSet
     */
    public static PermissionSet permission(int level) {
        return LevelBasedPermissionSet.forLevel(PermissionLevel.byId(level));
    }
}