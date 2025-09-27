package xiao.battleroyale.compat.tacz;

import com.tacz.guns.api.event.common.*;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.compat.playerrevive.PlayerRevive;

/**
 * 处理 TaCZ 在 PlayerRevive 倒地状态期间的事件
 */
public class TaczBleedingHandler {

    private static class TaczBleedingHandlerHolder {
        private static final TaczBleedingHandler INSTANCE = new TaczBleedingHandler();
    }

    public static TaczBleedingHandler get() {
        return TaczBleedingHandlerHolder.INSTANCE;
    }

    private TaczBleedingHandler() {}

    private static boolean isRegistered = false;

    public static void register() {
        if (isRegistered) {
            return;
        }
        MinecraftForge.EVENT_BUS.register(get());
        isRegistered = true;
        BattleRoyale.LOGGER.debug("Registered TaczBleedingHandler");
    }

    public static void unregister() {
        if (!isRegistered) {
            return;
        }
        MinecraftForge.EVENT_BUS.unregister(get());
        isRegistered = false;
        BattleRoyale.LOGGER.debug("Unregistered TaczBleedingHandler");
    }

    /**
     * 服务端取消倒地开枪
     */
    @SubscribeEvent
    public void onGunShoot(GunShootEvent event) {
        if (Tacz.allowDownShoot || event.getLogicalSide() == LogicalSide.CLIENT) {
            return;
        }

        if (!(event.getShooter() instanceof Player player)) {
            return;
        }
        if (PlayerRevive.get().isBleeding(player)) {
            event.setCanceled(true);
            BattleRoyale.LOGGER.debug("Prevented bleeding {} from shooting", getPlayerDebugName(player));
        }
    }

    /**
     * 服务端取消装弹
     */
    @SubscribeEvent
    public void onGunReload(GunReloadEvent event) {
        if (Tacz.allowDownReload || event.getLogicalSide() == LogicalSide.CLIENT) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (PlayerRevive.get().isBleeding(player)) {
            event.setCanceled(true);
            BattleRoyale.LOGGER.debug("Prevented bleeding {} from reloading", getPlayerDebugName(player));
        }
    }

//    /**
//     * 装弹时被击倒，倒地期间装弹完成将被取消
//     */
//    @SubscribeEvent
//    public void onGunFinishReload(GunFinishReloadEvent event) {
//        if (event.getLogicalSide() == LogicalSide.CLIENT) {
//            return;
//        }
//
//        // 没提供Entity或者Player，只能提前拦截
//    }

    /**
     * 取消倒地时切换开火模式
     */
    @SubscribeEvent
    public void onGunFireSelect(GunFireSelectEvent event) {
        if (Tacz.allowDownFireSelect || event.getLogicalSide() == LogicalSide.CLIENT) {
            return;
        }

        if (!(event.getShooter() instanceof Player player)) {
            return;
        }
        if (PlayerRevive.get().isBleeding(player)) {
            event.setCanceled(true);
            BattleRoyale.LOGGER.debug("Prevented bleeding {} from selecting fire mode", getPlayerDebugName(player));
        }
    }

    /**
     * 取消倒地时用枪近战
     */
    @SubscribeEvent
    public void onGunMeleeEvent(GunMeleeEvent event) {
        if (Tacz.allowDownMelee || event.getLogicalSide() == LogicalSide.CLIENT) {
            return;
        }

        if (!(event.getShooter() instanceof Player player)) {
            return;
        }
        if (PlayerRevive.get().isBleeding(player)) {
            event.setCanceled(true);
            BattleRoyale.LOGGER.debug("Prevented bleeding {} from GunMelee", getPlayerDebugName(player));
        }
    }

    private static String getPlayerDebugName(Player player) {
        GamePlayer gamePlayer = GameTeamManager.getGamePlayerByUUID(player.getUUID());
        if (gamePlayer != null) {
            return String.format("GamePlayer [%s][%s]%s", gamePlayer.getGameTeamId(), gamePlayer.getGameSingleId(), gamePlayer.getPlayerName());
        } else {
            return String.format("Player %s (UUID:%s)", player.getName().getString(), player.getUUID());
        }
    }
}
