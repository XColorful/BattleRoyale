package xiao.battleroyale.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

public class WorldUtils {

    public static int getGroundY(@NotNull ServerLevel serverLevel, double x, double z) {
        int maxBuildHeight = serverLevel.getMaxY();
        BlockPos lookupPos = BlockPos.containing(x, maxBuildHeight, z);
        return serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, lookupPos.getX(), lookupPos.getZ());
    }
}
