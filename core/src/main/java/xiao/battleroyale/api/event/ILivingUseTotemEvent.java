package xiao.battleroyale.api.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ILivingUseTotemEvent extends IEvent {

    @NotNull LivingEntity getEntity();

    @NotNull DamageSource getSource();

    @NotNull ItemStack getTotem();

    @NotNull InteractionHand getHandHolding();
}