package xiao.battleroyale.api.client.render.gui;

import xiao.battleroyale.api.client.event.IRenderGuiEventPost;
import xiao.battleroyale.api.client.render.IClientRendererName;

public interface IClientGuiRender extends IClientRendererName {

    void onRenderGuiEvent(IRenderGuiEventPost event);
}
