package xiao.battleroyale.compat.forge.event.game.zone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.zone.AirdropEventData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.common.loot.LootGenerator.LootContext;

import java.util.List;

public class AirdropEvent extends AbstractSpecialZoneEvent {

    protected final List<ItemStack> lootItems;
    protected final List<ItemStack> lastLootItems;
    protected @NotNull final CompoundTag nbt;
    protected final LootContext lootContext;
    protected final ILootEntry lootEntry;

    public AirdropEvent(IGameManager gameManager, @NotNull ZoneTickContext zoneTickContext,
                        String protocol, @NotNull CompoundTag tag,
                        List<ItemStack> lootItems, List<ItemStack> lastLootItems, @NotNull CompoundTag nbt,
                        LootContext lootContext, ILootEntry lootEntry) {
        super(gameManager, zoneTickContext, protocol, tag);
        this.lootItems = lootItems;
        this.lastLootItems = lastLootItems;
        this.nbt = nbt;
        this.lootContext = lootContext;
        this.lootEntry = lootEntry;
    }

    public List<ItemStack> getLootItems() {
        return this.lootItems;
    }

    public List<ItemStack> getLastLootItems() {
        return this.lastLootItems;
    }

    @Override
    public @NotNull CompoundTag getNbt() {
        return this.nbt;
    }

    public List<ItemStack> generateLoot() {
        return LootGenerator.generateLootItem(lootContext, lootEntry);
    }

    public static AirdropEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof AirdropEventData data)) {
            throw new RuntimeException("Expected AirdropEventData but received: " + customEventData.getClass().getName());
        }
        return new AirdropEvent(data.gameManager, data.zoneTickContext,
                data.protocol, data.tag,
                data.lootItems, data.lastLootItems, data.nbt,
                data.lootContext, data.lootEntry);
    }
}
