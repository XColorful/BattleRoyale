package xiao.battleroyale.compat.journeymap;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.BattleRoyale;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;

import static journeymap.client.api.event.ClientEvent.Type.*;

@ParametersAreNonnullByDefault
@journeymap.client.api.ClientPlugin
public class JourneyMapPlugin implements IClientPlugin {

    // API reference
    private IClientAPI jmAPI = null;
    // Forge listener reference
    private static ForgeEventListener forgeEventListener;

    public static final String MOD_ID = BattleRoyale.MOD_ID;

    private static JourneyMapPlugin INSTANCE;

    public JourneyMapPlugin()
    {
        INSTANCE = this;
    }

    public static JourneyMapPlugin getInstance() {
        return INSTANCE;
    }

    protected static boolean registered = false;

    public static void register() {
        if (!registered && forgeEventListener != null) {
            MinecraftForge.EVENT_BUS.register(forgeEventListener);
            BattleRoyale.LOGGER.debug("Registered JourneyMapPlugin");
        }
    }

    public static void unregister() {
        if (forgeEventListener != null) {
            MinecraftForge.EVENT_BUS.unregister(forgeEventListener);
            forgeEventListener.jmAPI.removeAll(JourneyMapPlugin.MOD_ID);
            BattleRoyale.LOGGER.debug("Unregistered JourneyMapPlugin");
        }
    }

    /**
     * Called by JourneyMap during the init phase of mod loading.  The IClientAPI reference is how the mod
     * will add overlays, etc. to JourneyMap.
     *
     * @param jmAPI Client API implementation
     */
    @Override
    public void initialize(IClientAPI jmAPI) {
        BattleRoyale.LOGGER.debug("initialize JourneyMapPlugin");
        this.jmAPI = jmAPI;
        forgeEventListener = new ForgeEventListener(jmAPI);
        register();

        this.jmAPI.subscribe(getModId(), EnumSet.of(DISPLAY_UPDATE, MAPPING_STARTED, MAPPING_STOPPED));

        BattleRoyale.LOGGER.info("Initialized {}", getClass().getName());
    }

    /**
     * Used by JourneyMap to associate a modId with this plugin.
     */
    @Override
    public String getModId() {
        return MOD_ID;
    }

    /**
     * Called by JourneyMap on the main Minecraft thread when a {@link journeymap.client.api.event.ClientEvent} occurs.
     * Be careful to minimize the time spent in this method so you don't lag the game.
     * <p>
     * You must call {@link IClientAPI#subscribe(String, EnumSet)} at some point to subscribe to these events, otherwise this
     * method will never be called.
     * <p>
     * If the event type is {@link journeymap.client.api.event.ClientEvent.Type#DISPLAY_UPDATE},
     * this is a signal to {@link journeymap.client.api.IClientAPI#show(journeymap.client.api.display.Displayable)}
     * all relevant Displayables for the {@link journeymap.client.api.event.ClientEvent#dimension} indicated.
     * (Note: ModWaypoints with persisted==true will already be shown.)
     *
     * @param event the event
     */
    @Override
    public void onEvent(ClientEvent event) {
        BattleRoyale.LOGGER.debug("Not registered but triggered event");
        try {
            switch (event.type) {
                case DISPLAY_UPDATE, // 这个事件并不会实时更新小地图，绘制放在ClientTickEvent里
                     MAPPING_STARTED: // 刚进游戏时触发
                    ShapeDrawer.cachedDimension = event.dimension;
                    break;
                case MAPPING_STOPPED: // 退出游戏时触发
                    onMappingStopped(event);
                    break;
            }
        } catch (Throwable t) {
            BattleRoyale.LOGGER.error(t.getMessage(), t);
        }
    }

    void onMappingStopped(ClientEvent event) {
        jmAPI.removeAll(MOD_ID);
    }
}