package xiao.battleroyale.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.inventory.LootSpawnerMenu;

public class LootSpawnerScreen extends AbstractLootContainerScreen<LootSpawnerMenu> {
    public LootSpawnerScreen(LootSpawnerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        initScreen();
    }

    @Override
    protected void adjustTitleLabelOff() {
        super.adjustTitleLabelOff(8, 15);
    }

    @Override
    protected void adjustInventoryLabelOff() {
        super.adjustInventoryLabelOff(8, 65);
    }

    @Override
    protected void adjustTextureOff() {
        super.adjustTextureOff(0, 9);
    }

    @Override
    protected void adjustTextureSize() {
        super.adjustTextureSize(176, 149);
    }

    @Override
    protected void adjustTexture() {
        super.adjustTexture(BattleRoyale.getMcRegistry().createResourceLocation(String.format("%s:textures/gui/loot_spawner_gui.png", BattleRoyale.MOD_ID)));
    }
}