package xiao.battleroyale.api.event.game.zone;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.common.loot.ILootEntry;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.event.EventDispatcher;

import java.util.List;

public class EntityEvent extends AbstractSpecialZoneEvent {

    protected final List<Entity> lootEntities;
    protected final List<Entity> lastLootEntities;
    protected @NotNull final CompoundTag nbt;
    protected final LootGenerator.LootContext lootContext;
    protected final ILootEntry lootEntry;

    public EntityEvent(IGameManager gameManager, @NotNull ZoneManager.ZoneTickContext zoneTickContext,
                       String protocol, @NotNull JsonObject jsonTag,
                       List<Entity> lootEntities, List<Entity> lastLootEntities, @NotNull CompoundTag nbt,
                       LootGenerator.LootContext lootContext, ILootEntry lootEntry) {
        super(gameManager, zoneTickContext, protocol, jsonTag);
        this.lootEntities = lootEntities;
        this.lastLootEntities = lastLootEntities;
        this.nbt = nbt;
        this.lootContext = lootContext;
        this.lootEntry = lootEntry;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.ENTITY_EVENT;
    }

    public List<Entity> getLootEntities() {
        return this.lootEntities;
    }

    public List<Entity> getLastLootEntities() {
        return this.lastLootEntities;
    }

    public @NotNull CompoundTag getNbt() {
        return this.nbt;
    }

    public List<Entity> generateLoot() {
        return LootGenerator.generateLootEntities(lootContext, lootEntry);
    }

    @Override public String getTextName() {
        return "CBR EntityEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }

    private static final EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(EntityEvent.class);
    @Override public @NotNull EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
