package xiao.battleroyale.api.event.client;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.client.game.IClientGameDataManager;
import xiao.battleroyale.api.event.CustomEvent;

public abstract class AbstractClientEvent extends CustomEvent {

    protected final IClientGameDataManager clientGameDataManager;

    public AbstractClientEvent(IClientGameDataManager clientGameDataManager) {
        this.clientGameDataManager = clientGameDataManager;
    }

    public IClientGameDataManager getClientGameDataManager() {
        return clientGameDataManager;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        return null;
    }
}
