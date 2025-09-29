package xiao.battleroyale.init.registry;

import net.minecraft.sounds.SoundEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.init.registry.IRegistrar;

public class ModSounds {
    public static final IRegistrar<SoundEvent> SOUNDS =
            BattleRoyale.getRegistrarFactory().createSounds(BattleRoyale.MOD_ID);
}