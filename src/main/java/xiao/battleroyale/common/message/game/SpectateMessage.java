package xiao.battleroyale.common.message.game;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.message.AbstractCommonMessage;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import static xiao.battleroyale.common.message.game.SpectateMessageManager.SPECTATE_KEY;

public class SpectateMessage extends AbstractCommonMessage {

    public int sendCooldown = 0;
    private final Queue<GamePlayer> spectateGamePlayers = new ArrayDeque<>();

    public SpectateMessage(@NotNull CompoundTag nbt, int updateTime, List<GamePlayer> gamePlayers) {
        super(nbt, updateTime);
        this.spectateGamePlayers.addAll(gamePlayers);
    }

    @NotNull
    public CompoundTag toNBT(int playerTotal) {
        CompoundTag nbt = new CompoundTag();
        if (spectateGamePlayers.isEmpty()) {
            return nbt;
        }

        CompoundTag spectateTag = new CompoundTag();
        playerTotal = Math.min(playerTotal, spectateGamePlayers.size());
        for (int i = 0; i < playerTotal; i++) {
            GamePlayer gamePlayer = spectateGamePlayers.poll();
            if (gamePlayer != null) { // 防御性编程，尽管不会触发
                spectateTag.putString(gamePlayer.getPlayerUUID().toString(), gamePlayer.getGameTeamColor());
            } else {
                BattleRoyale.LOGGER.warn("SpectateMessage get null GamePlayer from this.spectateGamePlayers, this is unexpected");
            }
        }
        nbt.put(SPECTATE_KEY, spectateTag);

        return nbt;
    }

    public boolean isFinished() {
        return spectateGamePlayers.isEmpty();
    }
}
