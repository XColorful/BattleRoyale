package xiao.battleroyale.event.game;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDamageEvent; // 实体受到伤害事件
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.TeamManager;

/**
 * 统计造成的伤害值
 * 伤害数值调整
 */
public class DamageEventHandler {

    private static DamageEventHandler instance;

    private DamageEventHandler() {}

    public static DamageEventHandler getInstance() {
        if (instance == null) {
            instance = new DamageEventHandler();
        }
        return instance;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(getInstance());
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(getInstance());
        instance = null;
    }

    /**
     * 监听实体受到伤害事件。
     * 用于统计玩家造成的伤害和受到的伤害。
     * @param event 实体受到伤害事件
     */
    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        if (!GameManager.get().isInGame()) {
            unregister();
            return; // 只在游戏进行中处理
        }

        LivingEntity damagedEntity = event.getEntity();
        DamageSource damageSource = event.getSource();
        float damageAmount = event.getAmount();

        // 统计受到的伤害
        if (damagedEntity instanceof ServerPlayer damagedPlayer) {
            GamePlayer gamePlayer = TeamManager.get().getGamePlayerByUUID(damagedPlayer.getUUID());
            if (gamePlayer != null) {
                gamePlayer.addDamageTaken(damageAmount);
                // BattleRoyale.LOGGER.debug("Player {} took {} damage. Total: {}", damagedPlayer.getName().getString(), damageAmount, gamePlayer.getDamageTaken());
            }
        }

        // 统计造成的伤害
        if (damageSource.getEntity() instanceof ServerPlayer attackingPlayer) {
            GamePlayer gamePlayer = TeamManager.get().getGamePlayerByUUID(attackingPlayer.getUUID());
            if (gamePlayer != null) {
                gamePlayer.addDamageDealt(damageAmount);
                // BattleRoyale.LOGGER.debug("Player {} dealt {} damage. Total: {}", attackingPlayer.getName().getString(), damageAmount, gamePlayer.getDamageDealt());
            }
        }

        // TODO: 伤害数值调整逻辑可以在这里实现
        // 例如：event.setAmount(newAmount);
    }
}
