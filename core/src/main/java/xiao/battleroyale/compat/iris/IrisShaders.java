package xiao.battleroyale.compat.iris;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.compat.AbstractCompatMod;

public class IrisShaders extends AbstractCompatMod {

    @Override
    public String getModId() {
        return "iris";
    }

    private static class IrisShadersHolder {
        private static final IrisShaders INSTANCE = new IrisShaders();
    }

    public static IrisShaders get() {
        return IrisShadersHolder.INSTANCE;
    }

    private IrisShaders() {}

    @Override
    protected void onModLoaded() throws Exception {
        ;
    }

    public static void registerSolid(RenderPipeline renderPipeline) {
        if (!get().isLoaded()) {
            return;
        }

        BattleRoyale.getCompatApi().iIrisPipelineRegister().registerSoild(renderPipeline);
    }

    public static void registerTranslucent(RenderPipeline renderPipeline) {
        if (!get().isLoaded()) {
            return;
        }

        BattleRoyale.getCompatApi().iIrisPipelineRegister().registerTranslucent(renderPipeline);
    }
}
