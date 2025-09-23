package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.MessageFunc;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.StringUtils;

public class MessageFuncEntry extends AbstractFuncEntry {

    private final boolean setTitleAnimation;
    private final int fadeInTicks;
    private final int stayTicks;
    private final int fadeOutTicks;
    private final boolean sendTitle;
    private @NotNull final Component title;
    private final boolean sendSubtitle;
    private @NotNull final Component subTitle;
    private final boolean sendActionBar;
    private @NotNull final Component actionBar;

    public MessageFuncEntry(int moveDelay, int moveTime, int tickFreq, int tickOffset,
                            boolean setTitleAnimation, int fadeInTicks, int stayTicks, int fadeOutTicks,
                            boolean sendTitle, Component title, boolean sendSubtitle, Component subTitle,
                            boolean sendActionBar, Component actionBar) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.setTitleAnimation = setTitleAnimation;
        this.fadeInTicks = fadeInTicks;
        this.stayTicks = stayTicks;
        this.fadeOutTicks = fadeOutTicks;
        this.sendTitle = sendTitle;
        this.title = title != null ? title : Component.empty();
        this.sendSubtitle = sendSubtitle;
        this.subTitle = subTitle != null ? subTitle : Component.empty();
        this.sendActionBar = sendActionBar;
        this.actionBar = actionBar != null ? actionBar : Component.empty();
    }

    @Override
    public String getType() {
        return ZoneFuncTag.MESSAGE;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new MessageFunc(
                moveDelay, moveTime, tickFreq, tickOffset,
                setTitleAnimation, fadeInTicks, stayTicks, fadeOutTicks,
                sendTitle, title, sendSubtitle, subTitle, sendActionBar, actionBar
        );
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneFuncTag.TYPE_NAME, getType());
        jsonObject.addProperty(ZoneFuncTag.MOVE_DELAY, moveDelay);
        jsonObject.addProperty(ZoneFuncTag.MOVE_TIME, moveTime);
        jsonObject.addProperty(ZoneFuncTag.TICK_FREQUENCY, tickFreq);
        jsonObject.addProperty(ZoneFuncTag.TICK_OFFSET, tickOffset);

        jsonObject.addProperty(ZoneFuncTag.SET_TITLE_ANIMATION, setTitleAnimation);
        if (setTitleAnimation) {
            jsonObject.addProperty(ZoneFuncTag.FADE_IN_TICKS, fadeInTicks);
            jsonObject.addProperty(ZoneFuncTag.STAY_TICKS, stayTicks);
            jsonObject.addProperty(ZoneFuncTag.FADE_OUT_TICKS, fadeOutTicks);
        }
        jsonObject.addProperty(ZoneFuncTag.SEND_TITLE, sendTitle);
        if (sendTitle) {
            jsonObject.addProperty(ZoneFuncTag.TITLE, StringUtils.componentToString(title));
        }
        jsonObject.addProperty(ZoneFuncTag.SEND_SUBTITLE, sendSubtitle);
        if (sendSubtitle) {
            jsonObject.addProperty(ZoneFuncTag.SUBTITLE, StringUtils.componentToString(subTitle));
        }
        jsonObject.addProperty(ZoneFuncTag.SEND_ACTION_BAR, sendActionBar);
        if (sendActionBar) {
            jsonObject.addProperty(ZoneFuncTag.ACTION_BAR, StringUtils.componentToString(actionBar));
        }
        return jsonObject;
    }

    public static MessageFuncEntry fromJson(JsonObject jsonObject) {
        int moveDelay = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_DELAY, 0);
        int moveTime = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_TIME, 0);
        int tickFreq = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_FREQUENCY, 20);
        int tickOffset = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_OFFSET, -1);

        boolean setTitleAnimation = JsonUtils.getJsonBool(jsonObject, ZoneFuncTag.SET_TITLE_ANIMATION, false);
        int fadeInTicks = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.FADE_IN_TICKS, 10);
        int stayTicks = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.STAY_TICKS, 80);
        int fadeOutTicks = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.FADE_OUT_TICKS, 20);

        boolean sendTitle = JsonUtils.getJsonBool(jsonObject, ZoneFuncTag.SEND_TITLE, false);
        Component title = JsonUtils.getJsonComponent(jsonObject, ZoneFuncTag.TITLE, Component.empty());

        boolean sendSubtitle = JsonUtils.getJsonBool(jsonObject, ZoneFuncTag.SEND_SUBTITLE, false);
        Component subTitle = JsonUtils.getJsonComponent(jsonObject, ZoneFuncTag.SUBTITLE, Component.empty());

        boolean sendActionBar = JsonUtils.getJsonBool(jsonObject, ZoneFuncTag.SEND_ACTION_BAR, false);
        Component actionBar = JsonUtils.getJsonComponent(jsonObject, ZoneFuncTag.ACTION_BAR, Component.empty());

        return new MessageFuncEntry(moveDelay, moveTime, tickFreq, tickOffset,
                setTitleAnimation, fadeInTicks, stayTicks, fadeOutTicks,
                sendTitle, title, sendSubtitle, subTitle, sendActionBar, actionBar
        );
    }
}
