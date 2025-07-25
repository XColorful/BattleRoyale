package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.ColorUtils;
import xiao.battleroyale.util.JsonUtils;

import java.util.Collections;
import java.util.List;

public class MessageEntry implements ILootEntry {
    private final boolean onlyGamePlayer;
    private final boolean sendPosition;
    private final String messageString;
    private final String messageColor;
    private final int colorRGB;

    public MessageEntry(boolean onlyGamePlayer, boolean sendPosition, String messageString, String messageColor) {
        this.onlyGamePlayer = onlyGamePlayer;
        this.sendPosition = sendPosition;
        this.messageString = messageString;
        this.messageColor = messageColor;
        this.colorRGB = ColorUtils.parseColorToInt(messageColor) & 0xFFFFFF;
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootGenerator.LootContext lootContext, T target) {
        MutableComponent message = Component.empty();
        message.append(
                Component.translatable("battleroyale.loot.label")
                        .withStyle(ChatFormatting.WHITE)
        );

        if (sendPosition) {
            BlockPos blockPos = target.getBlockPos();
            message.append(Component.literal(" "));
            message.append(Component.translatable(
                    "battleroyale.loot.pos",
                    blockPos.getX(),
                    blockPos.getY(),
                    blockPos.getZ())
                    .withStyle(ChatFormatting.AQUA));
        }
        if (!messageString.isEmpty()) {
            message.append(Component.literal(" "));
            message.append(Component.literal(messageString)
                    .withStyle(style -> style.withColor(TextColor.fromRgb(colorRGB)))
            );
        }

        if (onlyGamePlayer) {
            List<GamePlayer> gamePlayers = GameManager.get().getGamePlayers();
            for (GamePlayer gamePlayer : gamePlayers) {
                if (gamePlayer == null) {
                    continue;
                }
                ServerPlayer player = (ServerPlayer) lootContext.serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                if (player != null) {
                    ChatUtils.sendMessageToPlayer(player, message);
                }
            }
        } else {
            ChatUtils.sendMessageToAllPlayers(lootContext.serverLevel, message);
        }
        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_MESSAGE;
    }

    @NotNull
    public static MessageEntry fromJson(JsonObject jsonObject) {
        boolean onlyGamePlayer = JsonUtils.getJsonBool(jsonObject, LootEntryTag.ONLY_GAME_PLAYER, false);
        boolean sendPosition = JsonUtils.getJsonBool(jsonObject, LootEntryTag.SEND_POSITION, true);
        String messageString = JsonUtils.getJsonString(jsonObject, LootEntryTag.MESSAGE, "");
        String messageColor = JsonUtils.getJsonString(jsonObject, LootEntryTag.MESSAGE_COLOR, "");
        return new MessageEntry(onlyGamePlayer, sendPosition, messageString, messageColor);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.ONLY_GAME_PLAYER, onlyGamePlayer);
        jsonObject.addProperty(LootEntryTag.SEND_POSITION, sendPosition);
        jsonObject.addProperty(LootEntryTag.MESSAGE, messageString);
        jsonObject.addProperty(LootEntryTag.MESSAGE_COLOR, messageColor);
        return jsonObject;
    }
}
