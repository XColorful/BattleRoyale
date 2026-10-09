package xiao.battleroyale.compat.forge.compat.journeymap;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.display.PolygonOverlay;
import journeymap.api.v2.client.event.DisplayUpdateEvent;
import journeymap.api.v2.client.event.MappingEvent;
import journeymap.api.v2.client.model.MapPolygon;
import journeymap.api.v2.client.model.ShapeProperties;
import journeymap.api.v2.common.event.ClientEventRegistry;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.compat.journeymap.*;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * 注解必须用全限定名：API 2.0的注解与本体类同名，单类型导入会与本文件声明的类冲突。
 */
@ParametersAreNonnullByDefault
@journeymap.api.v2.common.JourneyMapPlugin(apiVersion = "2.0.0")
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
     * Called by JourneyMap during the init phase of mod loading.  The IClientAPI reference is how the mod
     * will add overlays, etc. to JourneyMap.
     *
     * @param jmAPI Client API implementation
     */
    @Override
    public void initialize(IClientAPI jmAPI) {
        BattleRoyale.LOGGER.debug("initialize JourneyMapPlugin");
        this.jmAPI = jmAPI;
        ClientEventRegistry.MAPPING_EVENT.subscribe(getModId(), this::handleMappingEvent);
        ClientEventRegistry.DISPLAY_UPDATE_EVENT.subscribe(getModId(), this::handleDisplayUpdateEvent);
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

    private void handleDisplayUpdateEvent(DisplayUpdateEvent event) {
        // 这个事件并不会实时更新小地图，绘制放在ClientTickEvent里
        try {
            JMShapeDrawer.cachedDimension = event.dimension;
        } catch (Throwable t) {
            BattleRoyale.LOGGER.error(t.getMessage(), t);
        }
    }

    private void handleMappingEvent(MappingEvent event) {
        try {
            switch (event.getStage()) {
                case MAPPING_STARTED -> // 刚进游戏时触发
                        JMShapeDrawer.cachedDimension = event.dimension;
                case MAPPING_STOPPED -> // 退出游戏时触发
                        jmAPI.removeAll(JMEventHandler.MOD_JM_ID);
            }
        } catch (Throwable t) {
            BattleRoyale.LOGGER.error(t.getMessage(), t);
        }
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

        // API 2.0移除了displayId参数，overlay身份由随机UUID标识，靠removeAll避免累积
        PolygonOverlay polygonOverlay = new PolygonOverlay(
                JMPolygonOverlay.modId(),
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
