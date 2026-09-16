package xiao.battleroyale.util;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.minecraft.CommandLevel;

import java.util.Set;
import java.util.function.Consumer;

import static net.minecraft.nbt.Tag.TAG_BYTE;
import static net.minecraft.nbt.Tag.TAG_INT;

public class CommandUtils {

    public static String buildVanillaTeleport(Vec3 pos) {
        return  "/tp " + pos.x + " " + pos.y + " " + pos.z;
    }
    public static String buildNearestEntitySelect(Entity entity) {
        return "@p[name=" + entity.getName().getString() + "]";
    }

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

    public static MutableComponent buildRunnableText(String displayText, String command, ChatFormatting chatFormatting) {
        return buildRunnableText(Component.literal(displayText), command, chatFormatting);
    }
    public static MutableComponent buildRunnableText(MutableComponent mutableComponent, String command, ChatFormatting chatFormatting) {
        return Component.empty()
                .append(mutableComponent.setStyle(Style.EMPTY.withColor(chatFormatting)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(command)))));
    }

    /**
     * 点击坐标传送
     */
    public static MutableComponent buildRunnableVec(Vec3 pos) {
        String command = buildVanillaTeleport(pos);
        return buildRunnableVec(pos, command, command);
    }
    public static MutableComponent buildRunnableVec(Vec3 pos, String displayName) {
        return buildRunnableVec(pos, buildVanillaTeleport(pos), displayName);
    }
    public static MutableComponent buildRunnableVec(Vec3 pos, String command, String displayName) {
        MutableComponent component = Component.empty();
        component.append(Component.literal(pos.toString())
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(displayName))))
        );

        return component;
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

    public static MutableComponent buildIntBracketWithColor(int number, TextColor textColor) {
        return Component.empty()
                .append(Component.literal("["))
                .append(Component.literal(String.valueOf(number))
                        .setStyle(Style.EMPTY.withColor(textColor)))
                .append(Component.literal("]"));
    }
    public static MutableComponent buildIntBracketWithFullColor(int number, TextColor textColor) {
        return Component.empty()
                .append(Component.literal(String.format("[%d]", number)))
                .setStyle(Style.EMPTY
                        .withColor(textColor));
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

    /**
     * Total:int
     * {key1:value1,key2:value2}
     * {key3:value3}
     */
    public static MutableComponent buildNbtListVerticalList(ListTag nbtList) {
        MutableComponent nbtComponent = Component.empty();
        int size = nbtList.size();
        nbtComponent.append(Component.literal("Total"))
                .append(Component.literal(":" + size));
        for (int i = 0; i < size; i++) {
            nbtComponent.append(Component.literal("\n"));
            Tag tag = nbtList.get(i);
            nbtComponent.append(Component.literal(tag.toString()).withStyle(tag.getId() == Tag.TAG_COMPOUND ? ChatFormatting.WHITE : ChatFormatting.DARK_AQUA));
        }

        return nbtComponent;
    }

    /**
     * (封装方法) 立即执行 function
     * 规避 1.20.4 指令队列
     * 替换原先的 {@link ServerFunctionManager#execute(CommandFunction, CommandSourceStack)}
     */
    public static void executeCommand(ServerFunctionManager manager, CommandFunction<CommandSourceStack> function, CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        ProfilerFiller profiler = server.getProfiler();
        profiler.push(() -> "function " + function.id());

        try {
            InstantiatedFunction<CommandSourceStack> instantiated =
                    function.instantiate(null, manager.getDispatcher());
            executeCommandInContext(source, (context) -> {
                ExecutionContext.queueInitialFunctionCall(context, instantiated, source, CommandResultCallback.EMPTY);
            });

        } catch (FunctionInstantiationException e) {
            BattleRoyale.LOGGER.error("Failed to instantiate function {}: {}", function.id(), e.getMessage());
        } catch (Exception e) {
            BattleRoyale.LOGGER.warn("Failed to execute function {}", function.id(), e);
        } finally {
            profiler.pop();
        }
    }

    /**
     * 逻辑对应原版 {@link Commands#executeCommandInContext} 的同步实现部分
     * 但去掉了对 CURRENT_EXECUTION_CONTEXT 的检查
     */
    public static void executeCommandInContext(CommandSourceStack source, Consumer<ExecutionContext<CommandSourceStack>> task) {
        MinecraftServer server = source.getServer();
        if (true) {
            int i = Math.max(1, server.getGameRules().getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH));
            int j = server.getGameRules().getInt(GameRules.RULE_MAX_COMMAND_FORK_COUNT);

            try {
                ExecutionContext<CommandSourceStack> executioncontext1 = new ExecutionContext<>(i, j, server.getProfiler());

                try {
                    task.accept(executioncontext1);
                    executioncontext1.runCommandQueue();
                } catch (Throwable var15) {
                    try {
                        executioncontext1.close();
                    } catch (Throwable var14) {
                        var15.addSuppressed(var14);
                    }

                    throw var15;
                } finally {
                    executioncontext1.close();
                }
            } finally {
                ;
            }
        } else {
            ;
        }
    }

    /**
     * @param commandLevel 请使用 {@link CommandLevel}，并注意隐藏返回值类型 (或者用 {@code var})
     * @return 新的 CommandSourceStack
     */
    public static CommandSourceStack sourceStack(CommandSource source,
                                                 Vec3 position,
                                                 Vec2 rotation,
                                                 ServerLevel level,
                                                 int commandLevel,
                                                 String textName,
                                                 Component displayName,
                                                 MinecraftServer server,
                                                 @Nullable Entity entity) {
        if (entity != null) {
            return new CommandSourceStack( // _SourceStack.entity(
                    source,
                    position,
                    rotation,
                    level,
                    commandLevel,
                    textName, // 26.3移除
                    displayName, // 26.3移除
                    server,
                    entity
            );
        } else {
            return new CommandSourceStack( // _SourceStack.name(
                    source,
                    position,
                    rotation,
                    level,
                    commandLevel,
                    textName, // 26.3移除
                    displayName,
                    server
                    , null // 26.3移除
            );
        }
    }

    /**
     * 外部调用请使用 {@link CommandUtils#sourceStack}
     * <ul>
     *     保留1.20.1-26.2参数写法的原因:
     *     <li>已经有大量使用，可以查找替换{@code return new CommandSourceStack(} -> {@code CommandUtils.sourceStack(}</li>
     *     <li>即使用了Access Transformer，26.3的参数也对不上，额外抽一个AT接口（到平台层实现）会很迷</li>
     * </ul>
     * 即最终选择的做法是：全局查找替换 + one line import
     */
    @ApiStatus.AvailableSince("26.3")
    @ApiStatus.Internal
    public static class _SourceStack {

        /**
         * Entity version
         */
        public static CommandSourceStack entity(CommandSource source,
                                                Vec3 position,
                                                Vec2 rotation,
                                                ServerLevel level,
                                                int commandLevel,
                                                MinecraftServer server,
                                                Entity entity) {
            return new CommandSourceStack(
                    source,
                    position,
                    rotation,
                    level,
                    commandLevel,
                    "", // 26.3移除
                    Component.empty(), // 26.3移除
                    server,
                    entity
            );
        }

        public static CommandSourceStack name(CommandSource source,
                                              Vec3 position,
                                              Vec2 rotation,
                                              ServerLevel level,
                                              int commandLevel,
                                              Component name,
                                              MinecraftServer server) {
            return new CommandSourceStack(
                    source,
                    position,
                    rotation,
                    level,
                    commandLevel,
                    "", // 26.3移除
                    name,
                    server
                    , null // 26.3移除
            );
        }
    }
}