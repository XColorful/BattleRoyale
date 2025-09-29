package xiao.battleroyale.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import xiao.battleroyale.api.network.INetworkHook;

import java.util.function.Consumer;

public class NetworkHook implements INetworkHook {

    private static NetworkHook INSTANCE;
    private final INetworkHook networkHook;

    private NetworkHook(INetworkHook networkHook) {
        this.networkHook = networkHook;
    }

    public static void initialize(INetworkHook networkHook) {
        if (INSTANCE != null){
            throw new IllegalStateException("NetworkHook already initialized.");
        }
        INSTANCE = new NetworkHook(networkHook);
    }

    public static NetworkHook get() {
        if (INSTANCE == null) {
            throw new IllegalStateException("NetworkHook not initialized. Call initialize() first.");
        }
        return INSTANCE;
    }

    @Override
    public void openScreen(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter) {
        this.networkHook.openScreen(player, containerSupplier, extraDataWriter);
    }
}
