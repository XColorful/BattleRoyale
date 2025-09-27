package xiao.battleroyale.init.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.init.registry.IRegistrar;
import xiao.battleroyale.api.init.registry.IRegistryObject;

public class ModCreativeTabs {
    public static final IRegistrar<CreativeModeTab> TABS =
            BattleRoyale.getRegistrarFactory().createCreativeTabs(BattleRoyale.MOD_ID);

    public static final IRegistryObject<CreativeModeTab> BLOCK_TAB = TABS.register("block", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tab.battleroyale.block"))
            .icon(() -> new ItemStack(ModBlocks.ZONE_CONTROLLER.get()))
            .displayItems(((itemDisplayParameters, output) -> {
                output.accept(ModItems.LOOT_SPAWNER_ITEM.get());
                output.accept(ModItems.ENTITY_SPAWNER_ITEM.get());
                output.accept(ModItems.ZONE_CONTROLLER_ITEM.get());
            }))
            .build());
}