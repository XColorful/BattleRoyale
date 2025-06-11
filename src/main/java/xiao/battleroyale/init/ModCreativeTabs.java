package xiao.battleroyale.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import xiao.battleroyale.BattleRoyale;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BattleRoyale.MOD_ID);

    public static RegistryObject<CreativeModeTab> BLOCK_TAB = TABS.register("block", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tab.battleroyale.block"))
            .icon(() -> new ItemStack(ModBlocks.ZONE_CONTROLLER.get()))
            .displayItems(((itemDisplayParameters, output) -> {
                output.accept(ModItems.LOOT_SPAWNER_ITEM.get());
                output.accept(ModItems.ENTITY_SPAWNER_ITEM.get());
                output.accept(ModItems.ZONE_CONTROLLER_ITEM.get());
            }))
            .build());
}