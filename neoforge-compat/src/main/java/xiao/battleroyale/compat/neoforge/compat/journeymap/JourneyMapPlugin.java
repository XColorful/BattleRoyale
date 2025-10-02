package xiao.battleroyale.compat.neoforge.compat.journeymap;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.compat.journeymap.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;

import static journeymap.client.api.event.ClientEvent.Type.*;

@ParametersAreNonnullByDefault
@journeymap.client.api.ClientPlugin
public class JourneyMapPlugin implements IClientPlugin {

    // API reference
    private IClientAPI jmAPI = null;

    private static JourneyMapPlugin INSTANCE;

    public JourneyMapPlugin() {
        INSTANCE = this;
    }

    public static JourneyMapPlugin getInstance() {
        return INSTANCE;
    }

    /**
     * Called by JourneyMap during the init phase of mod loading. The IClientAPI reference is how the mod
     * will add overlays, etc. to JourneyMap.
     *
     * @param jmAPI Client API implementation
     */
    @Override
    public void initialize(IClientAPI jmAPI) {
        BattleRoyale.LOGGER.debug("initialize JourneyMapPlugin");
        this.jmAPI = jmAPI;
        this.jmAPI.subscribe(getModId(), EnumSet.of(DISPLAY_UPDATE, MAPPING_STARTED, MAPPING_STOPPED));
        JourneyMap.register();
        JmApi.initialized = true;
        BattleRoyale.LOGGER.info("Initialized {}", getClass().getName());
    }

    /**
     * Used by JourneyMap to associate a modId with this plugin.
     */
    @Override
    public String getModId() {
        return JMEventHandler.MOD_JM_ID;
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
        try {
            switch (event.type) {
                case DISPLAY_UPDATE, // 这个事件并不会实时更新小地图，绘制放在ClientTickEvent里
                     MAPPING_STARTED: // 刚进游戏时触发
                    JMShapeDrawer.cachedDimension = event.dimension;
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
        jmAPI.removeAll(JMEventHandler.MOD_JM_ID);
    }

    public void removeAll(String modId) {
        jmAPI.removeAll(modId);
    }

    public void show(JMPolygonOverlay JMPolygonOverlay) {
        JMShapeProperties jmShapeProperties = JMPolygonOverlay.JMShapeProperties();
        ShapeProperties shapeProperties = new ShapeProperties()
                .setFillColor(jmShapeProperties.fillColorInt())
                .setFillOpacity(jmShapeProperties.fillOpacity())
                .setStrokeColor(jmShapeProperties.strokeColorInt())
                .setStrokeOpacity(jmShapeProperties.strokeOpacity())
                .setStrokeWidth(jmShapeProperties.strokeWidth());

        MapPolygon mapPolygon = new MapPolygon(JMPolygonOverlay.JMMapPolygon().points());

        PolygonOverlay polygonOverlay = new PolygonOverlay(
                JMPolygonOverlay.modId(),
                JMPolygonOverlay.displayId(),
                JMPolygonOverlay.dimension(),
                shapeProperties,
                mapPolygon);
        try {
            jmAPI.show(polygonOverlay);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to draw filled polygon on JourneyMap: {}", e.getMessage(), e);
        }
    }
}