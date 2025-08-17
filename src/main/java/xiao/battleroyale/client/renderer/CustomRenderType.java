package xiao.battleroyale.client.renderer;

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
    public static RenderType SolidColorZone = createSolidColorZone();

    private static RenderType createSolidColorZone() {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorTexShader))
                .setTextureState(new RenderStateShard.TextureStateShard(WHITE_TEXTURE, false, false))
                .setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                }, RenderSystem::disableBlend))
                .setDepthTestState(new RenderStateShard.DepthTestStateShard("always", 519))
                .setCullState(new RenderStateShard.CullStateShard(false))
                .setLightmapState(new RenderStateShard.LightmapStateShard(false))
                .setOverlayState(new RenderStateShard.OverlayStateShard(false))
                .createCompositeState(true);

        return RenderType.create("zone_render_type",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS, 256, true, false,
                compositeState);
    }
}