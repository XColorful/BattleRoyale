package xiao.battleroyale.util;

import net.minecraft.ChatFormatting;
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
            if (colorString.length() > 9) {
                colorString = colorString.substring(0, 9);
            }
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
            BattleRoyale.LOGGER.debug("Invalid color:{}, defaulted", colorString);
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

    /**
     * 找到与给定RGB颜色最接近的ChatFormatting颜色。
     * @param rgbColor 24位的RGB颜色值 (0xRRGGBB)。
     * @return 匹配的ChatFormatting。如果没有合适的，返回ChatFormatting.RESET。
     */
    public static ChatFormatting getClosestChatFormatting(int rgbColor) {
        int minDistance = Integer.MAX_VALUE;
        ChatFormatting closest = ChatFormatting.RESET;

        for (ChatFormatting formatting : ChatFormatting.values()) {
            if (formatting.isColor() && formatting.getColor() != null) {
                int chatColor = formatting.getColor();
                int distance = getColorDistance(rgbColor, chatColor);
                if (distance < minDistance) {
                    minDistance = distance;
                    closest = formatting;
                }
            }
        }
        return closest;
    }
    public static ChatFormatting getClosestChatFormatting(String colorString) {
        return getClosestChatFormatting(parseColorToInt(colorString));
    }

    /**
     * 计算两个RGB颜色之间的欧几里得距离。
     */
    private static int getColorDistance(int color1, int color2) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        return (r2 - r1) * (r2 - r1) + (g2 - g1) * (g2 - g1) + (b2 - b1) * (b2 - b1);
    }
}
