package xiao.battleroyale.config.common;

import xiao.battleroyale.api.IConfigSingleEntry;

public abstract class AbstractSingleConfig implements IConfigSingleEntry {

    public final int id;
    public final String name;
    public final String color;
    public final boolean isDefault;

    public AbstractSingleConfig(int id, String name, String color, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.isDefault = isDefault;
    }

    @Override
    public int getConfigId() {
        return this.id;
    }

    @Override
    public boolean isDefaultSelect() {
        return this.isDefault;
    }
}
