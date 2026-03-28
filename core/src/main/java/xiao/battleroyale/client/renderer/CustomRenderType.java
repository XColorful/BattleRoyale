package xiao.battleroyale.client.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import xiao.battleroyale.BattleRoyale;

public class CustomRenderType {

    private static final Identifier WHITE_TEXTURE = BattleRoyale.getMcRegistry().createResourceLocation(String.format("%s:textures/white.png", BattleRoyale.MOD_ID));

    // 先加载
    public static final RenderPipeline SOLID_OPAQUE_COLOR_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
            .withLocation(BattleRoyale.getMcRegistry().createResourceLocation(String.format("%s:solid_opaque", BattleRoyale.MOD_ID)))
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withColorTargetState(ColorTargetState.DEFAULT)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .build();
    public static final RenderPipeline SOLID_TRANSLUCENT_COLOR_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
            .withLocation(BattleRoyale.getMcRegistry().createResourceLocation(String.format("%s:solid_translucent", BattleRoyale.MOD_ID)))
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .build();
    // 后加载
    public static final RenderType SolidTranslucentColor = createSolidTranslucent();
    public static final RenderType SolidOpaqueColor = createSolidOpaque();


    private static RenderType createSolidTranslucent() {
        RenderSetup builder = RenderSetup.builder(SOLID_TRANSLUCENT_COLOR_PIPELINE)
                // .useLightmap() // 默认为false
                // .useOverlay() // 默认为false
                .setOutputTarget(OutputTarget.MAIN_TARGET)
                .setOutline(RenderSetup.OutlineProperty.NONE) // 默认值 (无描边)
                .bufferSize(RenderType.BIG_BUFFER_SIZE) // TRANSIENT_BUFFER_SIZE (1536) 随便来个椭球就爆了, SMALL_BUFFER_SIZE形状一多就会导致1帧内两次draw call
                // .sortOnUpload() // 默认为false
                .createRenderSetup();

        return RenderType.create(
                "solid_translucent_color",
                builder
        );
    }

    private static RenderType createSolidOpaque() {// 1. 创建 RenderSetup.builder()
        RenderSetup builder = RenderSetup.builder(SOLID_OPAQUE_COLOR_PIPELINE)
                // .useLightmap() // 默认为false
                // .useOverlay() // 默认为false
                .setOutputTarget(OutputTarget.MAIN_TARGET)
                .setOutline(RenderSetup.OutlineProperty.NONE) // 默认值 (无描边)
                .bufferSize(RenderType.BIG_BUFFER_SIZE) // TRANSIENT_BUFFER_SIZE (1536) 随便来个椭球就爆了, SMALL_BUFFER_SIZE形状一多就会导致1帧内两次draw call
                .sortOnUpload()
                .createRenderSetup();

        return RenderType.create(
                "solid_opaque_color",
                builder
        );
    }
}
