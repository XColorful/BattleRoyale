package xiao.battleroyale.api.event.game.zone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.common.loot.LootGenerator;

import java.util.List;

public class EntityEvent extends AbstractSpecialZoneEvent {

    protected final List<Entity> lootEntities;
    protected final List<Entity> lastLootEntities;
    protected @NotNull final CompoundTag nbt;
    protected final LootGenerator.LootContext lootContext;
    protected final ILootEntry lootEntry;

    public EntityEvent(IGameManager gameManager, @NotNull ZoneManager.ZoneTickContext zoneTickContext,
                       String protocol, @NotNull CompoundTag tag,
                       List<Entity> lootEntities, List<Entity> lastLootEntities, @NotNull CompoundTag nbt,
                       LootGenerator.LootContext lootContext, ILootEntry lootEntry) {
        super(gameManager, zoneTickContext, protocol, tag);
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

    @Override
    public @NotNull CompoundTag getNbt() {
        return this.nbt;
    }

    public List<Entity> generateLoot() {
        return LootGenerator.generateLootEntities(lootContext, lootEntry);
    }
}
