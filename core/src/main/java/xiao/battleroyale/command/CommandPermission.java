package xiao.battleroyale.command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.players.NameAndId;

public class CommandPermission {

    public static final int MAX_PERMISSION_LEVEL = PermissionLevel.OWNERS.id();

    public static boolean checkCommandLevel(CommandSourceStack source, int requiredLevel) {
        ServerPlayer player = source.getPlayer();

        if (player != null) {
            NameAndId nameAndId = new NameAndId(player.getGameProfile().id(), player.getGameProfile().name());
            LevelBasedPermissionSet permissions = source.getServer().getProfilePermissions(nameAndId);
            int actualLevel = permissions.level().id();
            return actualLevel >= requiredLevel;
        }
        return requiredLevel <= MAX_PERMISSION_LEVEL;
    }
}
