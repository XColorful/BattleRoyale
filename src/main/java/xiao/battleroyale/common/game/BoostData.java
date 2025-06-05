package xiao.battleroyale.common.game;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BoostData extends AbstractGameManagerData {

    private static final String DATA_NAME = "BoostData";
    private final List<Integer> boostPlayerIdList = new ArrayList<>(); // 大概没必要用链表，一个止痛药就要180秒才删一次
    private final Map<Integer, Integer> boostGamePlayerSyncCooldown = new ConcurrentHashMap<>(); // playerId -> syncCooldown

    private static final int SYNC_FREQUENCY = 20;
    private static final int HEAL_COOLDOWN = 160;
    private static final int EFFECT_COOLDOWN = 20;

    public BoostData() {
        super(DATA_NAME);
    }

    @Override
    public void clear() {
        boostPlayerIdList.clear();
        boostGamePlayerSyncCooldown.clear();
    }

    @Override
    public void startGame() {
        if (locked) {
            return;
        }

        clear();
        lockData();
    }

    /**
     * 消耗boost，给予效果
     */
    public void onGameTick(int gameTime) {
        if (!locked) {
            return;
        }

        Iterator<Integer> iterator = boostPlayerIdList.iterator();
        while (iterator.hasNext()) {
            int playerId = iterator.next();
            GamePlayer gamePlayer = GameManager.get().getGamePlayerBySingleId(playerId);
            if (gamePlayer == null || gamePlayer.isEliminated() || !gamePlayer.isAlive()) {
                iterator.remove();
                boostGamePlayerSyncCooldown.remove(playerId);
                if (gamePlayer != null) {
                    gamePlayer.resetBoost();
                    GameManager.get().addChangedTeamInfo(gamePlayer.getGameTeamId());
                }
                continue;
            }

            int boost = gamePlayer.getBoost();
            int boostLevel = GamePlayer.getBoostLevel(boost);
            int syncCooldown = boostGamePlayerSyncCooldown.get(playerId);
            if (syncCooldown <= 1) {
                GameManager.get().addChangedTeamInfo(gamePlayer.getGameTeamId());
                boostGamePlayerSyncCooldown.put(playerId, SYNC_FREQUENCY);
            } else {
                boostGamePlayerSyncCooldown.put(playerId, syncCooldown - 1);
            }
            gamePlayer.dropBoost();

            ServerLevel serverLevel = GameManager.get().getServerLevel();
            // 回血
            if (gamePlayer.getHealCooldown() <= 0) {
                if (gamePlayer.isActiveEntity() && serverLevel != null) { // isActiveEntity是GameManager已经预处理好ServerPlayer是否为null
                    ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                    if (player != null) {
                        double healAmount = 0;
                        switch (boostLevel) {
                            case 4 -> { healAmount = 0.8f; }
                            case 3 -> { healAmount = 0.6f; }
                            case 2 -> { healAmount = 0.4f; }
                            case 1 -> { healAmount = 0.2f; }
                        }
                        player.heal((float) healAmount);
                    }
                }
                gamePlayer.addHealCooldown(HEAL_COOLDOWN);
            }
            gamePlayer.dropHealCooldown();
            // 速度效果
            if (gamePlayer.getEffectCooldown() <= 0 && boostLevel >= 3) {
                if (gamePlayer.isActiveEntity() && serverLevel != null) { // isActiveEntity是GameManager已经预处理好ServerPlayer是否为null
                    ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                    if (player != null) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, EFFECT_COOLDOWN, boostLevel > 3 ? 1 : 0, false, false));
                    }
                }
                gamePlayer.addEffectCooldown(EFFECT_COOLDOWN);
            }
            gamePlayer.dropEffectCooldown();

            if (gamePlayer.getBoost() <= 0) {
                iterator.remove();
                boostGamePlayerSyncCooldown.remove(playerId);
                gamePlayer.resetBoost();
                GameManager.get().addChangedTeamInfo(gamePlayer.getGameTeamId()); // 立即通知队伍成员更新队伍HUD
            }
        }
    }

    /**
     * 为GamePlayer添加能量条，不立即通知更新队伍HUD
     * 通常是使用物品时触发，立即更新冷却
     */
    public void addBoost(int amount, GamePlayer gamePlayer) {
        if (!locked) {
            return;
        }
        // 添加到维护的列表里，用于onGameTick
        int playerId = gamePlayer.getGameSingleId();
        if (!boostGamePlayerSyncCooldown.containsKey(playerId)) {
            boostGamePlayerSyncCooldown.put(playerId, 0);
            boostPlayerIdList.add(playerId);
        }
        // 提升等级则立即重置冷却
        int preLevel = GamePlayer.getBoostLevel(gamePlayer.getBoost());
        gamePlayer.addBoost(amount);
        int curLevel = GamePlayer.getBoostLevel(gamePlayer.getBoost());
        if (preLevel < curLevel) {
            gamePlayer.setHealCooldown(0);
            gamePlayer.setEffectCooldown(0);
        }
    }

    @Override
    public void endGame() {
        if (!locked) {
            return;
        }

        unlockData();
        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel == null) {
            return;
        }

        for (GameTeam gameTeam : GameManager.get().getGameTeams()) {
            boolean shouldSync = false;
            for (GamePlayer gamePlayer : gameTeam.getTeamMembers()) {
                int preBoost = gamePlayer.getBoost();
                gamePlayer.resetBoost();
                int curBoost = gamePlayer.getBoost();
                if (preBoost == curBoost) {
                    continue;
                }

                if (gamePlayer.isBot() || !gamePlayer.isActiveEntity()) {
                    continue;
                }
                ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                if (player != null) {
                    shouldSync = true;
                }
            }
            if (shouldSync) {
                GameManager.get().addChangedTeamInfo(gameTeam.getGameTeamId());
            }
        }
    }
}