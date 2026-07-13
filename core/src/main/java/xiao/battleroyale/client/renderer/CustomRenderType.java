package xiao.battleroyale.client.renderer;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.client.renderer.level.SpectatePlayerRenderer;
import xiao.battleroyale.client.renderer.level.TeamMemberRenderer;
import xiao.battleroyale.client.renderer.level.ZoneRenderer;
import xiao.battleroyale.compat.iris.IrisShaders;

import java.util.function.Consumer;

public class CustomRenderType {

    private static final Identifier WHITE_TEXTURE = BattleRoyale.getMcRegistry().createResourceLocation(String.format("%s:textures/white.png", BattleRoyale.MOD_ID));

    // 先加载
    // 26.2: MATRICES_PROJECTION_SNIPPET 不再存在，手动构建: GLOBALS + MATRICES_PROJECTION + FOG
    public static final RenderPipeline SOLID_OPAQUE_COLOR_PIPELINE = RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
            .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            .withBindGroupLayout(BindGroupLayouts.FOG)
            .withLocation(BattleRoyale.getMcRegistry().createResourceLocation(String.format("%s:solid_opaque", BattleRoyale.MOD_ID)))
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withColorTargetState(ColorTargetState.DEFAULT)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withCull(false)
            .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .build();
    public static final RenderPipeline SOLID_TRANSLUCENT_COLOR_PIPELINE = RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
            .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            .withBindGroupLayout(BindGroupLayouts.FOG)
            .withLocation(BattleRoyale.getMcRegistry().createResourceLocation(String.format("%s:solid_translucent", BattleRoyale.MOD_ID)))
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
            .withCull(false)
            .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .build();
    // 后加载
    public static RenderType SolidTranslucentColor;
    public static RenderType SolidOpaqueColor;


    private static RenderType createSolidTranslucent() {
        RenderSetup builder = RenderSetup.builder(SOLID_TRANSLUCENT_COLOR_PIPELINE)
                // .useLightmap() // 默认为false
                // .useOverlay() // 默认为false
                .setOutputTarget(OutputTarget.MAIN_TARGET)
                .setOutline(RenderSetup.OutlineProperty.NONE) // 默认值 (无描边)
                // .sortOnUpload() // 默认为false
                .createRenderSetup();

        return RenderType.create(
                "solid_translucent_color",
                builder
        );
    }

    private static RenderType createSolidOpaque() {
        RenderSetup builder = RenderSetup.builder(SOLID_OPAQUE_COLOR_PIPELINE)
                // .useLightmap() // 默认为false
                // .useOverlay() // 默认为false
                .setOutputTarget(OutputTarget.MAIN_TARGET)
                .setOutline(RenderSetup.OutlineProperty.NONE) // 默认值 (无描边)
                .sortOnUpload()
                .createRenderSetup();

        return RenderType.create(
                "solid_opaque_color",
                builder
        );
    }

    @ApiStatus.AvailableSince("1.21.6")
    public static void onRegisterRenderPipelines(Consumer<RenderPipeline> registrar) {
        registrar.accept(SOLID_OPAQUE_COLOR_PIPELINE);
        registrar.accept(SOLID_TRANSLUCENT_COLOR_PIPELINE);
        SolidTranslucentColor = createSolidTranslucent();
        SolidOpaqueColor = createSolidOpaque();
        ZoneRenderer.TRANSLUCENT_ZONE = SolidTranslucentColor;
        ZoneRenderer.OPAQUE_ZONE = SolidOpaqueColor;
        TeamMemberRenderer.TEAM_MARKER_RENDER_TYPE = SolidTranslucentColor;
        SpectatePlayerRenderer.SPECTATE_PLAYER_RENDER_TYPE = SolidTranslucentColor;

        // --------Iris Shaders--------
        IrisShaders.registerSolid(SOLID_OPAQUE_COLOR_PIPELINE);
        IrisShaders.registerTranslucent(SOLID_TRANSLUCENT_COLOR_PIPELINE);
    }
}
