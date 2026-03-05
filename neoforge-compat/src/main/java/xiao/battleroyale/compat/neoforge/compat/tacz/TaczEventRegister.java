package xiao.battleroyale.compat.neoforge.compat.tacz;

import com.tacz.guns.api.event.common.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
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

    private final BleedingEventListener bleedingListener = new BleedingEventListener();

    @Override
    public boolean registerBleedingHandler() {
        NeoForge.EVENT_BUS.register(bleedingListener);
        BattleRoyale.LOGGER.debug("Registered TaczBleedingHandler");
        return true;
    }

    @Override
    public boolean unregisterBleedingHandler() {
        NeoForge.EVENT_BUS.unregister(bleedingListener);
        BattleRoyale.LOGGER.debug("Unregistered TaczBleedingHandler");
        return true;
    }

    private static class BleedingEventListener {
        @SubscribeEvent
        public void onGunShoot(GunShootEvent event) {
            TaczBleedingHandler.get().onGunShoot(new TaczGunShootEvent(event));
        }

        @SubscribeEvent
        public void onGunReload(GunReloadEvent event) {
            TaczBleedingHandler.get().onGunReload(new TaczGunReloadEvent(event));
        }

        @SubscribeEvent
        public void onGunFireSelect(GunFireSelectEvent event) {
            TaczBleedingHandler.get().onGunFireSelect(new TaczGunFireSelectEvent(event));
        }

        @SubscribeEvent
        public void onGunMelee(GunMeleeEvent event) {
            TaczBleedingHandler.get().onGunMelee(new TaczGunMeleeEvent(event));
        }
    }
}