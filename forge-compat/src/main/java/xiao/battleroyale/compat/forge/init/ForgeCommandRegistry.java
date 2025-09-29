package xiao.battleroyale.compat.forge.init;

import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.init.CommandRegistry;

@Mod.EventBusSubscriber
public class ForgeCommandRegistry {

    private static final CommandRegistry COMMAND_REGISTRY = CommandRegistry.get();

    @SubscribeEvent
    public static void onServerCommandsRegister(RegisterCommandsEvent event) {
        COMMAND_REGISTRY.registerServerCommands(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onClientCommandsRegister(RegisterClientCommandsEvent event) {
        COMMAND_REGISTRY.registerClientCommands(event.getDispatcher());
    }
}
