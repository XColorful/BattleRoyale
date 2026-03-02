package xiao.battleroyale.client.init;

import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.init.IClientModEvent;

public class ClientModEvent implements IClientModEvent {

    private static final ClientModEvent INSTANCE = new ClientModEvent();

    public static ClientModEvent get() {
        return INSTANCE;
    }

    @Override
    public void onClientPlayerLoggingOut(@Nullable LocalPlayer localPlayer) {
        if (BattleRoyale.getMcSide().isClientSide()) {
            BattleRoyale.getClientGameDataManager().clear();
        }
    }
}
