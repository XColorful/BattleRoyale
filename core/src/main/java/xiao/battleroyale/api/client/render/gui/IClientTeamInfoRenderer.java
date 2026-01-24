package xiao.battleroyale.api.client.render.gui;

public interface IClientTeamInfoRenderer extends IClientGuiRender {

    void setDisplayTeam(boolean shouldDisplay);

    void setOfflineTimeLimit(int time);

    void setXRatio(double ratio);

    void setYRatio(double ratio);
}
