package xiao.battleroyale.util;

import net.minecraft.world.phys.Vec3;
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
    public static Vec3 parseVectorString(@Nullable String inputString) {
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
    public static String vectorToString(Vec3 vec3) {
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
}