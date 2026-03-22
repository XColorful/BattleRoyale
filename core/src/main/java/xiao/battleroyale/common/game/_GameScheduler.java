package xiao.battleroyale.common.game;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.DelayedEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.util.ChatUtils;

import java.util.function.Consumer;

public class _GameScheduler {

    protected static void delayedInitAfterGame(IGameManager gameManager) {
        // 游戏结束后自动初始化下一局游戏
        if (gameManager.getGameEntry().initGameAfterGame) {
            @Nullable ServerLevel serverLevel = gameManager.getServerLevel();
            if (gameManager.hasWinner() && serverLevel != null) { // 游戏正常结束
                // 延迟1tick初始化游戏
                ResourceKey<Level> cachedGameLevelKey = serverLevel.dimension();
                Consumer<ResourceKey<Level>> delayedTask = levelKey -> {
                    ServerLevel currentServerLevel = BattleRoyale.getMinecraftServer().getLevel(levelKey);
                    if (currentServerLevel != null) {
                        BattleRoyale.getGameManager().initGame(currentServerLevel);
                    }
                };
                new DelayedEvent<>(delayedTask, cachedGameLevelKey, 1, "GameScheduler::delayedInitAfterGame");
            }
        }
    }

    protected static void delayedRestartAfterGame(IGameManager gameManager) {
        GameEntry gameEntry = gameManager.getGameEntry();
        int remainRestart = gameManager.getRemainRestartTime();
        if (gameEntry.restartAfterGame && gameManager.hasWinner() && remainRestart > 0) {
            @Nullable ServerLevel serverLevel = gameManager.getServerLevel();
            if (serverLevel != null) {
                // 延迟重开游戏
                int delay = Math.max(GameManager.MIN_RESTART_DELAY, gameEntry.restartDelay);
                ResourceKey<Level> cachedGameLevelKey = serverLevel.dimension();
                Consumer<ResourceKey<Level>> delayedTask = levelKey -> {
                    ServerLevel currentServerLevel = BattleRoyale.getMinecraftServer().getLevel(levelKey);
                    if (currentServerLevel != null) {
                        BattleRoyale.getGameManager().startGame(currentServerLevel);
                    }
                };
                new DelayedEvent<>(delayedTask, cachedGameLevelKey, delay, "GameScheduler::delayedRestartAfterGame");
                gameManager.setRemainRestartTime(remainRestart - 1);
                ChatUtils.sendComponentMessageToAllPlayers(serverLevel,
                        Component.translatable("battleroyale.message.auto_restart_after_game", delay / 20)
                                .append(Component.literal(" ("))
                                .append(Component.translatable("battleroyale.message.remain"))
                                .append(Component.literal(": "))
                                .append(Component.literal(String.valueOf(gameManager.getRemainRestartTime())).withStyle(ChatFormatting.AQUA))
                                .append(Component.literal("/" + gameEntry.maxRestartRound + ")"))
                );
                return;
            }
        }
        // 未提前返回则重置次数
        gameManager.setRemainRestartTime(gameEntry.maxRestartRound);
    }
}
