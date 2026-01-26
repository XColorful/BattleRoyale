```java
package xiao.battleroyale.api.config.sub;

public interface IConfigLoadable<T> {
	String getConfigPath(int folderId);
	String getConfigSubPath(int folderId);
}
```