package xiao.battleroyale.compat.neoforge.init;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.init.CommandRegistry;

@EventBusSubscriber(modid = BattleRoyale.MOD_ID)
public class NeoCommandRegistry {

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