package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.config.common.loot.type.ItemEntry;
import xiao.battleroyale.config.common.loot.type.MultiEntry;
import xiao.battleroyale.config.common.loot.type.RandomEntry;
import xiao.battleroyale.config.common.loot.type.WeightEntry;

import java.util.Arrays;

public class DefaultAirdrop extends DefaultConfigHelper {

    private static final String AIRDROP_CONFIG_PATH = "config/battleroyale/loot/airdrop/default.json";

    public static void generateDefaultConfigs() {
        JsonArray airdropConfigsJson = new JsonArray();
        airdropConfigsJson.add(generateDefaultAirdrop());
        writeJsonToFile(AIRDROP_CONFIG_PATH, airdropConfigsJson);
    }

    private static JsonObject generateDefaultAirdrop() {
        JsonObject config = new JsonObject();
        config.addProperty("id", 201);
        config.addProperty("name", "Netherite Airdrop");
        config.addProperty("color", "#FFFF00");
        ILootEntry<?> multiEntry = new MultiEntry<>(Arrays.asList(
                new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:netherite_helmet")), 1)),
                new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:netherite_chestplate")), 1)),
                new WeightEntry<>(Arrays.asList(
                        WeightEntry.createWeightedEntry(20, new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:netherite_sword")), 1))),
                        WeightEntry.createWeightedEntry(20, new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:netherite_axe")), 1)))
                )),
                new RandomEntry<>(0.05, new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:enchanted_golden_apple")), 1)))
        ));
        config.add("entry", multiEntry.toJson());
        return config;
    }
}