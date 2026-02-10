```java
package xiao.battleroyale.api.game.stats;

public interface IGameEventStatsRecorder {
	void onRecordStart(GameStartFinishEvent event);
	void onRecordGameTick(GameTickFinishEvent event);
	void onRecordPlayerDamage(GamePlayerDamageFinishEvent event);
	void onRecordPlayerDown(GamePlayerDownFinishEvent event);
	void onRecordPlayerRevive(GamePlayerReviveFinishEvent event);
	void onRecordPlayerDeath(GamePlayerDeathFinishEvent event);
	void onRecordStop(GameStopFinishEvent event);
	void onRecordComplete(GameCompleteFinishEvent event);
}
```