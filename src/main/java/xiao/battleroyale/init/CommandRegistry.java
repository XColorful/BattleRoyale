package xiao.battleroyale.init;

import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.command.ClientCommand;
import xiao.battleroyale.command.ServerCommand;
import xiao.battleroyale.compat.pubgmc.PubgmcCommand;

@Mod.EventBusSubscriber
public class CommandRegistry {

    @SubscribeEvent
    public static void onServerStarting(RegisterCommandsEvent event) {
        ServerCommand.register(event.getDispatcher());
        PubgmcCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onClientStarting(RegisterClientCommandsEvent event) {
        ClientCommand.register(event.getDispatcher());
    }
}