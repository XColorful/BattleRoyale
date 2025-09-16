package xiao.battleroyale.compat.tacz;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.compat.AbstractCompatMod;
import xiao.battleroyale.compat.playerrevive.BleedingHandler;

public class Tacz extends AbstractCompatMod {

    @Override
    public String getModId() {
        return "tacz";
    }

    private static class TaczHolder {
        private static final Tacz INSTANCE = new Tacz();
    }

    public static Tacz get() {
        return TaczHolder.INSTANCE;
    }

    private Tacz() {}

    /**
     * 抛出异常后父类会处理为未加载
     * 任一api变动即视为不兼容
     */
    @Override
    protected void onModLoaded() throws Exception {
        Class<?> gunShootEvent = Class.forName("com.tacz.guns.api.event.common.GunShootEvent");
        gunShootEvent.getMethod("getShooter");

        Class<?> gunReloadEvent = Class.forName("com.tacz.guns.api.event.common.GunReloadEvent");
        gunReloadEvent.getMethod("getEntity");

//        Class<?> gunFinishReloadEvent = Class.forName("com.tacz.guns.api.event.common.GunFinishReloadEvent");
//        gunFinishReloadEvent.getMethod("getGunItemStack");

        Class<?> gunFireSelectEvent = Class.forName("com.tacz.guns.api.event.common.GunFireSelectEvent");
        gunFireSelectEvent.getMethod("getShooter");

        Class<?> gunMeleeEvent = Class.forName("com.tacz.guns.api.event.common.GunMeleeEvent");
        gunMeleeEvent.getMethod("getShooter");

        Class<?> iGunOperator = Class.forName("com.tacz.guns.api.entity.IGunOperator");
        iGunOperator.getMethod("fromLivingEntity", LivingEntity.class);
        iGunOperator.getMethod("cancelReload");
    }

    /**
     * 监听并拦截倒地玩家触发的 GunShootEvent
     */
    public static void registerBleedingEvent() {
        if (!get().isLoaded() || !enableBleedingHandler) {
            return;
        }
        TaczBleedingHandler.register();
    }
    public static void unregisterBleedingEvent() {
        if (!get().isLoaded()) {
            return;
        }
        TaczBleedingHandler.unregister();
    }

    public static void onAddingBleedingPlayer(@NotNull Player player) {
        if (!get().isLoaded()) {
            return;
        }
        // 实际取消不了换弹
        TaczGunOperator.cancelReload(player);
    }

    protected static boolean enableBleedingHandler = true;
    protected static boolean allowDownShoot = false;
    protected static boolean allowDownReload = false;
    protected static boolean allowDownFireSelect = false;
    protected static boolean allowDownMelee = false;

    public static void setGameConfig(boolean downShoot, boolean downReload, boolean downFireSelect, boolean downMelee) {
        allowDownShoot = downShoot;
        allowDownReload = downReload;
        allowDownFireSelect = downFireSelect;
        allowDownMelee = downMelee;
        enableBleedingHandler = !(allowDownShoot && allowDownReload && allowDownFireSelect && allowDownMelee);
        if (BleedingHandler.isIsRegistered()) { // 游戏中切换配置立即生效
            registerBleedingEvent();
        }
    }
}
