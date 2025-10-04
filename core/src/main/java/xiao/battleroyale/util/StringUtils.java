package xiao.battleroyale.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

public class StringUtils {

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    /**
     * 解析一个表示二维或三维向量的字符串，并返回一个 Vec3 对象。
     * 字符串格式可以是 "Double,Double" 或 "Double,Double,Double"。
     *
     * @param inputString 要解析的字符串。
     * @return 对应的 Vec3 对象。如果格式不匹配或解析失败，则返回 null。
     */
    public @Nullable static Vec3 parseVectorString(@Nullable String inputString) {
        if (inputString == null || inputString.trim().isEmpty()) {
            return null;
        }

        String[] parts = COMMA_PATTERN.split(inputString.trim());

        try {
            if (parts.length == 2) { // 格式: Double,Double
                double d1 = Double.parseDouble(parts[0].trim());
                double d2 = Double.parseDouble(parts[1].trim());
                return new Vec3(d1, 0, d2);
            } else if (parts.length == 3) { // 格式: Double,Double,Double
                double d1 = Double.parseDouble(parts[0].trim());
                double d2 = Double.parseDouble(parts[1].trim());
                double d3 = Double.parseDouble(parts[2].trim());
                return new Vec3(d1, d2, d3);
            } else { // 格式不匹配
                return null;
            }
        } catch (NumberFormatException e) {
            BattleRoyale.LOGGER.info("Error parsing double from string: " + e.getMessage());
            return null;
        }
    }

    /**
     * 将 Vec3 对象转换为字符串格式 "x,y,z"。
     *
     * @param vec3 要转换的 Vec3 对象。
     * @return 对应的字符串。
     */
    public @NotNull static String vectorToString(@Nullable Vec3 vec3) {
        if (vec3 == null) {
            return "";
        }
        return String.format("%f,%f,%f", vec3.x, vec3.y, vec3.z);
    }

    public static String buildCommandString(String... parts) {
        StringBuilder commandBuilder = new StringBuilder("/");
        for (String part : parts) {
            commandBuilder.append(part).append(" ");
        }
        return commandBuilder.toString().trim();
    }

    /**
     * 返回格式(1.20, 2.33, 6.66)
     */
    public static String vectorTo2fString(Vec3 vec3) {
        return String.format("(%.2f, %.2f, %.2f)", vec3.x, vec3.y, vec3.z);
    }

    /**
     * 解析一个表示文本组件的 JSON 字符串，并返回一个 Component 对象。
     * 字符串格式必须符合 Minecraft 的 Component JSON 规范。
     * @param inputString 要解析的字符串。
     * @return 对应的 Component 对象。如果输入为 null 或解析失败，则返回 null。
     */
    public @Nullable static Component parseComponentString(@Nullable String inputString) {
        HolderLookup.Provider registries = BattleRoyale.getStaticRegistries();
        return inputString != null && registries != null ? Component.Serializer.fromJson(inputString, registries) : null;
    }

    /**
     * 将一个 Component 对象转换为稳定的 JSON 字符串格式。
     * “稳定”意味着 JSON 对象中的键（key）将始终按字母顺序排序，
     * 这确保了对于相同的 Component 对象，生成的 JSON 字符串总是一致的。
     * 这对于哈希（hashing）和版本控制（如 Git）非常有用。
     *
     * @param component 要转换的 Component 对象。
     * @return 对应的稳定 JSON 字符串。如果输入为 null，则返回空字符串。
     */
    public @NotNull static String componentToString(@Nullable Component component) {
        HolderLookup.Provider registries = BattleRoyale.getStaticRegistries();
        if (component == null || registries == null) {
            return "";
        }
        return Component.Serializer.toJson(component, registries);
    }
}