package xiao.battleroyale.api.compat.iris;

public interface IIrisPipelineRegister {

    /**
     * @param renderPipeline {@link com.mojang.blaze3d.pipeline.RenderPipeline}
     */
    void registerSoild(Object renderPipeline);

    /**
     * @param renderPipeline {@link com.mojang.blaze3d.pipeline.RenderPipeline}
     */
    void registerTranslucent(Object renderPipeline);
}
