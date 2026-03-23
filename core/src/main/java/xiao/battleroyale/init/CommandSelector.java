package xiao.battleroyale.init;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.team.ITeamManager;
import xiao.battleroyale.api.init.ISelectorRegistry;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.data.io.TempDataManager;

import static xiao.battleroyale.api.data.TempDataTag.*;

public class CommandSelector {

    private static final CommandSelector INSTANCE = new CommandSelector();

    public static CommandSelector get() {
        return INSTANCE;
    }

    private CommandSelector() {
    }

    public void setupSelectors(ISelectorRegistry registry) {
        TempDataManager tempDataManager = TempDataManager.get();
        if (!shouldRegister(tempDataManager, ENTITY_SELECTOR)) {
            return;
        }

        if (shouldRegister(tempDataManager, SELECTOR_GAMEPLAYERS))
            SelectorRegister.gamePlayers(registry);

        if (shouldRegister(tempDataManager, SELECTOR_NONGAMEPLAYERS_PLAYER))
            SelectorRegister.nonGamePlayersPlayer(registry);

        if (shouldRegister(tempDataManager, SELECTOR_GAMEPLAYERS_PLAYER))
            SelectorRegister.gamePlayersPlayer(registry);

        if (shouldRegister(tempDataManager, SELECTOR_GAMEPLAYERS_BOT))
            SelectorRegister.gamePlayersBot(registry);

        if (shouldRegister(tempDataManager, SELECTOR_GAMEPLAYERS_DOWNED))
            SelectorRegister.gamePlayersDowned(registry);

        if (shouldRegister(tempDataManager, SELECTOR_STANDINGGAMEPLAYERS))
            SelectorRegister.standingGamePlayers(registry);

        if (shouldRegister(tempDataManager, SELECTOR_NONSTANDINGGAMEPLAYERS_PLAYER))
            SelectorRegister.nonStandingGamePlayersPlayer(registry);

        if (shouldRegister(tempDataManager, SELECTOR_STANDINGGAMEPLAYERS_PLAYER))
            SelectorRegister.standingGamePlayersPlayer(registry);

        if (shouldRegister(tempDataManager, SELECTOR_STANDINGGAMEPLAYERS_BOT))
            SelectorRegister.standingGamePlayersBot(registry);

        if (shouldRegister(tempDataManager, SELECTOR_ELIMINATEDGAMEPLAYERS))
            SelectorRegister.eliminatedGamePlayers(registry);

        if (shouldRegister(tempDataManager, SELECTOR_ELIMINATEDGAMEPLAYERS_PLAYER))
            SelectorRegister.eliminatedGamePlayersPlayer(registry);

        if (shouldRegister(tempDataManager, SELECTOR_ELIMINATEDGAMEPLAYERS_BOT))
            SelectorRegister.eliminatedGamePlayersBot(registry);
    }
    private static boolean shouldRegister(TempDataManager manager, String key) {
        Boolean bool = manager.getBool(REGISTRY, key);
        return bool != null && bool;
    }

    /**
     * 内部静态类负责具体的谓词逻辑定义与注册
     */
    public static class SelectorRegister {

        public static boolean register(ISelectorRegistry registry, String type) {
            switch (type) {
                case SELECTOR_GAMEPLAYERS -> gamePlayers(registry);
                case SELECTOR_NONGAMEPLAYERS_PLAYER -> nonGamePlayersPlayer(registry);
                case SELECTOR_GAMEPLAYERS_PLAYER -> gamePlayersPlayer(registry);
                case SELECTOR_GAMEPLAYERS_BOT -> gamePlayersBot(registry);
                case SELECTOR_GAMEPLAYERS_DOWNED -> gamePlayersDowned(registry);
                case SELECTOR_STANDINGGAMEPLAYERS -> standingGamePlayers(registry);
                case SELECTOR_NONSTANDINGGAMEPLAYERS_PLAYER -> nonStandingGamePlayersPlayer(registry);
                case SELECTOR_STANDINGGAMEPLAYERS_PLAYER -> standingGamePlayersPlayer(registry);
                case SELECTOR_STANDINGGAMEPLAYERS_BOT -> standingGamePlayersBot(registry);
                case SELECTOR_ELIMINATEDGAMEPLAYERS -> eliminatedGamePlayers(registry);
                case SELECTOR_ELIMINATEDGAMEPLAYERS_PLAYER -> eliminatedGamePlayersPlayer(registry);
                case SELECTOR_ELIMINATEDGAMEPLAYERS_BOT -> eliminatedGamePlayersBot(registry);
                default -> {
                    return false;
                }
            }
            return true;
        }

