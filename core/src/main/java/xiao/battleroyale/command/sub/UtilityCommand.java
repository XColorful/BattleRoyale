package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.team.ITeamManager;
import xiao.battleroyale.api.minecraft.CommandLevel;
import xiao.battleroyale.common.server.utility.ConfigGenerator;
import xiao.battleroyale.common.server.utility.SurvivalLobby;

import static xiao.battleroyale.command.CommandArg.*;

public class UtilityCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        // 不需要权限
        LiteralArgumentBuilder<CommandSourceStack> utilityCommand = Commands.literal(UTILITY)
                // 生存模式大厅
                .then(Commands.literal(SURVIVAL_LOBBY)
                        .executes(UtilityCommand::survivalLobby))
                .then(Commands.literal(TO_SURVIVAL_LOBBY)
                        .executes(UtilityCommand::toSurvivalLobby));

        // 需要权限
        utilityCommand.then(Commands.literal(LOOT_CONFIG)
                .requires(CommandLevel.hasPermission(3))
                .then(Commands.argument(ID, IntegerArgumentType.integer())
                        .then(Commands.argument(TYPE, StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    builder.suggest(SLOT);
                                    builder.suggest(BLOCK);
                                    builder.suggest(CHUNK);
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                                        .executes(UtilityCommand::autoLootConfigSimple)
                                        .then(Commands.argument(REPEAT, IntegerArgumentType.integer(1))
                                                .then(Commands.argument(BASE_WEIGHT, IntegerArgumentType.integer(1))
                                                        .then(Commands.argument(CHUNK_RADIUS, IntegerArgumentType.integer(0))
                                                                .then(Commands.argument(AUTO_RELOAD, BoolArgumentType.bool())
                                                                        .executes(UtilityCommand::autoLootConfig)
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .requires(CommandLevel.hasPermission(4)) // 战利品表读不了就直接进不了存档
                        .then(Commands.literal(TO_LOOT_TABLE)
                                .then(Commands.argument(FILE, StringArgumentType.string())
                                        .executes(UtilityCommand::toLootTable)
                                )
                        )
                )
        );

        utilityCommand.then(Commands.literal(PROFILE)
                .requires(CommandLevel.hasPermission(2))
                .then(Commands.literal(SAVE)
                        .then(Commands.argument(ID, IntegerArgumentType.integer())
                                .then(Commands.argument(OVERWRITE, BoolArgumentType.bool())
                                        .executes(UtilityCommand::saveProfile)
                                )
                        )
                )
                .then(Commands.literal(LOAD)
                        .then(Commands.argument(ID, IntegerArgumentType.integer())
                                .executes(UtilityCommand::loadProfile)
                        )
                )
        );

        utilityCommand.then(Commands.literal(TEAM)
                .requires(CommandLevel.hasPermission(2))
                .then(Commands.literal(REMOVE)
                        .then(Commands.argument(GAME_TEAM_ONLY, BoolArgumentType.bool())
                                .executes(UtilityCommand::removeVanillaTeam)
                        )
                )
                .then(Commands.literal(REBUILD)
                        .then(Commands.argument(FORMAT_STRING, StringArgumentType.string())
                                .then(Commands.argument(HIDE_NAME, BoolArgumentType.bool())
                                        .then(Commands.argument(FORCE_REBUILD, BoolArgumentType.bool())
                                                .executes(UtilityCommand::rebuildVanillaTeam)
                                        )
                                )
                        )
                )
        );

        return utilityCommand;
    }

    private static int survivalLobby(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (source.isPlayer()) {
            ServerPlayer player = source.getPlayerOrException();
            SurvivalLobby.get().sendLobbyInfo(player);
        } else {
            ServerLevel serverLevel = source.getLevel();
            SurvivalLobby.get().sendLobbyInfo(serverLevel);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int toSurvivalLobby(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (source.isPlayer()) {
            ServerPlayer player = source.getPlayerOrException();
            SurvivalLobby.get().teleportToLobby(player);
            return Command.SINGLE_SUCCESS;
        } else {
            return 0;
        }
    }

    private static int autoLootConfigSimple(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return autoLootConfig(context, 1, 1, 0, true);
    }
    private static int autoLootConfig(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int repeat = IntegerArgumentType.getInteger(context, REPEAT);
        int weight = IntegerArgumentType.getInteger(context, BASE_WEIGHT);
        int radius = IntegerArgumentType.getInteger(context, CHUNK_RADIUS);
        boolean autoReload = BoolArgumentType.getBool(context, AUTO_RELOAD);
        return autoLootConfig(context, repeat, weight, radius, autoReload);
    }
    private static int autoLootConfig(CommandContext<CommandSourceStack> context, int repeat, int weight, int radius, boolean autoReload) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        @Nullable ServerPlayer player = source.isPlayer() ? source.getPlayerOrException() : null;
        int id = IntegerArgumentType.getInteger(context, ID);
        String type = StringArgumentType.getString(context, TYPE);
        Vec3 pos =  Vec3Argument.getVec3(context, XYZ);
        if (ConfigGenerator.autoLootConfig(player, context.getSource().getLevel(),
                id,
                type, pos,
                repeat, weight, radius, autoReload)) {
            return Command.SINGLE_SUCCESS;
        } else {
            return 0;
        }
    }
    private static int toLootTable(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        int id = IntegerArgumentType.getInteger(context, ID);
        String fileName = StringArgumentType.getString(context, FILE);
        if (ConfigGenerator.toLootTable(source, id, fileName)) {
            return Command.SINGLE_SUCCESS;
        } else {
            return 0;
        }
    }

    private static int saveProfile(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        int id = IntegerArgumentType.getInteger(context, ID);
        boolean overwrite = BoolArgumentType.getBool(context, OVERWRITE);

        int saved = BattleRoyale.getServerManager().getProfileManager().saveCurrentProfile(source, source.getLevel(), id, overwrite);
        return saved >= 0 ? Command.SINGLE_SUCCESS : 0;
    }
    private static int loadProfile(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        int id = IntegerArgumentType.getInteger(context, ID);

        int loaded = BattleRoyale.getServerManager().getProfileManager().loadProfile(source, source.getLevel(), id);
        return loaded >= 0 ? Command.SINGLE_SUCCESS : 0;
    }

    private static int removeVanillaTeam(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        boolean removeGameTeamOnly = BoolArgumentType.getBool(context, GAME_TEAM_ONLY);
        ITeamManager teamManager = BattleRoyale.getGameManager().getTeamManager();
        int removedTotal = teamManager.removeVanillaTeam(source.getLevel(), removeGameTeamOnly);
        if (removedTotal > 0) {
            source.sendSuccess(() -> Component.translatable("battleroyale.message.remove_vanilla_team", removedTotal), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.translatable("battleroyale.message.no_vanilla_team_removed"));
            return 0;
        }
    }
    private static int rebuildVanillaTeam(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String formatString = StringArgumentType.getString(context, FORMAT_STRING);
        boolean hideName = BoolArgumentType.getBool(context, HIDE_NAME);
        boolean forceRebuild = BoolArgumentType.getBool(context, FORCE_REBUILD);
        ITeamManager teamManager = BattleRoyale.getGameManager().getTeamManager();
        if (teamManager.buildVanillaTeam(source.getLevel(), formatString, hideName, forceRebuild)) {
            source.sendSuccess(() -> Component.translatable("battleroyale.message.rebuild_vanilla_team"), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.translatable("battleroyale.message.no_vanilla_team_rebuild"));
            return 0;
        }
    }
}