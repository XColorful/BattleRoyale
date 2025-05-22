package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class DefaultConfigHelper {

    protected static void writeJsonToFile(String filePath, JsonArray jsonArray) {
        Path path = Paths.get(filePath);
        if (Files.notExists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                System.err.println("Failed to create default config directory: " + e.getMessage());
                return;
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Files.write(path, gson.toJson(jsonArray).getBytes(StandardCharsets.UTF_8));
            System.out.println("Generated default config at: " + path);
        } catch (IOException e) {
            System.err.println("Failed to write default config: " + e.getMessage());
        }
    }
}