        // 全部游戏玩家 (@gameplayers)
        public static void gamePlayers(ISelectorRegistry registry) {
            registry.registerSelector(
                    "gameplayers",
                    entity -> {
                        if (!(entity instanceof LivingEntity)) return false;
                        return BattleRoyale.getGameManager().getTeamManager().getGamePlayerByUUID(entity.getUUID()) != null;
                    },
                    true,
                    Component.translatable("battleroyale.argument.entity.selector.gameplayers")
            );
        }

        // 全部非游戏玩家 (玩家) (@nongameplayers.player)
        public static void nonGamePlayersPlayer(ISelectorRegistry registry) {
            registry.registerSelector(
                    "nongameplayers.player",
                    entity -> BattleRoyale.getGameManager().getTeamManager().getGamePlayerByUUID(entity.getUUID()) == null,
                    false,
                    Component.translatable("battleroyale.argument.entity.selector.nongameplayers.player")
            );
        }

        // 全部游戏玩家 (玩家) (@gameplayers.player)
        public static void gamePlayersPlayer(ISelectorRegistry registry) {
            registry.registerSelector(
                    "gameplayers.player",
                    entity -> {
//                        if (!(entity instanceof LivingEntity)) return false;
                        @Nullable GamePlayer gamePlayer = BattleRoyale.getGameManager().getTeamManager().getGamePlayerByUUID(entity.getUUID());
                        return gamePlayer != null && !gamePlayer.isBot();
                    },
                    false,
                    Component.translatable("battleroyale.argument.entity.selector.gameplayers.player")
            );
        }

        // 全部游戏玩家 (人机) (@gameplayers.bot)
        public static void gamePlayersBot(ISelectorRegistry registry) {
            registry.registerSelector(
                    "gameplayers.bot",
                    entity -> {
                        if (!(entity instanceof LivingEntity)) return false;
                        @Nullable GamePlayer gamePlayer = BattleRoyale.getGameManager().getTeamManager().getGamePlayerByUUID(entity.getUUID());
                        return gamePlayer != null && gamePlayer.isBot();
                    },
                    true,
                    Component.translatable("battleroyale.argument.entity.selector.gameplayers.bot")
            );
        }

        // 全部倒地游戏玩家 (@gameplayers.downed)
        public static void gamePlayersDowned(ISelectorRegistry registry) {
            registry.registerSelector(
                    "gameplayers.downed",
                    entity -> {
                        if (!(entity instanceof LivingEntity)) return false;
                        @Nullable GamePlayer gamePlayer = BattleRoyale.getGameManager().getTeamManager().getGamePlayerByUUID(entity.getUUID());
                        return gamePlayer != null && gamePlayer.isDowned();
                    },
                    true,
                    Component.translatable("battleroyale.argument.entity.selector.gameplayers.downed")
            );
        }

        // 全部未被淘汰的游戏玩家 (@standinggameplayers)
        public static void standingGamePlayers(ISelectorRegistry registry) {
            registry.registerSelector(
                    "standinggameplayers",
                    entity -> {
                        if (!(entity instanceof LivingEntity)) return false;
                        return BattleRoyale.getGameManager().getTeamManager().hasStandingGamePlayer(entity.getUUID());
                    },
                    true,
                    Component.translatable("battleroyale.argument.entity.selector.standinggameplayers")
            );
        }

        // 全部非未被淘汰的游戏玩家 (玩家) (@nonstandinggameplayers.player)
        public static void nonStandingGamePlayersPlayer(ISelectorRegistry registry) {
            registry.registerSelector(
                    "nonstandinggameplayers.player",
                    entity -> {
//                        if (!(entity instanceof LivingEntity)) return false;
                        return !BattleRoyale.getGameManager().getTeamManager().hasStandingGamePlayer(entity.getUUID());
                    },
                    false,
                    Component.translatable("battleroyale.argument.entity.selector.nonstandinggameplayers.player")
            );
        }

