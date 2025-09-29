package xiao.battleroyale.compat.forge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import xiao.battleroyale.api.network.INetworkHook;

import java.util.function.Consumer;

public class ForgeNetworkHook implements INetworkHook {

    @Override
    public void openScreen(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter) {
        player.openMenu(containerSupplier, extraDataWriter);
    }
}
