```java
package xiao.battleroyale.api.game.stats;

public interface IGameEventCount {
	int getHurtCount();
	int getDamageCount();
	int getKnockCount();
	int getDownCount();
	int getReviveCount();
	int getKillCount();
	int getDeathCount();
	
	double getHurtAmountTotal();
	double getDamageAmountTotal();
}
```