package xiao.battleroyale.client.init;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.client.gui.LootSpawnerScreen;
import xiao.battleroyale.client.renderer.game.GameInfoRenderer;
import xiao.battleroyale.client.renderer.game.TeamInfoRenderer;
import xiao.battleroyale.client.renderer.game.ZoneRenderer;
import xiao.battleroyale.init.ModMenuTypes;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
public class ClientSetupEvent {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ZoneRenderer::register);
        event.enqueueWork(TeamInfoRenderer::register);
        event.enqueueWork(GameInfoRenderer::register);
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.LOOT_SPAWNER_MENU.get(), LootSpawnerScreen::new);
        });
    }
}