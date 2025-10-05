package xiao.battleroyale.compat.tacz;

import net.minecraft.world.entity.player.Player;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.tacz.IGunFireSelectEvent;
import xiao.battleroyale.api.compat.tacz.IGunMeleeEvent;
import xiao.battleroyale.api.compat.tacz.IGunReloadEvent;
import xiao.battleroyale.api.compat.tacz.IGunShootEvent;
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

    public void register() {
        if (isRegistered) {
            return;
        }
        isRegistered = BattleRoyale.getCompatApi().taczEventRegister().registerBleedingHandler();
    }

    public void unregister() {
        if (!isRegistered) {
            return;
        }
        BattleRoyale.getCompatApi().taczEventRegister().unregisterBleedingHandler();
        isRegistered = false;
    }

    /**
     * 服务端取消倒地开枪
     */
    public void onGunShoot(IGunShootEvent event) {
        if (Tacz.allowDownShoot || event.getMcSide() == McSide.CLIENT) {
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
    public void onGunReload(IGunReloadEvent event) {
        if (Tacz.allowDownReload || event.getMcSide() == McSide.CLIENT) {
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
//    public void onGunFinishReload(IGunFinishReloadEvent event) {
//        // 没提供Entity或者Player，只能提前拦截
//    }

    /**
     * 取消倒地时切换开火模式
     */
    public void onGunFireSelect(IGunFireSelectEvent event) {
        if (Tacz.allowDownFireSelect || event.getMcSide() == McSide.CLIENT) {
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
    public void onGunMelee(IGunMeleeEvent event) {
        if (Tacz.allowDownMelee || event.getMcSide() == McSide.CLIENT) {
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
            return String.format("GamePlayer %s", gamePlayer.getNameWithId());
        } else {
            return String.format("Player %s (UUID:%s)", player.getName().getString(), player.getUUID());
        }
    }
}