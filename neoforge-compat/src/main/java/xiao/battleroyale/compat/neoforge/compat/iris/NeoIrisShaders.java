package xiao.battleroyale.compat.neoforge.compat.iris;

import xiao.battleroyale.api.compat.iris.IIrisPipelineRegister;

public class NeoIrisShaders implements IIrisPipelineRegister {

    private static class NeoIrisShadersHolder {
        private static final NeoIrisShaders INSTANCE = new NeoIrisShaders();
    }

    public static NeoIrisShaders get() {
        return NeoIrisShadersHolder.INSTANCE;
    }

    private NeoIrisShaders() {}

    @Override
    public void registerSoild(Object renderPipeline) {
        NeoIrisPipelines.get().registerSolid(renderPipeline);
    }

    @Override
    public void registerTranslucent(Object renderPipeline) {
        NeoIrisPipelines.get().registerTranslucent(renderPipeline);
    }
}