        // 全部未被淘汰的游戏玩家 (玩家) (@standinggameplayers.player)
        public static void standingGamePlayersPlayer(ISelectorRegistry registry) {
            registry.registerSelector(
                    "standinggameplayers.player",
                    entity -> {
//                        if (!(entity instanceof LivingEntity)) return false;
                        ITeamManager teamManager = BattleRoyale.getGameManager().getTeamManager();
                        @Nullable GamePlayer gamePlayer = teamManager.getGamePlayerByUUID(entity.getUUID());
                        return gamePlayer != null && !gamePlayer.isBot() && teamManager.hasStandingGamePlayer(entity.getUUID());
                    },
                    false,
                    Component.translatable("battleroyale.argument.entity.selector.standinggameplayers.player")
            );
        }

        // 全部未被淘汰的游戏玩家 (人机) (@standinggameplayers.bot)
        public static void standingGamePlayersBot(ISelectorRegistry registry) {
            registry.registerSelector(
                    "standinggameplayers.bot",
                    entity -> {
                        if (!(entity instanceof LivingEntity)) return false;
                        ITeamManager teamManager = BattleRoyale.getGameManager().getTeamManager();
                        @Nullable GamePlayer gamePlayer = teamManager.getGamePlayerByUUID(entity.getUUID());
                        return gamePlayer != null && gamePlayer.isBot() && teamManager.hasStandingGamePlayer(entity.getUUID());
                    },
                    true,
                    Component.translatable("battleroyale.argument.entity.selector.standinggameplayers.bot")
            );
        }

        // 全部被淘汰的游戏玩家 (@eliminatedgameplayers)
        public static void eliminatedGamePlayers(ISelectorRegistry registry) {
            registry.registerSelector(
                    "eliminatedgameplayers",
                    entity -> {
                        if (!(entity instanceof LivingEntity)) return false;
                        ITeamManager teamManager = BattleRoyale.getGameManager().getTeamManager();
                        @Nullable GamePlayer gamePlayer = teamManager.getGamePlayerByUUID(entity.getUUID());
                        return gamePlayer != null && !teamManager.hasStandingGamePlayer(gamePlayer.getPlayerUUID());
                    },
                    true,
                    Component.translatable("battleroyale.argument.entity.selector.eliminatedgameplayers")
            );
        }

        // 全部被淘汰的游戏玩家 (玩家) (@eliminatedgameplayers.player)
        public static void eliminatedGamePlayersPlayer(ISelectorRegistry registry) {
            registry.registerSelector(
                    "eliminatedgameplayers.player",
                    entity -> {
//                        if (!(entity instanceof LivingEntity)) return false;
                        ITeamManager teamManager = BattleRoyale.getGameManager().getTeamManager();
                        @Nullable GamePlayer gamePlayer = teamManager.getGamePlayerByUUID(entity.getUUID());
                        return gamePlayer != null && !gamePlayer.isBot() && !teamManager.hasStandingGamePlayer(gamePlayer.getPlayerUUID());
                    },
                    false,
                    Component.translatable("battleroyale.argument.entity.selector.eliminatedgameplayers.player")
            );
        }

        // 全部被淘汰的游戏玩家 (人机) (@eliminatedgameplayers.bot)
        public static void eliminatedGamePlayersBot(ISelectorRegistry registry) {
            registry.registerSelector(
                    "eliminatedgameplayers.bot",
                    entity -> {
                        if (!(entity instanceof LivingEntity)) return false;
                        ITeamManager teamManager = BattleRoyale.getGameManager().getTeamManager();
                        @Nullable GamePlayer gamePlayer = teamManager.getGamePlayerByUUID(entity.getUUID());
                        return gamePlayer != null && gamePlayer.isBot() && !teamManager.hasStandingGamePlayer(gamePlayer.getPlayerUUID());
                    },
                    true,
                    Component.translatable("battleroyale.argument.entity.selector.eliminatedgameplayers.bot")
            );
        }
    }
}