package xiao.battleroyale.api.client.render.level;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public interface IClientSimpleZoneRenderer extends IClientTranslucentRender {

    @Nullable Matrix4f getCurrentZoneMatrix();

    @Nullable VertexConsumer getVertexConsumer();
}
