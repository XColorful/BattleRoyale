package xiao.battleroyale.data.data;

public abstract class AbstractNameData {

    protected final String dataName;

    public AbstractNameData(String dataName) {
        this.dataName = dataName;
    }

    public abstract void clear();
}
