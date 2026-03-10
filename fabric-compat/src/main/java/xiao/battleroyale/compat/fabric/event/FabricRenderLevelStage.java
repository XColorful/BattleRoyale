package xiao.battleroyale.compat.fabric.event;

import xiao.battleroyale.api.event.RenderLevelStage;

public class FabricRenderLevelStage {
    // 这里的逻辑是将 Fabric 的 WorldRenderContext 对应的阶段映射到你的 API 核心枚举
    // 实际在 Manager 中根据调用的钩子（如 AFTER_ENTITIES）来传入对应的 Core Stage
    public static RenderLevelStage fromFabric() {
        // 具体的转换逻辑通常直接在 Manager 触发时硬编码传入
        return null;
    }
}