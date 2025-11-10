package xiao.battleroyale.api.client.render.game.level;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public interface IClientZoneRenderer extends IClientTranslucentRender {

    int getCircleSegments();
    void setCircleSegments(int segments);

    int getEllipseSegments();
    void setEllipseSegments(int segments);

    int getSphereSegments();
    void setSphereSegments(int segments);

    int getEllipsoidSegments();
    void setEllipsoidSegments(int segments);

    @Nullable Matrix4f getCurrentZoneMatrix();

    @Nullable VertexConsumer getVertexConsumer();
}
