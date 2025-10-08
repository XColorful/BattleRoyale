package xiao.battleroyale.developer.debug.text;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.LootNBTTag;
import xiao.battleroyale.api.minecraft.ComponentsTag;
import xiao.battleroyale.api.minecraft.InventoryIndex;
import xiao.battleroyale.api.minecraft.InventoryIndex.SlotType;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.developer.debug.command.sub.get.GetWorld;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.TagUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static xiao.battleroyale.util.CommandUtils.*;

public class WorldText {

    public static MutableComponent buildBlockEntitesNbt(ServerLevel serverLevel, Map<BlockPos, BlockEntity> blockEntities) {
        MutableComponent component = Component.empty();
        if (blockEntities == null) {
            return component;
        }
        for (Map.Entry<BlockPos, BlockEntity> entry : blockEntities.entrySet()) {
            BlockPos blockPos = entry.getKey();
            BlockEntity blockEntity = entry.getValue();
            component.append(Component.literal("\n"))
                    .append(buildBlockEntityNbt(serverLevel, blockPos, blockEntity));
        }

        return component;
    }

    public static final List<String> vanillaBlockEntityNbtKey = Arrays.asList("x", "y", "z", "id");;
    public static MutableComponent buildBlockEntityNbt(ServerLevel serverLevel, BlockPos blockPos, BlockEntity blockEntity) {
        MutableComponent component = Component.empty();
        if (blockPos == null || blockEntity == null) {
            return component;
        }
        CompoundTag fullNbt = blockEntity.saveWithFullMetadata(serverLevel.registryAccess());
        int nbtCount = fullNbt.isEmpty() ? 0 : fullNbt.keySet().size();

        CompoundTag components = fullNbt.getCompound(ComponentsTag.COMPONENTS).get();
        int componentsCount = components.isEmpty() ? 0 : components.keySet().size();

        ListTag items = fullNbt.getListOrEmpty("Items");
        int itemsCount = items.isEmpty() ? 0 : items.size();

        UUID gameId = null;
        Tag gameIdTag = null;

        // Vanilla
        Block block = serverLevel.getBlockState(blockPos).getBlock();
        ResourceLocation blockRL = BattleRoyale.getMcRegistry().getBlockRl(block);
        String blockName = block.getName().getString();
        component.append(buildHoverableText(blockName, blockRL != null ? blockRL.toString() : "null"))
                .append(buildHoverableTextWithColor("BlockEntity",
                        buildNbtVerticalList(fullNbt),
                        nbtCount > vanillaBlockEntityNbtKey.size() ? ChatFormatting.GREEN : ChatFormatting.AQUA))
                .append(buildRunnableVec(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ())));
        // components
        if (componentsCount > 0) {
            CustomData customData = blockEntity.components().get(DataComponents.CUSTOM_DATA);
            if (customData != null && customData.contains(LootNBTTag.GAME_ID_TAG)) {
                CompoundTag customDataTag = customData.copyTag();
                gameId = TagUtils.getUUID(customDataTag, LootNBTTag.GAME_ID_TAG);
                gameIdTag = customDataTag.get(LootNBTTag.GAME_ID_TAG);
            }
            component.append(Component.literal("|").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)))
                    .append(buildHoverableTextWithColor(ComponentsTag.COMPONENTS,
                            buildNbtVerticalList(components),
                            ChatFormatting.GREEN));
        }
        // Items
        if (itemsCount > 0) {
            component.append(Component.literal("|").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)))
                    .append(buildHoverableTextWithColor("Items",
                            buildNbtListVerticalList(items),
                            ChatFormatting.GOLD));
        }
        // GameId
        if (gameIdTag == null && fullNbt.contains(LootNBTTag.GAME_ID_TAG)) {
            gameId = TagUtils.getUUID(fullNbt, LootNBTTag.GAME_ID_TAG);
            gameIdTag = fullNbt.get(LootNBTTag.GAME_ID_TAG);
        }
        if (gameId != null && gameIdTag != null) {
            UUID currentGameId = GameManager.get().getGameId();
            component.append(Component.literal("|").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)))
                    .append(buildHoverableTextWithColor(LootNBTTag.GAME_ID_TAG,
                            gameIdTag.toString(),
                            gameId.equals(currentGameId) ? ChatFormatting.BLUE : ChatFormatting.GRAY));
        }

        return component;
    }

    // 默认slotIndex已经排序
    public static MutableComponent buildItemStacks(ServerPlayer player, List<Integer> slotIndex, List<ItemStack> itemStacks) {
        MutableComponent component = Component.empty();
        if (slotIndex.size() != itemStacks.size()) {
            BattleRoyale.LOGGER.error("Failed to buildItemStacks, slotIndex.size()={}, itemStacks.size()={}", slotIndex.size(), itemStacks.size());
            return component;
        }

        boolean hotbar = false;
        boolean inventory = false;
        boolean armor = false;
        boolean offhand = false;
        boolean custom = false;
        int currentSlotIndex = -1;
        ChatFormatting displayColor;
        String playerName = player.getName().getString();
        for (int i = 0; i < slotIndex.size(); i++) {
            currentSlotIndex = slotIndex.get(i);
            SlotType slotType = InventoryIndex.getSlotType(currentSlotIndex);
            switch (slotType) {
                case HOTBAR -> {
                    displayColor = ChatFormatting.AQUA;
                    if (!hotbar) {
                        component.append(buildRunnableText("\n[Hotbar]",
                                GetWorld.getHotbarItemStacksCommand(playerName),
                                ChatFormatting.GOLD));
                        hotbar = true;
                    }
                }
                case INVENTORY -> {
                    displayColor = ChatFormatting.GRAY;
                    if (!inventory) {
                        component.append(buildRunnableText("\n[Inventory]",
                                GetWorld.getInventoryItemStacksCommand(playerName),
                                ChatFormatting.GOLD));
                        inventory = true;
                    }
                }
                case ARMOR -> {
                    displayColor = ChatFormatting.GREEN;
                    if (!armor) {
                        component.append(buildRunnableText("\n[Armor]",
                                GetWorld.getArmorItemStacksCommand(playerName),
                                ChatFormatting.GOLD));
                        armor = true;
                    }
                }
                case OFFHAND -> {
                    displayColor = ChatFormatting.BLUE;
                    if (!offhand) {
                        component.append(buildRunnableText("\n[Offhand]",
                                GetWorld.getOffhandItemStacksCommand(playerName),
                                ChatFormatting.GOLD));
                        offhand = true;
                    }
                }
                default -> {
                    displayColor = ChatFormatting.WHITE;
                    if (!custom) {
                        component.append(buildRunnableText("\n[Offhand]",
                                GetWorld.getCustomItemStacksCommand(playerName),
                                ChatFormatting.GOLD));
                        custom = true;
                    }
                }
            }
            ItemStack itemStack = itemStacks.get(i);
            HolderLookup.Provider registries = BattleRoyale.getStaticRegistries();
            BattleRoyale.LOGGER.debug("buildItemStacks Not implemented yet");
//            CompoundTag nbt = registries != null ? (CompoundTag) itemStack.save(registries) : new CompoundTag();
//            component.append(buildHoverableTextWithColor(" " + itemStack.getDisplayName().getString(),
//                    buildNbtVerticalList(nbt != null ? nbt : new CompoundTag()),
//                    displayColor));
        }

        return component;
    }

    public static MutableComponent buildItemStack(ItemStack itemStack) {
        MutableComponent component = Component.empty();

        HolderLookup.Provider registries = BattleRoyale.getStaticRegistries();
        BattleRoyale.LOGGER.debug("buildItemStack Not implemented yet");
//        CompoundTag tag = registries != null ? (CompoundTag) itemStack.save(registries) : new CompoundTag();
//        component.append(buildHoverableText(itemStack.getDisplayName().getString(),
//                buildNbtVerticalList(tag != null ? tag : new CompoundTag())));
        return component;
    }

    public static MutableComponent buildBiome(BlockPos blockPos, ResourceLocation biomeRL, ResourceKey<Biome> biomeRK) {
        MutableComponent component = Component.empty();

        component.append(buildRunnableVec(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ())))
                .append(Component.literal("\n"))
                // biomeRL
                .append(Component.literal("biome"))
                .append(buildHoverableTextWithColor("ResourceLocation",
                biomeRL != null ? biomeRL.toString() : "",
                biomeRL != null ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
                // biomeRK
                .append(Component.literal("|").setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)))
                .append(buildHoverableTextWithColor("ResourceKey",
                        biomeRK != null ? biomeRK.toString() : "",
                        biomeRK != null ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY));

        return component;
    }

    public static MutableComponent buildStructures(BlockPos blockPos, Map<ResourceLocation, ResourceKey<Structure>> structures) {
        MutableComponent component = Component.empty();

        component.append(buildRunnableVec(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ())));

        for (Map.Entry<ResourceLocation, ResourceKey<Structure>> entry : structures.entrySet()) {
            ResourceLocation rl = entry.getKey();
            ResourceKey<Structure> rk = entry.getValue();
            component.append(Component.literal("\n"))
                    .append(Component.literal("structure"))
                    .append(buildHoverableTextWithColor("ResourceLocation",
                            rl.toString(),
                            ChatFormatting.AQUA))
                    .append(Component.literal("|").setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)))
                    .append(buildHoverableTextWithColor("ResourceKey",
                            rk != null ? rk.toString() : "",
                            rk != null ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY));
        }

        return component;
    }

    public static MutableComponent buildServerLevel(@Nullable ServerLevel serverLevel, ResourceKey<Level> levelKey, String levelKeyString) {
        MutableComponent component = Component.empty();

        component.append(Component.literal(levelKeyString))
                .append(Component.literal("\n"))
                .append(Component.literal("ServerLevel:"))
                .append(serverLevel != null ?
                        Component.literal(serverLevel.toString()).withStyle(ChatFormatting.AQUA)
                        : Component.literal("null").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal("\n"))
                .append(Component.literal("LevelKey:"))
                .append(serverLevel != null ?
                        buildHoverableText(levelKey.location().toString(), buildLevelKeyHover(levelKey)).withStyle(ChatFormatting.GREEN)
                        : Component.literal("null").withStyle(ChatFormatting.DARK_GRAY));

        return component;
    }

    public static MutableComponent buildLevelKey(@NotNull ServerLevel serverLevel) {
        MutableComponent component = Component.empty();

        ResourceKey<Level> levelKey = serverLevel.dimension();
        component.append(serverLevel.toString())
                .append(Component.literal(" "))
                .append(buildHoverableText(levelKey.toString(), buildLevelKeyHover(serverLevel.dimension())).withStyle(ChatFormatting.AQUA));

        return component;
    }

    private static MutableComponent buildLevelKeyHover(ResourceKey<Level> levelKey) {
        return Component.empty()
                .append(Component.literal("RegistryName:").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(levelKey.registry().toString()))
                .append(Component.literal("\n"))
                .append(Component.literal("Location:").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(levelKey.location().toString()).withStyle(ChatFormatting.GREEN));
    }
}
