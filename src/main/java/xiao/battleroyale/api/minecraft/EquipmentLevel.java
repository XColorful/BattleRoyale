package xiao.battleroyale.api.minecraft;

import xiao.battleroyale.config.common.loot.type.ItemEntry;

public class EquipmentLevel {

    public static final int TURTLE = 0;
    public static final int LEATHER = 1;
    public static final int GOLDEN = 2;
    public static final int CHAINMAIL = 3;
    public static final int IRON = 4;
    public static final int DIAMOND = 5;
    public static final int NETHERITE = 6;

    public static final int HELMET = 0;
    public static final int CHESTPLATE = 1;
    public static final int LEGGINGS = 2;
    public static final int BOOTS = 3;

    public static String getName(int material, int part) {
        if (material == TURTLE && part != HELMET) {
            return "minecraft:shield";
        }

        String materialString;
        switch (material) {
            case TURTLE -> materialString = "turtle";
            case LEATHER -> materialString = "leather";
            case GOLDEN -> materialString = "golden";
            case CHAINMAIL -> materialString = "chainmail";
            case IRON -> materialString = "iron";
            case DIAMOND -> materialString = "diamond";
            case NETHERITE -> materialString = "netherite";
            default -> {
                return "minecraft:shield";
            }
        }
        String partString;
        switch (part) {
            case HELMET -> partString = "helmet";
            case CHESTPLATE -> partString = "chestplate";
            case LEGGINGS -> partString = "leggings";
            case BOOTS -> partString = "boots";
            default -> {
                return "minecraft:shield";
            }
        }
        return "minecraft:" + materialString + "_" + partString;
    }

    public static int getDamage(int material, int part) {
        int baseDurability;
        switch (material) {
            case LEATHER -> baseDurability = 5;
            case GOLDEN -> baseDurability = 7;
            case CHAINMAIL, IRON -> baseDurability = 16;
            case DIAMOND -> baseDurability = 33;
            case NETHERITE -> baseDurability = 37;
            case TURTLE -> baseDurability = 25;
            default -> {
                return 335 + 1; // 盾牌
            }
        }

        int durabilityMultiplier;
        switch (part) {
            case HELMET -> durabilityMultiplier = 11;
            case CHESTPLATE -> durabilityMultiplier = 16;
            case LEGGINGS -> durabilityMultiplier = 15;
            case BOOTS -> durabilityMultiplier = 13;
            default -> {
                return 335 + 1; // 盾牌
            }
        }

        int maxDurability = baseDurability * durabilityMultiplier;

        if (material == TURTLE && part != HELMET) {
            return 335 + 1; // 盾牌
        }

        return maxDurability + 1;
    }

    public static ItemEntry equipment(int material, int part, int shootTaken) {
        String equipName = getName(material, part);
        int damage = Math.max(getDamage(material, part) - 2 * shootTaken, 0);
        return new ItemEntry(equipName, "{Damage:" + damage + "}", 1);
    }
}