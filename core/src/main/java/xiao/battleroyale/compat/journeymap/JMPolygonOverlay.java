package xiao.battleroyale.compat.journeymap;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record JMPolygonOverlay(String modId,
                               ResourceKey<Level> dimension,
                               JMShapeProperties JMShapeProperties,
                               JMMapPolygon JMMapPolygon) {}
