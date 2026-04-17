package xiao.battleroyale.config.common.game.gamerule.custom;

import com.google.gson.JsonObject;
import net.minecraft.world.BossEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.config.common.game.gamerule.IGameruleEntry;
import xiao.battleroyale.api.config.common.game.gamerule.custom.DeathMatchConfigTag;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class DeathmatchEntry implements IGameruleEntry {

    public int targetKill;
    public @NotNull List<Integer> killFuncs;
    public int respawnTrackDelay;
    public @NotNull List<Integer> retickZones;
    public boolean sendProgressBar;
    public BossEvent.BossBarColor progressBarColor;
    public BossEvent.BossBarOverlay progressBarOverlay;
    public boolean allowAllWin; // 是否允许全部队伍胜利，赢麻了

    public DeathmatchEntry() {
        this(50,
                null, 20 * 5, null,
                false, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS,
                false);
    }
    public DeathmatchEntry(int targetKill,
                           @Nullable List<Integer> killFuncs, int respawnTrackDelay, @Nullable List<Integer> retickZones,
                           boolean sendProgressBar, BossEvent.BossBarColor progressBarColor, BossEvent.BossBarOverlay progressBarOverlay,
                           boolean allowAllWin) {
        this.targetKill = Math.max(1, targetKill);
        this.killFuncs = killFuncs != null ? killFuncs : new ArrayList<>();
        this.respawnTrackDelay = Math.max(20, respawnTrackDelay);
        this.retickZones = retickZones != null ? retickZones : new ArrayList<>();
        this.sendProgressBar = sendProgressBar;
        this.progressBarColor = progressBarColor;
        this.progressBarOverlay = progressBarOverlay;
        this.allowAllWin = allowAllWin;
    }
    @Override public @NotNull DeathmatchEntry copy() {
        return new DeathmatchEntry(targetKill,
                new ArrayList<>(killFuncs), respawnTrackDelay, new ArrayList<>(retickZones),
                sendProgressBar, progressBarColor, progressBarOverlay,
                allowAllWin);
    }

    @Override
    public String getType() {
        return "configEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DeathMatchConfigTag.TARGET_KILL, targetKill);
        jsonObject.add(DeathMatchConfigTag.KILL_FUNCS, JsonUtils.writeIntListToJson(killFuncs));
        jsonObject.addProperty(DeathMatchConfigTag.RESPAWN_TRACK_DELAY, respawnTrackDelay);
        jsonObject.add(DeathMatchConfigTag.RETICK_ZONES, JsonUtils.writeIntListToJson(retickZones));
        jsonObject.addProperty(DeathMatchConfigTag.SEND_PROGRESS_BAR, sendProgressBar);
        jsonObject.addProperty(DeathMatchConfigTag.PROGRESS_BAR_COLOR, JsonUtils.writeBossBarColorToJson(progressBarColor));
        jsonObject.addProperty(DeathMatchConfigTag.PROGRESS_BAR_OVERLAY, JsonUtils.writeBossBarOverlayToJson(progressBarOverlay));
        jsonObject.addProperty(DeathMatchConfigTag.ALLOW_ALL_WIN, allowAllWin);
        return jsonObject;
    }

    @NotNull
    public static DeathmatchEntry fromJson(JsonObject jsonObject) {
        if (jsonObject == null) return new DeathmatchEntry();

        int targetKill = JsonUtils.getJsonInt(jsonObject, DeathMatchConfigTag.TARGET_KILL, 50);
        List<Integer> killFuncs = JsonUtils.getJsonIntList(jsonObject, DeathMatchConfigTag.KILL_FUNCS);
        int respawnTrackDelay = JsonUtils.getJsonInt(jsonObject, DeathMatchConfigTag.RESPAWN_TRACK_DELAY, 100);
        List<Integer> retickZones = JsonUtils.getJsonIntList(jsonObject, DeathMatchConfigTag.RETICK_ZONES);

        boolean sendProgressBar = JsonUtils.getJsonBool(jsonObject, DeathMatchConfigTag.SEND_PROGRESS_BAR, false);
        BossEvent.BossBarColor color = JsonUtils.getBossBarColor(jsonObject, DeathMatchConfigTag.PROGRESS_BAR_COLOR);
        BossEvent.BossBarOverlay overlay = JsonUtils.getBossBarOverlay(jsonObject, DeathMatchConfigTag.PROGRESS_BAR_OVERLAY);

        boolean allowAllWin = JsonUtils.getJsonBool(jsonObject, DeathMatchConfigTag.ALLOW_ALL_WIN, false);

        return new DeathmatchEntry(targetKill, killFuncs, respawnTrackDelay, retickZones,
                sendProgressBar, color, overlay, allowAllWin);
    }
}