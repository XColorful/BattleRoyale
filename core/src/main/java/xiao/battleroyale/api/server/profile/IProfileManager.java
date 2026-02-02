package xiao.battleroyale.api.server.profile;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.server.IServerSubManager;

public interface IProfileManager extends IServerSubManager {

    int saveCurrentProfile(@Nullable CommandSourceStack source, @Nullable ServerLevel serverLevel, int id, boolean overwrite);

    int loadProfile(@Nullable CommandSourceStack source, @Nullable ServerLevel serverLevel, int id);
}
