package xiao.battleroyale.command.sub;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.IConfigManager;
import xiao.battleroyale.api.config.IConfigSubManager;

public class ConfigUtils {

    public static @Nullable IConfigManager getConfigManager(CommandContext<CommandSourceStack> context, String managerNameKey) {
        IConfigManager configManager = BattleRoyale.getModConfigManager().getConfigManager(managerNameKey);
        if (configManager == null) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_config_manager_available", managerNameKey));
            return null;
        }
        return configManager;
    }

    public static @Nullable IConfigSubManager<?> getConfigSubManager(CommandContext<CommandSourceStack> context, String managerNameKey, String subManagerNameKey) {
        IConfigManager configManager = BattleRoyale.getModConfigManager().getConfigManager(managerNameKey);
        if (configManager == null) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_config_manager_available", managerNameKey));
            return null;
        }
        IConfigSubManager<?> configSubManager = configManager.getConfigSubManager(subManagerNameKey);
        if (configSubManager == null) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_sub_config_manager_available", subManagerNameKey));
            return null;
        }
        return configSubManager;
    }
    public static @Nullable IConfigSubManager<?> getConfigSubManager(CommandContext<CommandSourceStack> context, String subManagerNameKey) {
        IConfigSubManager<?> configSubManager = BattleRoyale.getModConfigManager().getConfigSubManager(subManagerNameKey);
        if (configSubManager == null) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_sub_config_manager_available", subManagerNameKey));
            return null;
        }
        return configSubManager;
    }
}
