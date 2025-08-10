package xiao.battleroyale.compat.journeymap;

import journeymap.client.api.IClientAPI;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.client.game.ClientGameDataManager;

public class ForgeEventListener {

    IClientAPI jmAPI;

    ForgeEventListener(IClientAPI jmAPI) {
        this.jmAPI = jmAPI;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (!ClientGameDataManager.get().getGameData().inGame()) { // 不在游戏中
                if (!ShapeDrawer.isCleared) {
                    jmAPI.removeAll(JourneyMapPlugin.MOD_ID);
                    ShapeDrawer.isCleared = true;
                }
            } else { // 在游戏中
                ResourceKey<Level> dimension = ShapeDrawer.cachedDimension;
                if (dimension == null) {
                    return;
                }
                ShapeDrawer.onMappingStarted(dimension, jmAPI);
            }

        }
    }
}
