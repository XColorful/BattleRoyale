package xiao.battleroyale.compat.playerrevive;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;

import java.util.*;

public class BleedingHandler {

    private static class BleedingHandlerHolder {
        private static final BleedingHandler INSTANCE = new BleedingHandler();
    }

    public static BleedingHandler get() {
        return BleedingHandlerHolder.INSTANCE;
    }

    private BleedingHandler() {}

    private static boolean isRegistered = false;

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
        isRegistered = true;
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
        downTime.clear();
        bleedingPlayerData.clear();
        isRegistered = false;
    }

    private static final List<Float> bleedDamage = new ArrayList<>();
    public static void setBleedDamage(List<Float> damageList) {
        bleedDamage.clear();
        bleedDamage.addAll(damageList);
    }
    private static int BLEED_COOLDOWN = 20;
    public static void setBleedCooldown(int cooldown) { BLEED_COOLDOWN = Math.max(0, cooldown); }
    private static Map<UUID, Integer> downTime = new HashMap<>();
    private static Map<UUID, BleedData> bleedingPlayerData = new HashMap<>();

    public void addBleedingPlayer(@NotNull Player player) {
        if (!isRegistered) {
            register();
        }
        UUID playerUUID = player.getUUID();
        int currentDownTime = downTime.compute(playerUUID, (k, v) -> v == null ? 1 : v + 1);
        // 没有设定倒地扣血量就判定为超过最大倒地次数
        if (currentDownTime > bleedDamage.size()) {
            BattleRoyale.LOGGER.debug("Player {} has downed {} time, can't bleed and kill", player.getName().getString(), currentDownTime);
            PlayerRevive.get().kill(player);
            return;
        }
        float damage = bleedDamage.get(currentDownTime - 1);
        bleedingPlayerData.put(playerUUID, new BleedData(player, damage, BLEED_COOLDOWN));
        BattleRoyale.LOGGER.debug("Player {} has downed {} time, damage:{}, cooldown:{}", player.getName().getString(), currentDownTime, damage, BLEED_COOLDOWN);
    }

    @SubscribeEvent
    public void onPlayerBleeding(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!GameManager.get().isInGame()) {
            unregister();
            return;
        }

        bleedingPlayerData.entrySet().removeIf(data -> {
            BleedData bleedData = data.getValue();
            if (!bleedData.isStillValid()) {
                return false;
            }
            bleedData.bleed();
            return bleedData.isStillValid();
        });

        // kill完之后可能游戏就结束了
        if (!GameManager.get().isInGame()) {
            unregister();
        }
    }

    private static class BleedData {
        Player bleedPlayer;
        float damage;
        int damageCooldown;
        public BleedData(@NotNull Player player, float damage, int cooldown) {
            this.bleedPlayer = player;
            this.damage = damage;
            this.damageCooldown = cooldown;
        }
        // 调用时保证在游戏内
        public void bleed() {
            if (--damageCooldown <= 0) {
                float currentHealth = bleedPlayer.getHealth();
                if (currentHealth <= damage) { // 此次扣血会致死
                    PlayerRevive.get().kill(bleedPlayer);
                    return;
                } else {
                    bleedPlayer.setHealth(currentHealth - damage); // 不触发受击音效和事件，绝对地扣血
                }
                damageCooldown = BLEED_COOLDOWN;
            }
        }
        public boolean isStillValid() {
            return bleedPlayer == null
                    || PlayerRevive.get().isBleeding(bleedPlayer)
                    || !GameManager.get().hasStandingGamePlayer(bleedPlayer.getUUID());
        }
    }
}
