package xiao.battleroyale.api.event.game.tick;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.event.EventDispatcher;

public class GameLootBfsEvent extends AbstractGameTickEvent {

    protected final int lastBfsProcessedLoot;

    public GameLootBfsEvent(IGameManager gameManager, int gameTime, int lastBfsProcessedLoot) {
        super(gameManager, gameTime);
        this.lastBfsProcessedLoot = lastBfsProcessedLoot;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_LOOT_BFS_EVENT;
    }
    
    public int getLastBfsProcessedLoot() {
        return this.lastBfsProcessedLoot;
    }

    @Override public String getTextName() {
        return "CBR GameLootBfsEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }

    private static final EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(GameLootBfsEvent.class);
    @Override public @NotNull EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
