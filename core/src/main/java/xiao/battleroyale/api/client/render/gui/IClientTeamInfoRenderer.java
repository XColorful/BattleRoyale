package xiao.battleroyale.api.client.render.gui;

import xiao.battleroyale.api.client.render.IClientSubRenderer;

public interface IClientTeamInfoRenderer extends IClientGuiRender, IClientSubRenderer {

    void setDisplayTeam(boolean shouldDisplay);

    void setOfflineTimeLimit(int time);

    void setXRatio(double ratio);

    void setYRatio(double ratio);
}
