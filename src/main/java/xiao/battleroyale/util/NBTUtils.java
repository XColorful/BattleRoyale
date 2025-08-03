package xiao.battleroyale.util;

import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.message.team.GameTeamTag;
import xiao.battleroyale.api.message.zone.GameZoneTag;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.client.game.data.TeamMemberInfo;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

import java.util.List;
import java.util.Optional;

public class NBTUtils {

    @NotNull
    public static CompoundTag stringToNBT(String nbt) {
        if (nbt == null || nbt.isEmpty()) {
            return new CompoundTag();
        }
        try {
            return TagParser.parseTag(nbt);
        } catch (Exception e) {
            BattleRoyale.LOGGER.warn("Failed to parse NBT {}: {}", nbt, e.getMessage());
            return new CompoundTag();
        }
    }

    public static CompoundTag serializeZoneToNBT(int zoneId, String zoneName, String zoneColor,
                                                 ITickableZone tickableZone, ISpatialZone spatialZone,
                                                 double shapeProgress) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(GameZoneTag.ZONE_ID, zoneId);
        tag.putString(GameZoneTag.ZONE_NAME, zoneName);
        tag.putString(GameZoneTag.ZONE_COLOR, zoneColor);

        tag.putString(GameZoneTag.FUNC, tickableZone.getFuncType().getName());

        ZoneShapeType shapeType = spatialZone.getShapeType();
        tag.putString(GameZoneTag.SHAPE, shapeType.getName());
        if (shapeType == ZoneShapeType.POLYGON || shapeType == ZoneShapeType.STAR) {
            tag.putInt(GameZoneTag.SEGMENTS, spatialZone.getSegments());
        }

        CompoundTag centerTag = new CompoundTag();
        Vec3 center = spatialZone.getCenterPos(shapeProgress);
        centerTag.putDouble("x", center.x);
        centerTag.putDouble("y", center.y);
        centerTag.putDouble("z", center.z);
        tag.put(GameZoneTag.CENTER, centerTag);

        CompoundTag dimTag = new CompoundTag();
        Vec3 dim = spatialZone.getDimension(shapeProgress);
        dimTag.putDouble("x", dim.x);
        dimTag.putDouble("y", dim.y);
        dimTag.putDouble("z", dim.z);
        tag.put(GameZoneTag.DIMENSION, dimTag);

        double rotate = spatialZone.getRotateDegree(shapeProgress);
        if (rotate != 0) {
            tag.putDouble(GameZoneTag.ROTATE, rotate);
        }

        tag.putDouble(GameZoneTag.SHAPE_PROGRESS, shapeProgress);
        return tag;
    }

    public static CompoundTag serializeTeamToNBT(int teamId, String teamColor, List<TeamMemberInfo> memberInfos) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(GameTeamTag.TEAM_ID, teamId);
        tag.putString(GameTeamTag.TEAM_COLOR, teamColor);

        CompoundTag teamMemberTag = new CompoundTag();
        for (TeamMemberInfo memberInfo : memberInfos) {
            CompoundTag memberTag = new CompoundTag();
            memberTag.putString(GameTeamTag.MEMBER_NAME, memberInfo.name);
            memberTag.putFloat(GameTeamTag.MEMBER_HEALTH, memberInfo.health);
            memberTag.putInt(GameTeamTag.MEMBER_BOOST, memberInfo.boost);
            memberTag.putUUID(GameTeamTag.MEMBER_UUID, memberInfo.uuid);
            teamMemberTag.put(String.valueOf(memberInfo.playerId), memberTag);
        }

        tag.put(GameTeamTag.TEAM_MEMBER, teamMemberTag);
        return tag;
    }

    @NotNull
    public static BlockState readBlockState(@NotNull CompoundTag nbt) {
        DataResult<BlockState> result = BlockState.CODEC.parse(NbtOps.INSTANCE, nbt);

        if (result.result().isPresent()) {
            return result.result().get(); // 返回成功解析的方块状态
        } else {
            // 解析失败，记录警告并回退到默认方块状态
            String blockId = nbt.getString("Name");
            Optional<DataResult.PartialResult<BlockState>> errorResult = result.error(); // 获取错误信息

            if (errorResult.isPresent()) {
                BattleRoyale.LOGGER.warn("Failed to parse BlockState from NBT: '{}', reason: {}. Returning default BlockState for {}.", nbt, errorResult.get().message(), blockId);
            } else {
                BattleRoyale.LOGGER.warn("Failed to parse BlockState from NBT: '{}'. Returning default BlockState for {}.", nbt, blockId);
            }

            // 尝试根据 blockId 获取方块，否则回退到空气方块的默认状态
            ResourceLocation rl = ResourceLocation.tryParse(blockId);
            Block block = (rl != null) ? ForgeRegistries.BLOCKS.getValue(rl) : null;
            return block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }
    }

    @NotNull
    public static ItemStack readItemStack(@NotNull CompoundTag nbt) {
        if (nbt.isEmpty()) {
            return ItemStack.EMPTY;
        }
        try {
            return ItemStack.of(nbt);
        } catch (Exception e) {
            BattleRoyale.LOGGER.warn("Failed to parse ItemStack from NBT: '{}', reason: {}", nbt, e.getMessage());
            return ItemStack.EMPTY;
        }
    }

    @NotNull
    public static CompoundTag JsonToNBT(@Nullable JsonObject jsonObject) {
        CompoundTag nbt = new CompoundTag();
        if (jsonObject == null) {
            return nbt;
        }

        // TODO Json转NBT

        return nbt;
    }
}