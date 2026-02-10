package xiao.battleroyale.api.config.common.game.stats;

import xiao.battleroyale.api.config.sub.IConfigSingleEntry;
import xiao.battleroyale.config.common.game.stats.scoreboard.ScoreboardEntry;

public interface IStatsSingleEntry extends IConfigSingleEntry {

    ScoreboardEntry getScoreboardEntry();
}
