package xiao.battleroyale.api.data;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import xiao.battleroyale.util.ClassUtils;

public class TempDataTag {

    // registry.json
    public static final String REGISTRY = "registry";
    public static final String ENTITY_SELECTOR = "entitySelector";
    public static final String SELECTOR_GAMEPLAYERS = "selector.gameplayers";
    public static final String SELECTOR_NONGAMEPLAYERS_PLAYER = "selector.nongameplayers.player";
    public static final String SELECTOR_GAMEPLAYERS_PLAYER = "selector.gameplayers.player";
    public static final String SELECTOR_GAMEPLAYERS_BOT = "selector.gameplayers.bot";
    public static final String SELECTOR_GAMEPLAYERS_DOWNED = "selector.gameplayers.downed";
    public static final String SELECTOR_STANDINGGAMEPLAYERS = "selector.standinggameplayers";
    public static final String SELECTOR_NONSTANDINGGAMEPLAYERS_PLAYER = "selector.nonstandinggameplayers.player";
    public static final String SELECTOR_STANDINGGAMEPLAYERS_PLAYER = "selector.standinggameplayers.player";
    public static final String SELECTOR_STANDINGGAMEPLAYERS_BOT = "selector.standinggameplayers.bot";
    public static final String SELECTOR_ELIMINATEDGAMEPLAYERS = "selector.eliminatedgameplayers";
    public static final String SELECTOR_ELIMINATEDGAMEPLAYERS_PLAYER = "selector.eliminatedgameplayers.player";
    public static final String SELECTOR_ELIMINATEDGAMEPLAYERS_BOT = "selector.eliminatedgameplayers.bot";
    public static final String PUBGMC_COMMAND = "pubgmcCommand";

    // gameManager.json
    public static final String GAME_MANAGER = "gameManager";
    public static final String GLOBAL_OFFSET = "globalOffset";
    public static final String LAST_GAME_ID = "lastGameId";

    // preCalculate.json
    public static final String PRE_CALCULATE = "preCalculate";
    public static final String SPAWN_MANAGER = "spawnManager";

    private TempDataTag() {}

    public static final ClassUtils.ArraySet<String> selectorTypes = new ClassUtils.ArraySet<>();
    static {
        selectorTypes.add(SELECTOR_GAMEPLAYERS);
        selectorTypes.add(SELECTOR_NONGAMEPLAYERS_PLAYER);
        selectorTypes.add(SELECTOR_GAMEPLAYERS_PLAYER);
        selectorTypes.add(SELECTOR_GAMEPLAYERS_BOT);
        selectorTypes.add(SELECTOR_GAMEPLAYERS_DOWNED);
        selectorTypes.add(SELECTOR_STANDINGGAMEPLAYERS);
        selectorTypes.add(SELECTOR_NONSTANDINGGAMEPLAYERS_PLAYER);
        selectorTypes.add(SELECTOR_STANDINGGAMEPLAYERS_PLAYER);
        selectorTypes.add(SELECTOR_STANDINGGAMEPLAYERS_BOT);
        selectorTypes.add(SELECTOR_ELIMINATEDGAMEPLAYERS);
        selectorTypes.add(SELECTOR_ELIMINATEDGAMEPLAYERS_PLAYER);
        selectorTypes.add(SELECTOR_ELIMINATEDGAMEPLAYERS_BOT);
    }
    public static String getSelectorString(String selector) {
        return "@" + selector.substring("selector.".length());
    }

    public static final SuggestionProvider<CommandSourceStack> SELECTOR_TYPE_SUGGESTS = (context, builder) ->
            SharedSuggestionProvider.suggest(selectorTypes.asList(), builder);
}