package xiao.battleroyale.developer.debug.text;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public static final List<String> vanillaBlockEntityNbtKey = Arrays.asList("ForgeData", "x", "y", "z", "id");;
    public static MutableComponent buildBlockEntityNbt(ServerLevel serverLevel, BlockPos blockPos, BlockEntity blockEntity) {
        MutableComponent component = Component.empty();
        if (blockPos == null || blockEntity == null) {
            return component;
        }
        CompoundTag fullNbt = blockEntity.saveWithFullMetadata();
        int nbtCount = fullNbt.isEmpty() ? 0 : fullNbt.getAllKeys().size();
        CompoundTag forgeData = blockEntity.getPersistentData();
        int forgeCount = forgeData.isEmpty() ? 0 : forgeData.getAllKeys().size();

        // Vanilla
        Block block = serverLevel.getBlockState(blockPos).getBlock();
        ResourceLocation blockRL = ForgeRegistries.BLOCKS.getKey(block);
        String blockName = block.getName().getString();
        component.append(buildHoverableText(blockName, blockRL != null ? blockRL.toString() : "null"))
                .append(buildHoverableTextWithColor("BlockEntity",
                        buildNbtVerticalList(fullNbt),
                        nbtCount > vanillaBlockEntityNbtKey.size() ? ChatFormatting.GREEN : ChatFormatting.AQUA))
                .append(buildRunnableVec(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ())));
        // ForgeData
        if (forgeCount > 0) {
            component.append(Component.literal("|").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)))
                    .append(buildHoverableTextWithColor("ForgeData",
                            buildNbtVerticalList(forgeData),
                            ChatFormatting.GREEN));
        }

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
}
