package xiao.battleroyale.compat.neoforge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import xiao.battleroyale.api.network.INetworkHook;

import java.util.function.Consumer;

public class NeoNetworkHook implements INetworkHook {

    @Override
    public void openScreen(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter) {
        Consumer<RegistryFriendlyByteBuf> registryAdapter = (RegistryFriendlyByteBuf registryBuf) -> {
            extraDataWriter.accept((FriendlyByteBuf) registryBuf);
        };

        player.openMenu(containerSupplier, registryAdapter);
    }
}