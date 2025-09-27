package xiao.battleroyale.compat.forge;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import xiao.battleroyale.api.init.IMenuTypeFactory;
import xiao.battleroyale.api.init.IRegistrar;
import xiao.battleroyale.api.init.IRegistrarFactory;

/**
 * IRegistrarFactory 的 Forge 实现，负责创建基于 Forge API 的注册器。
 */
public class ForgeRegistrarFactory implements IRegistrarFactory {

    private final IMenuTypeFactory menuTypeFactory = new ForgeMenuTypeFactory();

    @Override
    public <T> IRegistrar<T> create(String modId, Registry<T> registry) {
        return new ForgeRegistrar<>(DeferredRegister.create(registry.key(), modId));
    }

    @Override
    public IRegistrar<Block> createBlocks(String modId) {
        return new ForgeRegistrar<>(DeferredRegister.create(ForgeRegistries.BLOCKS, modId));
    }

    @Override
    public IRegistrar<BlockEntityType<?>> createBlockEntities(String modId) {
        return new ForgeRegistrar<>(DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, modId));
    }

    @Override
    public IRegistrar<Item> createItems(String modId) {
        return new ForgeRegistrar<>(DeferredRegister.create(ForgeRegistries.ITEMS, modId));
    }

    @Override
    public IRegistrar<CreativeModeTab> createCreativeTabs(String modId) {
        return new ForgeRegistrar<>(DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modId));
    }

    @Override
    public IRegistrar<MenuType<?>> createMenuTypes(String modId) {
        return new ForgeRegistrar<>(DeferredRegister.create(ForgeRegistries.MENU_TYPES, modId));
    }

    @Override
    public IRegistrar<SoundEvent> createSounds(String modId) {
        return new ForgeRegistrar<>(DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, modId));
    }

    @Override
    public IRegistrar<EntityType<?>> createEntityTypes(String modId) {
        return new ForgeRegistrar<>(DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, modId));
    }

    @Override
    public IMenuTypeFactory getMenuTypeFactory() {
        return menuTypeFactory;
    }
}