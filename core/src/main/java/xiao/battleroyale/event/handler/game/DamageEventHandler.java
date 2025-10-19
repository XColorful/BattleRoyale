package xiao.battleroyale.event.handler.game;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameMessageManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.compat.playerrevive.PlayerRevive;
import xiao.battleroyale.event.EventRegister;
import xiao.battleroyale.util.ChatUtils;

/**
 * 伤害数值调整
 */
public class DamageEventHandler implements IEventHandler {

    private DamageEventHandler() {}

    private static class DamageEventHandlerHolder {
        private static final DamageEventHandler INSTANCE = new DamageEventHandler();
    }

    public static DamageEventHandler get() {
        return DamageEventHandlerHolder.INSTANCE;
    }

    @Override public String getEventHandlerName() {
        return "DamageEventHandler";
    }

    public static void register() {
        EventRegister.register(get(), EventType.LIVING_DAMAGE_EVENT, EventPriority.HIGH, false);
    }

    public static void unregister() {
        EventRegister.unregister(get(), EventType.LIVING_DAMAGE_EVENT, EventPriority.HIGH, false);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.LIVING_DAMAGE_EVENT) {
            checkDamage((ILivingDamageEvent) event);
        } else {
            BattleRoyale.LOGGER.warn("{} received wrong event type: {}", getEventHandlerName(), eventType);
        }
    }

    /**
     * 监听实体受到伤害事件
     * 取消存活游戏玩家与非存活游戏玩家之间的伤害
     * 通知队伍更新成员信息
     * @param event 实体受到伤害事件
     */
    private void checkDamage(ILivingDamageEvent event) {
        LivingEntity damagedEntity = event.getEntity(); // 被攻击方
        DamageSource damageSource = event.getSource(); // 攻击方

        GameManager gameManager = GameManager.get();

        GamePlayer targetGamePlayer = GameTeamManager.hasStandingGamePlayer(damagedEntity.getUUID()) ? GameTeamManager.getGamePlayerByUUID(damagedEntity.getUUID()) : null;
        if (targetGamePlayer != null && targetGamePlayer.isEliminated()) {
            targetGamePlayer = null;
        }
        GamePlayer attackerGamePlayer = null;
        if (damageSource.getEntity() instanceof LivingEntity attackerEntity) {
            attackerGamePlayer = GameTeamManager.hasStandingGamePlayer(attackerEntity.getUUID()) ? GameTeamManager.getGamePlayerByUUID(attackerEntity.getUUID()) : null;
            if (attackerGamePlayer != null && attackerGamePlayer.isEliminated()) {
                attackerGamePlayer = null;
            }
        }

        // 游戏玩家之间的伤害
        if (attackerGamePlayer != null && targetGamePlayer != null) {
            // 如果双方在同一队伍，且友伤关闭，则取消伤害
            if (attackerGamePlayer.getGameTeamId() == targetGamePlayer.getGameTeamId()) {
                if (!gameManager.getGameEntry().friendlyFire) {
                    event.setCanceled(true);
                }
            }
            if (!gameManager.getGameEntry().downFire) {
                if (damageSource.getEntity() instanceof ServerPlayer attackPlayer
                        && PlayerRevive.get().isBleeding(attackPlayer)) {
                    ChatUtils.sendComponentMessageToPlayer(attackPlayer, Component.translatable("battleroyale.message.down_fire_not_enabled").withStyle(ChatFormatting.RED));
                    event.setCanceled(true);
                }
            }
            // 通知队伍更新成员信息
            GameMessageManager.notifyTeamChange(targetGamePlayer.getGameTeamId());
        }
        // 游戏玩家攻击非游戏玩家
        else if (attackerGamePlayer != null) {
//            if (damageSource instanceof LivingEntity livingEntity) {
//                ;
//            }
            event.setCanceled(true);
            if (damagedEntity instanceof ServerPlayer interfererPlayer) {
                // 把不参与游戏的玩家tp回大厅
                if (gameManager.getGameEntry().teleportInterfererToLobby
                        && damageSource.getEntity() instanceof ServerPlayer) {
                    SpawnManager.get().teleportToLobby(interfererPlayer);
                    ServerLevel serverLevel = gameManager.getServerLevel();
                    if (serverLevel != null) {
                        ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.teleport_non_game_player_to_lobby", interfererPlayer.getName().getString());
                        gameManager.sendGameSpectateMessage(interfererPlayer); // 提供观战指令
                    }
                }
            }
        }
        // 非游戏玩家攻击游戏玩家
        else if (targetGamePlayer != null) {
            if (damageSource.getEntity() instanceof LivingEntity livingEntity) {
                event.setCanceled(true);
                if (livingEntity instanceof ServerPlayer interfererPlayer) {
                    // 把不参与游戏的玩家tp回大厅
                    if (gameManager.getGameEntry().teleportInterfererToLobby) {
                        SpawnManager.get().teleportToLobby(interfererPlayer);
                        ServerLevel serverLevel = gameManager.getServerLevel();
                        if (serverLevel != null) {
                            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.teleport_non_game_player_to_lobby", interfererPlayer.getName().getString());
                            gameManager.sendGameSpectateMessage(interfererPlayer); // 提供观战指令
                        }
                    }
                }
            }
        }
        // 非游戏玩家打非游戏玩家
    }
}