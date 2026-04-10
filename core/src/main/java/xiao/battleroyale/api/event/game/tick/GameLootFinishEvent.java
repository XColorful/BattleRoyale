package xiao.battleroyale.api.event.game.tick;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.event.EventDispatcher;

public class GameLootFinishEvent extends AbstractGameTickFinishEvent {

    protected final int lastProcessedCount;
    protected final int clearedCachedChunk;
    protected final int clearedPlayerCenterChunk;

    public GameLootFinishEvent(IGameManager gameManager, int gameTime,
                               int lastProcessedCount, int clearedCachedChunk, int clearedPlayerCenterChunk) {
        super(gameManager, gameTime);
        this.lastProcessedCount = lastProcessedCount;
        this.clearedCachedChunk = clearedCachedChunk;
        this.clearedPlayerCenterChunk = clearedPlayerCenterChunk;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_LOOT_FINISH_EVENT;
    }

    public int getLastProcessedCount() {
        return this.lastProcessedCount;
    }

    public int getClearedCachedChunk() {
        return this.clearedCachedChunk;
    }

    public int getClearedPlayerCenterChunk() {
        return this.clearedPlayerCenterChunk;
    }

    @Override public String getTextName() {
        return "GameLootFinishEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }

    private static final EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(GameLootFinishEvent.class);
    @Override public @NotNull EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
