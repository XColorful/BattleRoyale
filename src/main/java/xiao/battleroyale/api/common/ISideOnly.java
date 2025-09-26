package xiao.battleroyale.api.common;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public interface ISideOnly {

    default boolean clientSideOnly() {
        return false;
    }
    default boolean serverSideOnly() {
        return false;
    }
    default boolean inProperSide() {
        return inProperSide(FMLEnvironment.dist);
    }
    default boolean inProperSide(Dist dist) {
        if (clientSideOnly() && dist == Dist.DEDICATED_SERVER) {
            return false;
        } else return !serverSideOnly() || dist != Dist.CLIENT;
    }
}
