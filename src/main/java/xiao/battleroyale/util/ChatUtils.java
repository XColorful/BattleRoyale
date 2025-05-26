package xiao.battleroyale.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class ChatUtils {

    public static void sendMessageToAllPlayers(ServerLevel serverLevel, String message) {
        MinecraftServer server = serverLevel.getServer();
        Component textComponent = Component.literal(message);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(textComponent);
        }
    }
    public static void sendMessageToAllPlayers(ServerLevel serverLevel, Component textComponent) {
        MinecraftServer server = serverLevel.getServer();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(textComponent);
        }
    }
    public static void sendTranslatableMessageToAllPlayers(ServerLevel serverLevel, String translationKey, Object... args) {
        Component translatableComponent = Component.translatable(translationKey, args);
        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            player.sendSystemMessage(translatableComponent);
        }
    }

    public static void sendTranslatableMessageToAllPlayers(ServerLevel serverLevel, Component translatableComponent) {
        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            player.sendSystemMessage(translatableComponent);
        }
    }

    public static void sendMessageToPlayer(ServerPlayer player, String message) {
        Component textComponent = Component.literal(message);
        player.sendSystemMessage(textComponent);
    }
    public static void sendMessageToPlayer(ServerPlayer player, Component textComponent) {
        player.sendSystemMessage(textComponent);
    }
    public static void sendTranslatableMessageToPlayer(ServerPlayer player, String translationKey, Object... args) {
        Component translatableComponent = Component.translatable(translationKey, args);
        player.sendSystemMessage(translatableComponent);
    }
    public static void sendTranslatableMessageToPlayer(ServerPlayer player, Component translatableComponent) {
        player.sendSystemMessage(translatableComponent);
    }


}
