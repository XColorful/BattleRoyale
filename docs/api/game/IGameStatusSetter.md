```java
package xiao.battleroyale.api.game;

public interface IGameStatusSetter {
	boolean setGameStep(int step);
	boolean setGlobalCenterOffset(Vec3 offset);
	void setDefaultLevel(String defaultLevelKey);
	
	boolean setHasWinner(boolean hasWinner);
	boolean setRemainRestartTime(int remainRestartTime);
}
```