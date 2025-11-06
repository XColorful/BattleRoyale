package xiao.battleroyale.api.event.loot.generate;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.loot.LootGenerator;

public abstract class AbstractSpecialGenerateEvent <T extends BlockEntity> extends AbstractGenerateEvent<T> {

    protected final String protocol;
    protected final @NotNull JsonObject jsonTag;

    public AbstractSpecialGenerateEvent(LootGenerator.LootContext lootContext, T target, String protocol, @NotNull JsonObject jsonTag) {
        super(lootContext, target);
        this.protocol = protocol;
        this.jsonTag = jsonTag;
    }

    public String getProtocol() {
        return protocol;
    }

    public @NotNull JsonObject getJsonTag() {
        return jsonTag;
    }

    @Deprecated
    public @NotNull JsonObject getTag() {
        return getJsonTag();
    }
}
