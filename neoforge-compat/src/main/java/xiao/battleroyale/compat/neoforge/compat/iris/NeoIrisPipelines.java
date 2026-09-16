package xiao.battleroyale.compat.neoforge.compat.iris;

// 26.3: Iris 尚无 26.3-neoforge 构建，此处为占位实现，待 Iris 更新后恢复
//import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.irisshaders.iris.pipeline.IrisPipelines;
import net.irisshaders.iris.pipeline.programs.ShaderKey;

public class NeoIrisPipelines {

    private static class NeoIrisPipelinesHolder {
        private static final NeoIrisPipelines INSTANCE = new NeoIrisPipelines();
    }

    public static NeoIrisPipelines get() {
        return NeoIrisPipelinesHolder.INSTANCE;
    }

    private NeoIrisPipelines() {}

    public void registerSolid(Object renderPipeline) {
//        RenderPipeline pipeline = (RenderPipeline) renderPipeline;
//        IrisPipelines.assignPipeline(pipeline, ShaderKey.BASIC_COLOR);
    }

    public void registerTranslucent(Object renderPipeline) {
//        RenderPipeline pipeline = (RenderPipeline) renderPipeline;
//        IrisPipelines.assignPipeline(pipeline, ShaderKey.BASIC_COLOR);
    }
}
