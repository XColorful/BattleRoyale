package xiao.battleroyale.api.loot;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import com.google.gson.JsonObject;

public interface ILootAction<T> {
    /**
     * 对生成的配置列表执行操作，例如生成实体或添加到物品堆叠中.
     * @param generatedLoot 生成的战利品列表
     * @param random 提供随机数的 Supplier
     * @param lootContext 战利品上下文
     * @param blockEntity 触发战利品的方块实体
     */
    void apply(List<T> generatedLoot, Supplier<Float> random, LootContext lootContext, BlockEntity blockEntity);

    /**
     * 获取当前 LootAction 的类型，用于 JSON 反序列化.
     * @return LootAction 的类型名称
     */
    String getType();

    /**
     * 将当前 LootAction 序列化为 JSON 对象，用于配置存储和编辑.
     * @return 包含 LootAction 配置的 JSON 对象
     */
    JsonObject toJson();
}