package xiao.battleroyale.client.init;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.client.gui.LootSpawnerScreen;
import xiao.battleroyale.init.ModMenuTypes;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
public class ClientSetupEvent {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(ModMenuTypes.LOOT_SPAWNER_MENU.get(), LootSpawnerScreen::new);

        // 注册其他客户端相关的事件监听器或初始化
        // 如渲染器注册、键盘绑定注册等
    }
}