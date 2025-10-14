package xiao.battleroyale.client.renderer.block;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LootRenderState extends BlockEntityRenderState {

    // public final ItemStackRenderState[] items;

    public LootRenderState(int size) {
        // this.items = new ItemStackRenderState[size];
        /**
         * 在{@link LootContainerRenderer#extractRenderState}创建对象
         */
//        for (int i = 0; i < size; i++) {
//            item[i] = new ItemStackRenderState();
//        }
    }
}
