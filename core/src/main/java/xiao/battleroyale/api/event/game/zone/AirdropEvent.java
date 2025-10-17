package xiao.battleroyale.api.event.game.zone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.common.loot.LootGenerator;

import java.util.List;

public class AirdropEvent extends AbstractSpecialZoneEvent {

    protected final List<ItemStack> lootItems;
    protected final List<ItemStack> lastLootItems;
    protected @NotNull
    final CompoundTag nbt;
    protected final LootGenerator.LootContext lootContext;
    protected final ILootEntry lootEntry;

    public AirdropEvent(IGameManager gameManager, @NotNull ZoneManager.ZoneTickContext zoneTickContext,
                        String protocol, @NotNull CompoundTag tag,
                        List<ItemStack> lootItems, List<ItemStack> lastLootItems, @NotNull CompoundTag nbt,
                        LootGenerator.LootContext lootContext, ILootEntry lootEntry) {
        super(gameManager, zoneTickContext, protocol, tag);
        this.lootItems = lootItems;
        this.lastLootItems = lastLootItems;
        this.nbt = nbt;
        this.lootContext = lootContext;
        this.lootEntry = lootEntry;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.AIRDROP_EVENT;
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
}

