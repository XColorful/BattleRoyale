package xiao.battleroyale.compat.fabric.compat.tacz;

import com.tacz.guns.api.event.common.*;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.compat.tacz.ITaczEventRegister;
import xiao.battleroyale.compat.tacz.TaczBleedingHandler;

public class TaczEventRegister implements ITaczEventRegister {

    private static class TaczEventRegisterHolder {
        private static final TaczEventRegister INSTANCE = new TaczEventRegister();
    }

    public static TaczEventRegister get() {
        return TaczEventRegisterHolder.INSTANCE;
    }

    private TaczEventRegister() {}

    private boolean isRegistered = false;

    @Override
    public boolean registerBleedingHandler() {
        if (isRegistered) return true;

        GunShootEvent.CALLBACK.register(event ->
                TaczBleedingHandler.get().onGunShoot(new TaczGunShootEvent(event)));

        GunReloadEvent.CALLBACK.register(event ->
                TaczBleedingHandler.get().onGunReload(new TaczGunReloadEvent(event)));

        GunFireSelectEvent.CALLBACK.register(event ->
                TaczBleedingHandler.get().onGunFireSelect(new TaczGunFireSelectEvent(event)));

        GunMeleeEvent.CALLBACK.register(event ->
                TaczBleedingHandler.get().onGunMelee(new TaczGunMeleeEvent(event)));

        BattleRoyale.LOGGER.debug("Registered TaczBleedingHandler");
        isRegistered = true;
        return true;
    }

    @Override
    public boolean unregisterBleedingHandler() {
        // Fabric API 标准的 Event 接口不支持注销单个 Lambda
        // 注销通常需要在 lambda 内部做一个开关判断，或者使用 TaCZ 提供的特定移除机制
        // 目前简单返回 false 或记录状态
        return false;
    }
}