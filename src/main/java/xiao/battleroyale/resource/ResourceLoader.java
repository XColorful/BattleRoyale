package xiao.battleroyale.resource;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.function.Consumer;

public enum ResourceLoader implements RepositorySource {
    INSTANCE;
    public PackType packType;

    @Override
    public void loadPacks(Consumer<Pack> packOnLoad) {
        ;
    }
}
