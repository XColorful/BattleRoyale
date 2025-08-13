package xiao.battleroyale.compat.playerrevive;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.compat.AbstractCompatMod;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class PlayerRevive extends AbstractCompatMod {

    @Override
    public String getModId() { return "playerrevive"; }

    private static class PlayerReviveHolder {
        private static final PlayerRevive INSTANCE = new PlayerRevive();
    }

    public static PlayerRevive get() {
        return PlayerReviveHolder.INSTANCE;
    }

    private PlayerRevive() {}

    private Class<?> PlayerReviveServer;
    private Method isBleeding;
    private Method getBleeding;
    private Method revive;
    private Method kill;
    private Class<?> IBleeding;
    private Method revivingPlayers;
    private Class<?> PlayerRevivedEvent; // 事件不方便监听，CompileOnly好像没必要


    @Override
    protected void onModLoaded() throws Exception {
        PlayerReviveServer = Class.forName("team.creative.playerrevive.server.PlayerReviveServer");
        isBleeding = PlayerReviveServer.getMethod("isBleeding", Player.class);
        getBleeding = PlayerReviveServer.getMethod("getBleeding", Player.class);
        revive = PlayerReviveServer.getMethod("revive", Player.class);
        kill = PlayerReviveServer.getMethod("kill", Player.class);
        IBleeding = Class.forName("team.creative.playerrevive.api.IBleeding");
        revivingPlayers = IBleeding.getMethod("revivingPlayers");
    }

    public boolean isBleeding(Player player) {
        if (!isLoaded()) {
            return false;
        }
        try {
            return (boolean) isBleeding.invoke(null, player);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("PlayerRevive: Failed to isBleeding {}: {}", player.getName().getString(), e.getMessage());
        }
        return false;
    }

    public void revive(Player player) {
        if (!isLoaded()) {
            return;
        }
        try {
            revive.invoke(null, player); // static 方法，第一个参数传 null
            BattleRoyale.LOGGER.debug("PlayerRevive: Revive {}", player.getName().getString());
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("PlayerRevive: Failed to revive {}: {}", player.getName().getString(), e.getMessage());
        }
    }

    public void kill(Player player) {
        if (!isLoaded()) {
            return;
        }
        try {
            kill.invoke(null, player);
            BattleRoyale.LOGGER.debug("PlayerRevive: Kill {}", player.getName().getString());
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("PlayerRevive: Failed to kill {}: {}", player.getName().getString(), e.getMessage());
        }
    }

    public void addBleedingPlayer(@NotNull Player player) {
        if (!isLoaded()) {
            return;
        }
        BleedingHandler.get().addBleedingPlayer(player);
    }

    /**
     * 获取正在帮忙扶起的玩家列表
     * 返回空列表即无帮忙玩家
     */
    public List<Player> getRevivingPlayers(Player player) {
        if (!isLoaded()) {
            return Collections.emptyList();
        }
        try {
            Object bleedingInstance = getBleeding.invoke(null, player);
            if (bleedingInstance != null) {
                Object result = revivingPlayers.invoke(bleedingInstance);
                if (result instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Player> players = (List<Player>) result;
                    return players;
                } else {
                    BattleRoyale.LOGGER.error("PlayerRevive: revivingPlayers returned a non-List object for player {}.", player.getName().getString());
                }
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("PlayerRevive: Failed to IBleeding.revivePlayers {}: {}", player.getName().getString(), e.getMessage());
        }
        return Collections.emptyList();
    }
}
