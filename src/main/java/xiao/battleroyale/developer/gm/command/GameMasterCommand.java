package xiao.battleroyale.developer.gm.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.api.data.io.DevDataTag;
import xiao.battleroyale.data.io.DevDataManager;
import xiao.battleroyale.developer.gm.command.sub.*;
import xiao.battleroyale.developer.gm.command.sub.original.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static xiao.battleroyale.developer.gm.command.CommandArg.*;

public class GameMasterCommand {

    private static final Map<UUID, String> gameMasters = new HashMap<>();
    private static void reloadGameMasters() {
        Map<UUID, String> loadedGameMasters = DevDataManager.get().getJsonUUIDStringMap(DevDataTag.GAMEMASTER, DevDataTag.GAMEMASTERS);
        gameMasters.clear();
        gameMasters.putAll(loadedGameMasters);
    }
    public static boolean isGameMasterCall(CommandSourceStack source) {
        if (!(source.source instanceof ServerPlayer player)) {
            return false;
        }
        String playerName = player.getName().getString();
        String registeredName = gameMasters.get(player.getUUID());
        return playerName.equals(registeredName);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        reloadGameMasters();
        dispatcher.register(get(GM_MOD, true));
        dispatcher.register(get(GM_MOD_SHORT, false));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> get(String rootName, boolean useFullName) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(rootName);
        // TODO 删除测试
        root.requires(source -> source.hasPermission(4)); // root.requires(GameMasterCommand::isGameMasterCall);
        LiteralArgumentBuilder<CommandSourceStack> gmCommand = Commands.literal(useFullName ? GAME_MASTER : GAME_MASTER_SHORT);

        // GM
        DebugManager.addServer(gmCommand, useFullName);
        GameManager.addServer(gmCommand, useFullName);
        LootManager.addServer(gmCommand, useFullName);
        MessageManager.addServer(gmCommand, useFullName);
        EffectManager.addServer(gmCommand, useFullName);
        // Original GM
        GmManager.addServer(gmCommand, useFullName);
        GmProtect.addServer(gmCommand, useFullName);
        VanillaJava.addServer(gmCommand, useFullName);
        GmCapability.addServer(gmCommand, useFullName);
        GmClient.addServer(gmCommand, useFullName);
        root.then(gmCommand);
        return root;
    }
}
