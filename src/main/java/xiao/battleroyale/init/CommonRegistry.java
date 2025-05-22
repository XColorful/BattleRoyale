package xiao.battleroyale.init;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.command.RootCommand;

@Mod.EventBusSubscriber
public class CommonRegistry {
    @SubscribeEvent
    public static void onServerStarting(RegisterCommandsEvent event) {
        RootCommand.register(event.getDispatcher());
    }
}
