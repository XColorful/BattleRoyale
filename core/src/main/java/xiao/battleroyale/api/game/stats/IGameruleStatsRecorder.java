package xiao.battleroyale.api.game.stats;

import java.util.Map;

public interface IGameruleStatsRecorder {

    void onRecordIntGamerule(Map<String, Integer> intGameruleWriter);

    void onRecordBoolGamerule(Map<String, Boolean> boolGameruleWriter);

    void onRecordDoubleGamerule(Map<String, Double> doubleGameruleWriter);

    void onRecordStringGamerule(Map<String, String> stringGameruleWriter);
}
