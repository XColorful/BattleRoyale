package xiao.battleroyale.util;

import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class TagUtils {

    public static boolean hasUUID(CompoundTag tag, String key) {
        return tag.contains(key) && tag.get(key) instanceof IntArrayTag;
    }
    public static @Nullable UUID getUUID(CompoundTag tag, String key) {
        Optional<int[]> intArray = tag.getIntArray(key);
        return intArray.map(UUIDUtil::uuidFromIntArray).orElse(null);
    }
    public static @NotNull CompoundTag putUUID(@NotNull CompoundTag tag, String key, UUID uuid) {
        int[] intArray = UUIDUtil.uuidToIntArray(uuid);
        tag.putIntArray(key, intArray);
        return tag;
    }

    public static boolean hasInt(CompoundTag tag, String key) {
        return tag.contains(key) && tag.get(key) instanceof IntTag;
    }
    public static int getInt(CompoundTag tag, String key) {
        return tag.getIntOr(key, 0);
    }

    public static @Nullable UUID getUUID(ValueInput input, String key) {
        return input.getIntArray(key)
                .map(UUIDUtil::uuidFromIntArray)
                .orElse(null);
    }
    public static int getInt(ValueInput input, String key, int defaultValue) {
        return input.getIntOr(key, defaultValue);
    }
    public static void putUUID(ValueOutput output, String key, UUID uuid) {
        int[] intArray = UUIDUtil.uuidToIntArray(uuid);
        output.putIntArray(key, intArray);
    }
    public static void putInt(ValueOutput output, String key, int value) {
        output.putInt(key, value);
    }
}
