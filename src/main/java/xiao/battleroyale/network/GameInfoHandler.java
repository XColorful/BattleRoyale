package xiao.battleroyale.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.network.message.ClientMessageGameInfo;
import xiao.battleroyale.network.message.ClientMessageTeamInfo;
import xiao.battleroyale.network.message.ClientMessageZoneInfo;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class GameInfoHandler {
    private static final String PROTOCOL_VERSION = "1.1";

    public static final SimpleChannel GAME_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BattleRoyale.MOD_ID, "game_channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static final AtomicInteger ID_COUNT = new AtomicInteger(0);

    public static void init() {
        GAME_CHANNEL.registerMessage(
                ID_COUNT.getAndIncrement(),
                ClientMessageZoneInfo.class,
                (message, buffer) -> message.encode(message, buffer),
                ClientMessageZoneInfo::decode,
                (message, contextSupplier) -> message.handle(message, contextSupplier),
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        GAME_CHANNEL.registerMessage(
                ID_COUNT.getAndIncrement(),
                ClientMessageTeamInfo.class,
                (message, buffer) -> message.encode(message, buffer),
                ClientMessageTeamInfo::decode,
                (message, contextSupplier) -> message.handle(message, contextSupplier),
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        GAME_CHANNEL.registerMessage(
                ID_COUNT.getAndIncrement(),
                ClientMessageGameInfo.class,
                (message, buffer) -> message.encode(message, buffer),
                ClientMessageGameInfo::decode,
                (message, contextSupplier) -> message.handle(message, contextSupplier),
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }

    public static void sendToAllPlayers(Object message) {
        GAME_CHANNEL.send(PacketDistributor.ALL.noArg(), message);
    }

    public static void sendToPlayer(@NotNull ServerPlayer player, Object message) {
        GAME_CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                message
        );
    }
}