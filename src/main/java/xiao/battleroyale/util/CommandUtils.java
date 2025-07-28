package xiao.battleroyale.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class CommandUtils {

    /**
     * 悬浮文本
     */
    public static MutableComponent buildHoverableText(String displayText, String hoverText) {
        MutableComponent fieldComp = Component.literal(displayText);
        Style style = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(hoverText)));
        return fieldComp.setStyle(style);
    }
    public static MutableComponent buildHoverableText(String displayText, MutableComponent hoverComponent) {
        MutableComponent fieldComp = Component.literal(displayText);
        Style style = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent));
        return fieldComp.setStyle(style);
    }

    public static MutableComponent buildHoverableTextWithColor(String displayText, String hoverText, String color) {
        return buildHoverableTextWithColor(displayText, hoverText, TextColor.fromRgb(ColorUtils.parseColorToInt(color)));
    }
    public static MutableComponent buildHoverableTextWithColor(String displayText, String hoverText, ChatFormatting color) {
        return buildHoverableTextWithColor(displayText, hoverText, TextColor.fromLegacyFormat(color));
    }
    public static MutableComponent buildHoverableTextWithColor(String displayText, String hoverText, TextColor textColor) {
        MutableComponent fieldComp = Component.literal(displayText);
        Style style = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(hoverText)));
        style = style.withColor(textColor);

        return fieldComp.setStyle(style);
    }
    public static MutableComponent buildHoverableTextWithColor(String displayText, MutableComponent hoverComponent, String color) {
        return buildHoverableTextWithColor(displayText, hoverComponent, TextColor.fromRgb(ColorUtils.parseColorToInt(color)));
    }
    public static MutableComponent buildHoverableTextWithColor(String displayText, MutableComponent hoverComponent, ChatFormatting color) {
        return buildHoverableTextWithColor(displayText, hoverComponent, TextColor.fromLegacyFormat(color));
    }
    public static MutableComponent buildHoverableTextWithColor(String displayText, MutableComponent hoverComponent, TextColor textColor) {
        MutableComponent fieldComp = Component.literal(displayText);
        Style style = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent));
        style = style.withColor(textColor);

        return fieldComp.setStyle(style);
    }
}