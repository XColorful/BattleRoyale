package xiao.battleroyale.api.game.process;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.game.team.GamePlayer;

public interface IGameEventHandler {

    void onPlayerLoggedIn(@NotNull ServerLevel serverLevel, ServerPlayer player, boolean onlyGamePlayerSpectate);

    void onPlayerLoggedOut(boolean isInGame, ServerPlayer player);

    void onPlayerDown(@NotNull GamePlayer gamePlayer, LivingEntity livingEntity, boolean removeInvalidTeam);

    void onPlayerDeath(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer);

    void onPlayerRevived(@NotNull GamePlayer gamePlayer);
}
