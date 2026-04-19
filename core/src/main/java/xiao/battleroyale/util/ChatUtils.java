package xiao.battleroyale.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class ChatUtils {

    // Chat to all players
    private static boolean messageToAll = true;
    private static boolean translatableToAll = true;
    private static boolean componentToAll = true;
    private static boolean titleToAll = true;
    // Chat to player
    private static boolean messageToPlayer = true;
    private static boolean translatableToPlayer = true;
    private static boolean componentToPlayer = true;
    private static boolean titleToPlayer = true;
    private static boolean actionBarToPlayer = true;

    // Getters & Setters
    public static boolean isMessageToAll() { return messageToAll; }
    public static void setMessageToAll(boolean bool) { messageToAll = bool; }
    public static boolean isTranslatableToAll() { return translatableToAll; }
    public static void setTranslatableToAll(boolean bool) { translatableToAll = bool; }
    public static boolean isComponentToAll() { return componentToAll; }
    public static void setComponentToAll(boolean bool) { componentToAll = bool; }
    public static boolean isTitleToAll() { return titleToAll; }
    public static void setTitleToAll(boolean bool) { titleToAll = bool; }
    public static void setToAll(boolean bool) {
        setMessageToAll(bool);
        setTranslatableToAll(bool);
        setComponentToAll(bool);
        setTitleToAll(bool);
    }
    public static boolean isMessageToPlayer() { return messageToPlayer; }
    public static void setMessageToPlayer(boolean bool) { messageToPlayer = bool; }
    public static boolean isTranslatableToPlayer() { return translatableToPlayer; }
    public static void setTranslatableToPlayer(boolean bool) { translatableToPlayer = bool; }
    public static boolean isComponentToPlayer() { return componentToPlayer; }
    public static void setComponentToPlayer(boolean bool) { componentToPlayer = bool; }
    public static boolean isTitleToPlayer() { return titleToPlayer; }
    public static void setTitleToPlayer(boolean bool) { titleToPlayer = bool; }
    public static boolean isActionBarToPlayer() { return actionBarToPlayer; }
    public static void setActionBarToPlayer(boolean bool) { actionBarToPlayer = bool; }
    public static void setToPlayer(boolean bool) {
        setMessageToPlayer(bool);
        setTranslatableToPlayer(bool);
        setComponentToPlayer(bool);
        setTitleToPlayer(bool);
        setActionBarToPlayer(bool);
    }


    /**
     * 向所有在线玩家发送普通文本消息。
     * @param serverLevel 当前的 ServerLevel。
     * @param message 要发送的字符串消息。
     */
    public static void sendMessageToAllPlayers(@NotNull ServerLevel serverLevel, String message) {
        if (!messageToAll) return;
        MinecraftServer server = serverLevel.getServer();
        Component textComponent = Component.literal(message);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(textComponent);
        }
    }

    /**
     * 向所有在线玩家发送 Minecraft 组件消息。
     * @param serverLevel 当前的 ServerLevel。
     * @param textComponent 要发送的 Minecraft Component 对象。
     */
    @Deprecated public static void sendMessageToAllPlayers(@NotNull ServerLevel serverLevel, Component textComponent) {
        sendComponentMessageToAllPlayers(serverLevel, textComponent);
    }

    /**
     * 向所有在线玩家发送可翻译的文本消息。
     * @param serverLevel 当前的 ServerLevel。
     * @param translationKey 翻译键。
     * @param args 翻译参数。
     */
    public static void sendTranslatableMessageToAllPlayers(@NotNull ServerLevel serverLevel, String translationKey, Object... args) {
        if (!translatableToAll) return;
        Component translatableComponent = Component.translatable(translationKey, args);
        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            player.sendSystemMessage(translatableComponent);
        }
    }

    /**
     * 向所有在线玩家发送可翻译的 Minecraft 组件消息。
     * @param serverLevel 当前的 ServerLevel。
     * @param component 要发送的可翻译的 Minecraft Component 对象。
     */
    public static void sendComponentMessageToAllPlayers(@NotNull ServerLevel serverLevel, Component component) {
        if (!componentToAll) return;
        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            player.sendSystemMessage(component);
        }
    }

    /**
     * 向所有在线玩家发送屏幕中央的标题和副标题。
     * @param serverLevel 当前的 ServerLevel。
     * @param title 标题 Component。
     * @param subtitle 副标题 Component。
     * @param fadeInTicks 标题淡入时间 (ticks)。
     * @param stayTicks 标题显示时间 (ticks)。
     * @param fadeOutTicks 标题淡出时间 (ticks)。
     */
    public static void sendTitleToAllPlayers(@NotNull ServerLevel serverLevel, Component title, Component subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        if (!titleToAll) return;
        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            player.connection.send(new ClientboundSetTitlesAnimationPacket(fadeInTicks, stayTicks, fadeOutTicks)); // 动画时间设置包
            player.connection.send(new ClientboundSetSubtitleTextPacket(subtitle)); // 副标题内容包
            player.connection.send(new ClientboundSetTitleTextPacket(title)); // 标题内容包
        }
    }

    /**
     * 向特定玩家发送普通文本消息。
     * @param player 接收消息的 ServerPlayer 对象。
     * @param message 要发送的字符串消息。
     */
    public static void sendMessageToPlayer(@NotNull ServerPlayer player, String message) {
        if (!messageToPlayer) return;
        Component textComponent = Component.literal(message);
        player.sendSystemMessage(textComponent);
    }

    /**
     * 向特定玩家发送 Minecraft 组件消息。
     * @param player 接收消息的 ServerPlayer 对象。
     * @param textComponent 要发送的 Minecraft Component 对象。
     */
    @Deprecated public static void sendMessageToPlayer(@NotNull ServerPlayer player, Component textComponent) {
        sendComponentMessageToPlayer(player, textComponent);
    }

    /**
     * 向特定玩家发送可翻译的文本消息。
     * @param player 接收消息的 ServerPlayer 对象。
     * @param translationKey 翻译键。
     * @param args 翻译参数。
     */
    public static void sendTranslatableMessageToPlayer(@NotNull ServerPlayer player, String translationKey, Object... args) {
        if (!translatableToPlayer) return;
        Component translatableComponent = Component.translatable(translationKey, args);
        player.sendSystemMessage(translatableComponent);
    }

    /**
     * 向特定玩家发送可翻译的 Minecraft 组件消息。
     * @param player 接收消息的 ServerPlayer 对象。
     * @param component 要发送的可翻译的 Minecraft Component 对象。
     */
    public static void sendComponentMessageToPlayer(@NotNull ServerPlayer player, Component component) {
        if (!componentToPlayer) return;
        player.sendSystemMessage(component);
    }

    /**
     * 向特定玩家发送可点击的 Minecraft 组件消息。
     * @param player 接收消息的 ServerPlayer 对象。
     * @param clickableComponent 要发送的可点击的 Minecraft Component 对象。
     */
    public static void sendClickableMessageToPlayer(@NotNull ServerPlayer player, Component clickableComponent) {
        if (!componentToPlayer) return;
        player.sendSystemMessage(clickableComponent);
    }

    /**
     * 向特定玩家发送屏幕中央的标题和副标题。
     * @param player 接收消息的 ServerPlayer 对象。
     * @param title 标题 Component。
     * @param subtitle 副标题 Component。
     * @param fadeInTicks 标题淡入时间 (ticks)。
     * @param stayTicks 标题显示时间 (ticks)。
     * @param fadeOutTicks 标题淡出时间 (ticks)。
     */
    public static void sendTitlesToPlayer(@NotNull ServerPlayer player, Component title, Component subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        if (!titleToPlayer) return;
        sendTitleAnimationToPlayer(player, fadeInTicks, stayTicks, fadeOutTicks);
        sendTitlesToPlayer(player, title, subtitle);
    }
    public static void sendTitleAnimationToPlayer(@NotNull ServerPlayer player, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        if (!titleToPlayer) return;
        player.connection.send(new ClientboundSetTitlesAnimationPacket(fadeInTicks, stayTicks, fadeOutTicks)); // 动画时间设置包
    }
    public static void sendTitlesToPlayer(@NotNull ServerPlayer player, Component title, Component subtitle) {
        if (!titleToPlayer) return;
        player.connection.send(new ClientboundSetSubtitleTextPacket(subtitle)); // 先发送副标题内容包
        player.connection.send(new ClientboundSetTitleTextPacket(title)); // 标题内容包
    }
    public static void sendActionBarToPlayer(@NotNull ServerPlayer player, Component actionBar) {
        if (!actionBarToPlayer) return;
        player.connection.send(new ClientboundSetActionBarTextPacket(actionBar)); // 动作条消息包
    }
}