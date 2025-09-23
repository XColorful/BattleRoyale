package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.util.ChatUtils;

public class MessageFunc extends AbstractSimpleFunc {

    public final boolean setTitleAnimation;
    public final int fadeInTicks;
    public final int stayTicks;
    public final int fadeOutTicks;
    public final boolean sendTitle;
    public @NotNull final Component title;
    public final boolean sendSubtitle;
    public @NotNull final Component subTitle;
    public final boolean sendActionBar;
    public @NotNull final Component actionBar;

    public MessageFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset,
                       boolean setTitleAnimation, int fadeInTicks, int stayTicks, int fadeOutTicks,
                       boolean sendTitle, @NotNull Component title, boolean sendSubtitle, @NotNull Component subTitle, boolean sendActionBar, @NotNull Component actionBar) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.setTitleAnimation = setTitleAnimation;
        this.fadeInTicks = fadeInTicks;
        this.stayTicks = stayTicks;
        this.fadeOutTicks = fadeOutTicks;
        this.sendTitle = sendTitle;
        this.title = title;
        this.sendSubtitle = sendSubtitle;
        this.subTitle = subTitle;
        this.sendActionBar = sendActionBar;
        this.actionBar = actionBar;
        if (!(this.setTitleAnimation || this.sendTitle || this.sendSubtitle || this.sendActionBar)) {
            BattleRoyale.LOGGER.warn("MessageFunc will not send any message, please check zone config which has title:{}, subtitle:{}, actionBar:{}", this.title, this.subTitle, this.actionBar);
        }
    }

    @Override
    public void funcTick(ZoneTickContext zoneTickContext) {
        for (GamePlayer gamePlayer : zoneTickContext.gamePlayers) {
            if (!gamePlayer.isBot() && zoneTickContext.spatialZone.isWithinZone(gamePlayer.getLastPos(), zoneTickContext.progress)) {
                LivingEntity livingEntity = (LivingEntity) zoneTickContext.serverLevel.getEntity(gamePlayer.getPlayerUUID());
                if (livingEntity instanceof ServerPlayer player) {
                    if (this.setTitleAnimation) {
                        ChatUtils.sendTitleAnimationToPlayer(player, fadeInTicks, stayTicks, fadeOutTicks);
                    }
                    if (this.sendTitle) {
                        ChatUtils.sendTitleToPlayer(player, title);
                    }
                    if (this.sendSubtitle) {
                        ChatUtils.sendSubtitleToPlayer(player, subTitle);
                    }
                    if (this.sendActionBar) {
                        ChatUtils.sendActionBarToPlayer(player, actionBar);
                    }
                }
            }
        }
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.MESSAGE;
    }
}