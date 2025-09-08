package xiao.battleroyale.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import xiao.battleroyale.BattleRoyale;

public class CustomRenderType {

    private static final ResourceLocation WHITE_TEXTURE = new ResourceLocation(BattleRoyale.MOD_ID, "textures/white.png");
    public static final RenderType SolidTranslucentColor = createSolidTranslucent();
    public static final RenderType SolidOpaqueColor = createSolidOpaque();

    private static RenderType createSolidTranslucent() {
        RenderStateShard.TransparencyStateShard translucent = new RenderStateShard.TransparencyStateShard(
                "zone_translucent",
                () -> {
                    RenderSystem.enableBlend();
                    GlStateManager._blendFuncSeparate(
                            GlStateManager.SourceFactor.SRC_ALPHA.value,
                            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value,
                            GlStateManager.SourceFactor.ONE.value,
                            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value
                    );
                },
                () -> {
                    RenderSystem.disableBlend();
                    RenderSystem.defaultBlendFunc();
                }
        );

        // 深度测试：LEQUAL
        RenderStateShard.DepthTestStateShard lequalDepth = new RenderStateShard.DepthTestStateShard("lequal", 515);
        // 半透明：只写颜色，不写深度
        RenderStateShard.WriteMaskStateShard colorWriteOnly = new RenderStateShard.WriteMaskStateShard(true, false);

        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader)) // 只用颜色（无normal/uv）
                .setTextureState(new RenderStateShard.TextureStateShard(WHITE_TEXTURE, false, false))
                .setTransparencyState(translucent)
                .setDepthTestState(lequalDepth)
                .setWriteMaskState(colorWriteOnly)
                .setCullState(new RenderStateShard.CullStateShard(false))
                .setLightmapState(new RenderStateShard.LightmapStateShard(false))
                .setOverlayState(new RenderStateShard.OverlayStateShard(false))
                .createCompositeState(true);

        return RenderType.create(
                "zone_translucent",
                DefaultVertexFormat.POSITION_COLOR,
                VertexFormat.Mode.QUADS,
                256,
                true, // affectsCrumbling
                false, // sortOnUpload
                state
        );
    }

    private static RenderType createSolidOpaque() {
        RenderStateShard.TransparencyStateShard noBlend = new RenderStateShard.TransparencyStateShard(
                "zone_opaque",
                RenderSystem::disableBlend,
                () -> {}
        );

        RenderStateShard.DepthTestStateShard lequalDepth = new RenderStateShard.DepthTestStateShard("lequal", 515);
        // 不透明：写颜色也写深度
        RenderStateShard.WriteMaskStateShard colorAndDepthWrite = new RenderStateShard.WriteMaskStateShard(true, true);

        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
                .setTextureState(new RenderStateShard.TextureStateShard(WHITE_TEXTURE, false, false))
                .setTransparencyState(noBlend)
                .setDepthTestState(lequalDepth)
                .setWriteMaskState(colorAndDepthWrite)
                .setCullState(new RenderStateShard.CullStateShard(false))
                .setLightmapState(new RenderStateShard.LightmapStateShard(false))
                .setOverlayState(new RenderStateShard.OverlayStateShard(false))
                .createCompositeState(true);

        return RenderType.create(
                "zone_opaque",
                DefaultVertexFormat.POSITION_COLOR,
                VertexFormat.Mode.QUADS,
                256,
                true,
                false,
                state
        );
    }
}
