package xiao.battleroyale.api.minecraft;

public class InventoryIndex {

    public static final int HOTBAR_START = 0;
    public static final int HOTBAR_END = 8;
    public static final int INVENTORY_START = 9;
    public static final int INVENTORY_END = 35;
    public static final int ARMOR_START = 36;
    public static final int ARMOR_END = 39;
    public static final int OFFHAND_START = 40;
    public static final int OFFHAND_END = 40;
    public static final int CUSTOM_START = 41;

    public static SlotType getSlotType(int slotIndex) {
        if (INVENTORY_START <= slotIndex && slotIndex <= INVENTORY_END) {
            return SlotType.INVENTORY;
        } else if (HOTBAR_START <= slotIndex && slotIndex <= HOTBAR_END) {
            return SlotType.HOTBAR;
        } else if (ARMOR_START <= slotIndex && slotIndex <= ARMOR_END) {
            return SlotType.ARMOR;
        } else if (OFFHAND_START <= slotIndex && slotIndex <= OFFHAND_END) {
            return SlotType.OFFHAND;
        }
        return SlotType.CUSTOM;
    }

    public enum SlotType {
        HOTBAR,
        INVENTORY,
        ARMOR,
        OFFHAND,
        CUSTOM
    }
}
