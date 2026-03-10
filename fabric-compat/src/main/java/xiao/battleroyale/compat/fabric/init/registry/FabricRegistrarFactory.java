package xiao.battleroyale.compat.fabric.init.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import xiao.battleroyale.api.init.registry.IMenuTypeFactory;
import xiao.battleroyale.api.init.registry.IRegistrar;
import xiao.battleroyale.api.init.registry.IRegistrarFactory;

public class FabricRegistrarFactory implements IRegistrarFactory {

    private final IMenuTypeFactory menuTypeFactory = new FabricMenuTypeFactory();

    @Override
    public <T> IRegistrar<T> create(String modId, Registry<T> registry) {
        return new FabricRegistrar<>(registry, modId);
    }

    @Override
    public IRegistrar<Block> createBlocks(String modId) {
        return new FabricRegistrar<>(BuiltInRegistries.BLOCK, modId);
    }

    @Override
    public IRegistrar<BlockEntityType<?>> createBlockEntities(String modId) {
        return new FabricRegistrar<>(BuiltInRegistries.BLOCK_ENTITY_TYPE, modId);
    }

    @Override
    public IRegistrar<Item> createItems(String modId) {
        return new FabricRegistrar<>(BuiltInRegistries.ITEM, modId);
    }

    @Override
    public IRegistrar<CreativeModeTab> createCreativeTabs(String modId) {
        return new FabricRegistrar<>(BuiltInRegistries.CREATIVE_MODE_TAB, modId);
    }

    @Override
    public IRegistrar<MenuType<?>> createMenuTypes(String modId) {
        return new FabricRegistrar<>(BuiltInRegistries.MENU, modId);
    }

    @Override
    public IRegistrar<SoundEvent> createSounds(String modId) {
        return new FabricRegistrar<>(BuiltInRegistries.SOUND_EVENT, modId);
    }

    @Override
    public IRegistrar<EntityType<?>> createEntityTypes(String modId) {
        return new FabricRegistrar<>(BuiltInRegistries.ENTITY_TYPE, modId);
    }

    @Override
    public IMenuTypeFactory getMenuTypeFactory() {
        return menuTypeFactory;
    }
}