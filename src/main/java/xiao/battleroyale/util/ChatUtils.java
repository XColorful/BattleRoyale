package xiao.battleroyale.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class ChatUtils {

    /**
     * 向所有在线玩家发送普通文本消息。
     * @param serverLevel 当前的 ServerLevel。
     * @param message 要发送的字符串消息。
     */
    public static void sendMessageToAllPlayers(@NotNull ServerLevel serverLevel, String message) {
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
    public static void sendMessageToAllPlayers(@NotNull ServerLevel serverLevel, Component textComponent) {
        MinecraftServer server = serverLevel.getServer();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(textComponent);
        }
    }

    /**
     * 向所有在线玩家发送可翻译的文本消息。
     * @param serverLevel 当前的 ServerLevel。
     * @param translationKey 翻译键。
     * @param args 翻译参数。
     */
    public static void sendTranslatableMessageToAllPlayers(@NotNull ServerLevel serverLevel, String translationKey, Object... args) {
        Component translatableComponent = Component.translatable(translationKey, args);
        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            player.sendSystemMessage(translatableComponent);
        }
    }

    /**
     * 向所有在线玩家发送可翻译的 Minecraft 组件消息。
     * @param serverLevel 当前的 ServerLevel。
     * @param translatableComponent 要发送的可翻译的 Minecraft Component 对象。
     */
    public static void sendTranslatableMessageToAllPlayers(@NotNull ServerLevel serverLevel, Component translatableComponent) {
        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            player.sendSystemMessage(translatableComponent);
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
        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            player.connection.send(new ClientboundSetTitlesAnimationPacket(fadeInTicks, stayTicks, fadeOutTicks)); // 动画时间设置包
            player.connection.send(new ClientboundSetTitleTextPacket(title)); // 标题内容包
            player.connection.send(new ClientboundSetSubtitleTextPacket(subtitle)); // 副标题内容包
        }
    }

    /**
     * 向特定玩家发送普通文本消息。
     * @param player 接收消息的 ServerPlayer 对象。
     * @param message 要发送的字符串消息。
     */
    public static void sendMessageToPlayer(@NotNull ServerPlayer player, String message) {
        Component textComponent = Component.literal(message);
        player.sendSystemMessage(textComponent);
    }

    /**
     * 向特定玩家发送 Minecraft 组件消息。
     * @param player 接收消息的 ServerPlayer 对象。
     * @param textComponent 要发送的 Minecraft Component 对象。
     */
    public static void sendMessageToPlayer(@NotNull ServerPlayer player, Component textComponent) {
        player.sendSystemMessage(textComponent);
    }

    /**
     * 向特定玩家发送可翻译的文本消息。
     * @param player 接收消息的 ServerPlayer 对象。
     * @param translationKey 翻译键。
     * @param args 翻译参数。
     */
    public static void sendTranslatableMessageToPlayer(@NotNull ServerPlayer player, String translationKey, Object... args) {
        Component translatableComponent = Component.translatable(translationKey, args);
        player.sendSystemMessage(translatableComponent);
    }

    /**
     * 向特定玩家发送可翻译的 Minecraft 组件消息。
     * @param player 接收消息的 ServerPlayer 对象。
     * @param translatableComponent 要发送的可翻译的 Minecraft Component 对象。
     */
    public static void sendTranslatableMessageToPlayer(@NotNull ServerPlayer player, Component translatableComponent) {
        player.sendSystemMessage(translatableComponent);
    }

    /**
     * 向特定玩家发送可点击的 Minecraft 组件消息。
     * @param player 接收消息的 ServerPlayer 对象。
     * @param clickableComponent 要发送的可点击的 Minecraft Component 对象。
     */
    public static void sendClickableMessageToPlayer(@NotNull ServerPlayer player, Component clickableComponent) {
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
    public static void sendTitleToPlayer(@NotNull ServerPlayer player, Component title, Component subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        player.connection.send(new ClientboundSetTitlesAnimationPacket(fadeInTicks, stayTicks, fadeOutTicks)); // 动画时间设置包
        player.connection.send(new ClientboundSetTitleTextPacket(title)); // 标题内容包
        player.connection.send(new ClientboundSetSubtitleTextPacket(subtitle)); // 副标题内容包
    }
}