package xiao.battleroyale.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ColorUtils {

    public static final int[] FIREWORK_COLORS = {
            // DyeColor.BLACK.getFireworkColor(),
            DyeColor.BLUE.getFireworkColor(),
            // DyeColor.BROWN.getFireworkColor(),
            DyeColor.CYAN.getFireworkColor(),
            // DyeColor.GRAY.getFireworkColor(),
            DyeColor.GREEN.getFireworkColor(),
            DyeColor.LIGHT_BLUE.getFireworkColor(),
            // DyeColor.LIGHT_GRAY.getFireworkColor(),
            DyeColor.LIME.getFireworkColor(),
            DyeColor.MAGENTA.getFireworkColor(),
            DyeColor.ORANGE.getFireworkColor(),
            DyeColor.PINK.getFireworkColor(),
            DyeColor.PURPLE.getFireworkColor(),
            DyeColor.RED.getFireworkColor(),
            DyeColor.WHITE.getFireworkColor(),
            DyeColor.YELLOW.getFireworkColor()
    };
    private static final int COLOR_TOTAL = FIREWORK_COLORS.length;

    /**
     * 解析 #RRGGBB 或 #RRGGBBAA
     * 默认为白色
     * 默认Alpha为255
     */
    @NotNull
    public static Color parseColorFromString(String colorString) {
        Color color = Color.WHITE;
        try {
            if (colorString.length() == 9 && colorString.startsWith("#")) { // #RRGGBBAA
                int rgba = (int) Long.parseLong(colorString.substring(1), 16);
                int r = (rgba >> 24) & 0xFF;
                int g = (rgba >> 16) & 0xFF;
                int b = (rgba >> 8) & 0xFF;
                int a = rgba & 0xFF;
                color = new Color(r, g, b, a);
            } else { // #RRGGBB
                color = Color.decode(colorString); // 默认 Alpha 为 255
            }
        } catch (NumberFormatException e) {
            BattleRoyale.LOGGER.warn("Failed to decode color hex: {}, reason: {}", colorString, e.getMessage());
        }
        return color;
    }

    /**
     * 解析字符串，输出0xAARRGGBB
     */
    public static int parseColorToInt(String colorString) {
        return parseColorToInt(parseColorFromString(colorString));
    }

    /**
     * 获取颜色0xAARRGGBB
     */
    public static int parseColorToInt(Color color) {
        return color.getRGB();
    }

    /**
     * 将字符串表示的颜色的RGB应用到输入颜色
     */
    public static Color changeColorExceptAlpha(Color baseColor, String colorString) {
        Color newRGBColor = parseColorFromString(colorString);
        return new Color(newRGBColor.getRed(), newRGBColor.getGreen(), newRGBColor.getBlue(), baseColor.getAlpha());
    }

    /**
     * 生成一个随机的烟花颜色。
     * @param random 随机源实例。
     * @return 随机选取的单个烟花颜色值。
     */
    public static int generateRandomColor(RandomSource random) {
        return FIREWORK_COLORS[random.nextInt(COLOR_TOTAL)];
    }

    /**
     * 生成指定数量的不重复的随机烟花颜色数组。
     * 如果请求数量大于可用颜色总数，将返回所有可用颜色。
     * @param random 随机源实例。
     * @param count 期望生成的颜色数量。
     * @return 包含随机选取的烟花颜色值的int数组。
     */
    public static int[] generateRandomColors(RandomSource random, int count) {
        if (count <= 0) {
            return new int[0];
        }

        List<Integer> availableColors = Arrays.stream(FIREWORK_COLORS)
                .boxed()
                .collect(Collectors.toList());

        if (count >= availableColors.size()) {
            return FIREWORK_COLORS;
        }

        Collections.shuffle(availableColors, BattleRoyale.COMMON_RANDOM);

        int[] resultColors = new int[count];
        for (int i = 0; i < count; i++) {
            resultColors[i] = availableColors.get(i);
        }
        return resultColors;
    }

    /**
     * 生成一个介于min和max（包含）之间数量的不重复的随机烟花颜色数组。
     * @param random 随机源实例。
     * @param min 最小颜色数量（包含）。
     * @param max 最大颜色数量（包含）。
     * @return 包含随机选取的烟花颜色值的int数组。
     */
    public static int[] generateRandomColors(RandomSource random, int min, int max) {
        if (min < 0) min = 0;
        if (max < min) max = min;

        if (max > COLOR_TOTAL) max = COLOR_TOTAL;
        if (min > COLOR_TOTAL) min = COLOR_TOTAL;

        int count;
        if (min == max) {
            count = min;
        } else {
            count = min + random.nextInt(max - min + 1);
        }

        return generateRandomColors(random, count);
    }
}
