```java
package xiao.battleroyale.api.config.sub;

public interface IConfigSingleEntry extends IConfigEntry, IConfigAppliable {
	int getConfigId();
	String getName();
	boolean isDefaultSelect();
	@Override @NotNull IConfigSingleEntry copy();
}
```