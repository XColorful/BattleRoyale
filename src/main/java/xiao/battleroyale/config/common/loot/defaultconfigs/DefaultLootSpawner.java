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
import java.util.List;

public class DefaultLootSpawner extends DefaultConfigHelper {

    private static final String LOOT_SPAWNER_CONFIG_PATH = "config/battleroyale/loot/loot_spawner/default.json";

    public static void generateDefaultConfigs() {
        JsonArray lootSpawnerConfigsJson = new JsonArray();
        lootSpawnerConfigsJson.add(generateDefaultLootSpawner1());
        lootSpawnerConfigsJson.add(generateDefaultLootSpawner2());
        writeJsonToFile(LOOT_SPAWNER_CONFIG_PATH, lootSpawnerConfigsJson);
    }

    private static JsonObject generateDefaultLootSpawner1() {
        JsonObject config = new JsonObject();
        config.addProperty("id", 1);
        config.addProperty("name", "Starter Gear");
        config.addProperty("color", "#A0522D");
        ILootEntry<?> multiEntry = new MultiEntry<>(Arrays.asList(
                new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:leather_helmet")), 1)),
                new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:leather_chestplate")), 1)),
                new WeightEntry<>(Arrays.asList(
                        WeightEntry.createWeightedEntry(20, new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:wooden_sword")), 1))),
                        WeightEntry.createWeightedEntry(20, new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:wooden_axe")), 1)))
                )),
                new RandomEntry<>(0.5, new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:melon_slice")), 1)))
        ));
        config.add("entry", multiEntry.toJson());
        return config;
    }

    private static JsonObject generateDefaultLootSpawner2() {
        JsonObject config = new JsonObject();
        config.addProperty("id", 2);
        config.addProperty("name", "Basic Combat Gear");
        config.addProperty("color", "#778899");
        ILootEntry<?> multiEntry = new MultiEntry<>(Arrays.asList(
                new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:iron_helmet")), 1)),
                new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:iron_chestplate")), 1)),
                new WeightEntry<>(Arrays.asList(
                        WeightEntry.createWeightedEntry(20, new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:iron_sword")), 1))),
                        WeightEntry.createWeightedEntry(20, new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:iron_axe")), 1)))
                )),
                new RandomEntry<>(0.2, new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:cooked_beef")), 1)))
        ));
        config.add("entry", multiEntry.toJson());
        return config;
    }
}