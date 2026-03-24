package xiao.battleroyale.api.init;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.function.Predicate;

public interface ISelectorRegistry {

    default void registerSelector(String token, Predicate<Entity> filter, boolean includeEntities, Component tooltip) {
        registerSelector(token, filter, Integer.MAX_VALUE, includeEntities, tooltip);
    }
    void registerSelector(String token, Predicate<Entity> filter, int maxResults, boolean includeEntities, Component tooltip);
}
