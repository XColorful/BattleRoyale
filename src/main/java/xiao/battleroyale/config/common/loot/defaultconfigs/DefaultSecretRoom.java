package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.config.common.loot.type.EmptyEntry;
import xiao.battleroyale.config.common.loot.type.ItemEntry;
import xiao.battleroyale.config.common.loot.type.MultiEntry;
import xiao.battleroyale.config.common.loot.type.NoneEntry;

import java.util.Arrays;

public class DefaultSecretRoom extends DefaultConfigHelper {

    private static final String SECRET_ROOM_CONFIG_PATH = "config/battleroyale/loot/secret_room/default.json";

    public static void generateDefaultConfigs() {
        JsonArray secretRoomConfigsJson = new JsonArray();
        secretRoomConfigsJson.add(generateDefaultSecretRoom());
        writeJsonToFile(SECRET_ROOM_CONFIG_PATH, secretRoomConfigsJson);
    }

    private static JsonObject generateDefaultSecretRoom() {
        JsonObject config = new JsonObject();
        config.addProperty("id", 401);
        config.addProperty("name", "Treasure trove");
        config.addProperty("color", "#00FFFF");
        ILootEntry<?> multiEntry = new MultiEntry<>(Arrays.asList(
                new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:iron_ingot")), 1)),
                new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:gold_ingot")), 1)),
                new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:copper_ingot")), 1)),
                new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:lapis_lazuli")), 1)),
                new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:redstone")), 1)),
                new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:diamond")), 1)),
                new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:emerald")), 1)),
                new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:amethyst_shard")), 1)),
                new ItemEntry(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:netherite_ingot")), 1))
        ));
        config.add("entry", multiEntry.toJson());
        return config;
    }
}