package xiao.battleroyale.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.inventory.LootSpawnerMenu;

public class LootSpawnerScreen extends AbstractLootContainerScreen<LootSpawnerMenu> {
    public static int IMAGE_WIDTH = 176;
    public static int IMAGE_HEIGHT = 149;
    public LootSpawnerScreen(LootSpawnerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, IMAGE_WIDTH, IMAGE_HEIGHT);
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
        super.adjustTextureSize(IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    @Override
    protected void adjustTexture() {
        super.adjustTexture(BattleRoyale.getMcRegistry().createResourceLocation(String.format("%s:textures/gui/loot_spawner_gui.png", BattleRoyale.MOD_ID)),
                256, 256);
    }
}