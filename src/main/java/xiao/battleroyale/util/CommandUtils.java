package xiao.battleroyale.util;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.Component;

import java.util.Set;

import static net.minecraft.nbt.Tag.TAG_BYTE;
import static net.minecraft.nbt.Tag.TAG_INT;

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

    /**
     * [number]
     * 数字带指令
     */
    public static MutableComponent buildRunnableIntBracket(int number, String command) {
        MutableComponent component = Component.empty();
        component.append(Component.literal("["))
                .append(Component.literal(String.valueOf(number))
                        .setStyle(Style.EMPTY
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(command)))
                        ))
                .append(Component.literal("]"));

        return component;
    }
    public static MutableComponent buildSuggestableIntBracket(int number, String command) {
        MutableComponent component = Component.empty();
        component.append(Component.literal("["))
                .append(Component.literal(String.valueOf(number))
                        .setStyle(Style.EMPTY
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(command)))
                        ))
                .append(Component.literal("]"));

        return component;
    }

    /**
     * [number]
     * 数字带颜色+指令
     */
    public static MutableComponent buildRunnableIntBracketWithColor(int number, String command, ChatFormatting chatFormatting) {
        return buildRunnableIntBracketWithColor(number, command, TextColor.fromLegacyFormat(chatFormatting));
    }
    public static MutableComponent buildSuggestableIntBracketWithColor(int number, String command, ChatFormatting chatFormatting) {
        return buildSuggestableIntBracketWithColor(number, command, TextColor.fromLegacyFormat(chatFormatting));
    }
    public static MutableComponent buildRunnableIntBracketWithColor(int number, String command, TextColor textColor) {
        MutableComponent component = Component.empty();
        component.append(Component.literal("[").withStyle(Style.EMPTY.withColor(textColor)))
                .append(Component.literal(String.valueOf(number))
                        .setStyle(Style.EMPTY
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(command)))
                        ))
                .append(Component.literal("]").withStyle(Style.EMPTY.withColor(textColor)));

        return component;
    }
    public static MutableComponent buildSuggestableIntBracketWithColor(int number, String command, TextColor textColor) {
        MutableComponent component = Component.empty();
        component.append(Component.literal("[").withStyle(Style.EMPTY.withColor(textColor)))
                .append(Component.literal(String.valueOf(number))
                        .setStyle(Style.EMPTY
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(command)))
                        ))
                .append(Component.literal("]").withStyle(Style.EMPTY.withColor(textColor)));

        return component;
    }
    /**
     * [number]
     * 数字和括号均带颜色+指令
     */
    public static MutableComponent buildRunnableIntBracketWithFullColor(int number, String command, TextColor textColor) {
        return Component.empty()
                .append(Component.literal("[" + number + "]")
                .setStyle(Style.EMPTY
                        .withColor(textColor)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(command)))));
    }
    public static MutableComponent buildSuggestableIntBracketWithFullColor(int number, String command, TextColor textColor) {
        return Component.empty()
                .append(Component.literal("[" + number + "]")
                        .setStyle(Style.EMPTY
                                .withColor(textColor)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(command)))));
    }

    /**
     * Total:int
     * key1:value1
     * key2:value2
     * ...
     */
    public static MutableComponent buildNbtVerticalList(CompoundTag nbt) {
        MutableComponent nbtComponent = Component.empty();
        Set<String> keys = nbt.getAllKeys();
        nbtComponent.append(Component.literal("Total"))
                .append(Component.literal(":" + keys.size()));
        for (String key : keys) {
            nbtComponent.append(Component.literal("\n"));
            byte type = nbt.getTagType(key);
            switch (type) {
                case TAG_INT -> {
                    int x = nbt.getInt(key);
                    nbtComponent.append(Component.literal(key).withStyle(x != 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                            .append(Component.literal(":" + x));
                }
                case TAG_BYTE -> {
                    boolean bool = nbt.getBoolean(key);
                    nbtComponent.append(Component.literal(key).withStyle(bool ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                            .append(Component.literal(":" + bool));
                }
                default -> {
                    nbtComponent.append(Component.literal(key))
                            .append(":" + nbt.get(key));
                }
            }
        }

        return nbtComponent;
    }
}