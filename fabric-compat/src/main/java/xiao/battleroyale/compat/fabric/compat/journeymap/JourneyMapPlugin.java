package xiao.battleroyale.compat.fabric.compat.journeymap;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.compat.journeymap.*;

import java.util.EnumSet;

import static journeymap.client.api.event.ClientEvent.Type.*;

@journeymap.client.api.ClientPlugin
public class JourneyMapPlugin implements IClientPlugin {

    private IClientAPI jmAPI = null;
    private static JourneyMapPlugin INSTANCE;

    public JourneyMapPlugin() {
        INSTANCE = this;
    }

    public static JourneyMapPlugin getInstance() {
        return INSTANCE;
    }

    /**
     * Called by JourneyMap during the init phase of mod loading.  Your implementation
     * should retain a reference to the IClientAPI passed in, since that is what your plugin
     * will use to add overlays, etc. to JourneyMap.
     * <p>
     * This is also a good time to call {@link IClientAPI#subscribe(String, EnumSet)} to subscribe to any
     * desired ClientEvent types.
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
     * Used by JourneyMap to associate your mod id with your plugin instance.
     */
    @Override
    public String getModId() {
        return JMEventHandler.MOD_JM_ID;
    }

    @Override
    public void onEvent(ClientEvent event) {
        try {
            switch (event.type) {
                case DISPLAY_UPDATE, // 这个事件并不会实时更新小地图，绘制放在ClientTickEvent里
                     MAPPING_STARTED:  // 刚进游戏时触发
                    JMShapeDrawer.cachedDimension = event.dimension;
                    break;
                case MAPPING_STOPPED: // 退出游戏时触发
                    if (jmAPI != null) {
                        jmAPI.removeAll(getModId());
                    }
                    break;
            }
        } catch (Throwable t) {
            BattleRoyale.LOGGER.error(t.getMessage(), t);
        }
    }

    public void removeAll(String modId) {
        if (jmAPI != null) jmAPI.removeAll(modId);
    }

    public void show(JMPolygonOverlay jmPolygonOverlay) {
        if (jmAPI == null) return;

        JMShapeProperties jmProps = jmPolygonOverlay.JMShapeProperties();
        ShapeProperties shapeProperties = new ShapeProperties()
                .setFillColor(jmProps.fillColorInt())
                .setFillOpacity(jmProps.fillOpacity())
                .setStrokeColor(jmProps.strokeColorInt())
                .setStrokeOpacity(jmProps.strokeOpacity())
                .setStrokeWidth(jmProps.strokeWidth());

        MapPolygon mapPolygon = new MapPolygon(jmPolygonOverlay.JMMapPolygon().points());

        PolygonOverlay polygonOverlay = new PolygonOverlay(
                jmPolygonOverlay.modId(),
                jmPolygonOverlay.displayId(),
                jmPolygonOverlay.dimension(),
                shapeProperties,
                mapPolygon);
        try {
            jmAPI.show(polygonOverlay);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to draw polygon on Fabric JourneyMap: {}", e.getMessage());
        }
    }
}