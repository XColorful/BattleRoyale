package xiao.battleroyale.client.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import xiao.battleroyale.BattleRoyale;

public class CustomRenderType {

    private static final ResourceLocation WHITE_TEXTURE = BattleRoyale.getMcRegistry().createResourceLocation(String.format("%s:textures/white.png", BattleRoyale.MOD_ID));

    // 先加载
    public static final RenderPipeline SOLID_OPAQUE_COLOR_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
            .withLocation(BattleRoyale.getMcRegistry().createResourceLocation(String.format("%s:solid_opaque", BattleRoyale.MOD_ID)))
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withoutBlend()
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .withDepthWrite(true)
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .build();
    public static final RenderPipeline SOLID_TRANSLUCENT_COLOR_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
            .withLocation(BattleRoyale.getMcRegistry().createResourceLocation(String.format("%s:solid_translucent", BattleRoyale.MOD_ID)))
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withBlend(BlendFunction.TRANSLUCENT)
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .withDepthWrite(false)
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .build();
    // 后加载
    public static final RenderType SolidTranslucentColor = createSolidTranslucent();
    public static final RenderType SolidOpaqueColor = createSolidOpaque();


    private static RenderType createSolidTranslucent() {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                .setOverlayState(RenderStateShard.NO_OVERLAY)
                .setOutputState(RenderStateShard.MAIN_TARGET)
                .createCompositeState(false);

        return RenderType.create(
                "solid_translucent_color",
                256,
                false,
                false,
                SOLID_TRANSLUCENT_COLOR_PIPELINE,
                state
        );
    }

    private static RenderType createSolidOpaque() {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                .setOverlayState(RenderStateShard.NO_OVERLAY)
                .setOutputState(RenderStateShard.MAIN_TARGET)
                .createCompositeState(true);

        return RenderType.create(
                "solid_opaque_color",
                256,
                false,
                false,
                SOLID_OPAQUE_COLOR_PIPELINE,
                state
        );
    }
}
