package xiao.battleroyale.compat.playerrevive;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.IServerTickEvent;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameMessageManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.compat.tacz.Tacz;
import xiao.battleroyale.event.EventRegister;

import java.util.*;

public class BleedingHandler implements IEventHandler {

    private static class BleedingHandlerHolder {
        private static final BleedingHandler INSTANCE = new BleedingHandler();
    }

    public static BleedingHandler get() {
        return BleedingHandlerHolder.INSTANCE;
    }

    private BleedingHandler() {}

    private static boolean isRegistered = false;
    public static boolean isIsRegistered() { return isRegistered; }

    @Override
    public String getEventHandlerName() {
        return "BleedingHandler";
    }

    public static void register() {
        if (EventRegister.register(get(), EventType.SERVER_TICK_EVENT)) {
            isRegistered = true;
            Tacz.registerBleedingEvent();
        } else {
            BattleRoyale.LOGGER.warn("BleedingHandler failed to register instantly");
        }
    }

    public static void unregister() {
        EventRegister.unregister(get(), EventType.SERVER_TICK_EVENT);
        isRegistered = false;
        Tacz.unregisterBleedingEvent();
    }

    private static final List<Float> bleedDamage = new ArrayList<>();
    public static void setBleedDamage(List<Float> damageList) {
        bleedDamage.clear();
        bleedDamage.addAll(damageList);
    }
    private static int BLEED_COOLDOWN = 20;
    public static void setBleedCooldown(int cooldown) { BLEED_COOLDOWN = Math.max(0, cooldown); }
    private static final Map<UUID, Integer> downTime = new HashMap<>();
    private static final Map<UUID, BleedData> bleedingPlayerData = new HashMap<>();

    public void addBleedingPlayer(@NotNull Player player) {
        if (!isRegistered) {
            register();
        }
        BattleRoyale.LOGGER.debug("GameTime:{} addBleedingPlayer", GameManager.get().getGameTime());
        UUID playerUUID = player.getUUID();
        GamePlayer gamePlayer = GameTeamManager.getGamePlayerByUUID(playerUUID);
        if (gamePlayer == null) {
            BattleRoyale.LOGGER.warn("Attempt to add a non GamePlayer {} (UUID:{}) to bleeding player, skipped", player.getName().getString(), playerUUID);
            return;
        }
        int currentDownTime = downTime.compute(playerUUID, (k, v) -> v == null ? 1 : v + 1);
        // 没有设定倒地扣血量就判定为超过最大倒地次数
        if (currentDownTime > bleedDamage.size()) {
            BattleRoyale.LOGGER.debug("Player {} has downed {} time, can't bleed and kill", player.getName().getString(), currentDownTime);
            GameManager.get().onPlayerDeath(gamePlayer, null);
            PlayerRevive.get().kill(player);
            GameMessageManager.notifyTeamChange(gamePlayer.getGameTeamId());
            return;
        }
        float damage = bleedDamage.get(currentDownTime - 1);
        bleedingPlayerData.put(playerUUID, new BleedData(player, gamePlayer, damage, BLEED_COOLDOWN));
        BattleRoyale.LOGGER.debug("Player {} (UUID:{}) has downed {} time, damage:{}, cooldown:{}", player.getName().getString(), player.getUUID(), currentDownTime, damage, BLEED_COOLDOWN);
        Tacz.onAddingBleedingPlayer(player);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.SERVER_TICK_EVENT) {
            onPlayerBleeding((IServerTickEvent) event);
        } else {
            BattleRoyale.LOGGER.warn("{} received wrong event type: {}", getEventHandlerName(), eventType);
        }
    }

    public void onPlayerBleeding(IServerTickEvent event) {
        // 照理说复活事件+倒地事件应该不会一起发生在这里遍历的过程中
        // 虽然没有监听PlayerRevive的复活事件，但是PlayerRevive处理复活的逻辑是在PlayerTickEvent上的，最多晚1tick检测到
        bleedingPlayerData.entrySet().removeIf(data -> {
            BleedData bleedData = data.getValue();
            if (bleedData.isRevived()) { // 被救起
                GameManager.get().onPlayerRevived(bleedData.gamePlayer);
                return true;
            }
            if (!bleedData.isBleeding()) {
                return true;
            }
            bleedData.bleed();
            return !bleedData.isBleeding();
        });

        if (bleedingPlayerData.isEmpty()) {
            unregister();
        }
    }

    private static class BleedData {
        Player bleedPlayer;
        GamePlayer gamePlayer;
        float damage;
        int damageCooldown;
        public BleedData(@NotNull Player player, @NotNull GamePlayer gamePlayer, float damage, int cooldown) {
            this.bleedPlayer = player;
            this.gamePlayer = gamePlayer;
            this.damage = damage;
            this.damageCooldown = cooldown;
        }
        // 调用时保证在游戏内
        public void bleed() {
            // 如果有队友在扶就停止计时，但不重置流血进度
            if (!PlayerRevive.get().getRevivingPlayers(bleedPlayer).isEmpty()) {
                return;
            }
            // 流血流程
            if (--damageCooldown <= 0) {
                float currentHealth = bleedPlayer.getHealth();
                if (currentHealth <= damage) { // 此次扣血会致死
                    BattleRoyale.LOGGER.debug("Bleed damage will kill game player {}", gamePlayer.getPlayerName());
                    GameManager.get().onPlayerDeath(gamePlayer, null);
                    PlayerRevive.get().kill(bleedPlayer);
                    GameMessageManager.notifyTeamChange(gamePlayer.getGameTeamId());
                    return;
                } else {
                    if (bleedPlayer != null) {
                        bleedPlayer.setHealth(currentHealth - damage); // 不触发受击音效和事件，绝对地扣血
                        GameMessageManager.notifyTeamChange(gamePlayer.getGameTeamId());
                    } else {
                        BattleRoyale.LOGGER.debug("bleedPlayer is null, GamePlayer {} (UUID:{}) skipped bleed damage", gamePlayer.getPlayerName(), gamePlayer.getPlayerUUID());
                    }
                }
                damageCooldown = BLEED_COOLDOWN;
            }
        }
        public boolean isRevived() {
            return bleedPlayer != null
                    && !PlayerRevive.get().isBleeding(bleedPlayer) // 没有该流血玩家
                    && bleedPlayer.getHealth() > 0
                    && GameTeamManager.hasStandingGamePlayer(bleedPlayer.getUUID());
        }
        public boolean isBleeding() {
            return bleedPlayer != null
                    && PlayerRevive.get().isBleeding(bleedPlayer)
                    && bleedPlayer.getHealth() > 0;
        }
    }

    public void clear() {
        downTime.clear();
        bleedingPlayerData.clear();
    }
}
