package xiao.battleroyale.compat.playerrevive;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.compat.AbstractCompatMod;

import java.lang.reflect.Method;

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
    private Method revive;
    private Method kill;


    @Override
    protected void onModLoaded() throws Exception {
        PlayerReviveServer = Class.forName("team.creative.playerrevive.server.PlayerReviveServer");
        revive = PlayerReviveServer.getMethod("revive", Player.class);
        kill = PlayerReviveServer.getMethod("kill", Player.class);
        isBleeding = PlayerReviveServer.getMethod("isBleeding", Player.class);
    }

    public boolean isBleeding(Player player) {
        if (!isLoaded()) {
            return false;
        }
        try {
            boolean bleeding = (boolean) isBleeding.invoke(null, player);
            BattleRoyale.LOGGER.debug("PlayerRevive: isBleeding {}: {}", player.getName().getString(), bleeding);
            return bleeding;
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
        BleedingHandler.get().addBleedingPlayer(player);
    }
}
