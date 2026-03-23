package xiao.battleroyale.compat.neoforge.init;

import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.command.EntitySelectorManager;
import net.neoforged.neoforge.common.command.IEntitySelectorType;
import xiao.battleroyale.api.init.ISelectorRegistry;

import java.util.function.Predicate;

public class NeoCommandSelectorRegistry implements ISelectorRegistry {

    @Override
    public void registerSelector(String token, Predicate<Entity> filter, int maxResults, boolean includeEntities, Component tooltip) {
        EntitySelectorManager.register(token, new IEntitySelectorType() {
            @Override
            public EntitySelector build(EntitySelectorParser parser) {
                parser.setMaxResults(Integer.MAX_VALUE);
                parser.setIncludesEntities(includeEntities);
                parser.addPredicate(filter);
                return parser.getSelector();
            }
            @Override
            public Component getSuggestionTooltip() {
                return tooltip;
            }
        });
    }
}