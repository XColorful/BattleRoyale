package xiao.battleroyale.api.client.render.game.gui;

import xiao.battleroyale.api.client.event.IRenderGuiEventPost;
import xiao.battleroyale.api.client.render.game.IClientRendererName;

public interface IClientGuiRender extends IClientRendererName {

    void onRenderGuiEvent(IRenderGuiEventPost event);
}
