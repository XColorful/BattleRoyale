package xiao.battleroyale.config.common.server.function.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.common.server.function.IFunctionEntry;
import xiao.battleroyale.api.config.common.server.function.RegisterEntryTag;
import xiao.battleroyale.api.config.sub.IConfigAppliable;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class RegisterEntry implements IFunctionEntry, IConfigAppliable {
    public boolean clearPreviousBeforeApply;
    public final List<RegisterDetail> registerDetails;

    public static class RegisterDetail {
        public @Nullable String functionRl;
        public @Nullable String tagRl;
        public @NotNull String rl;
        public boolean isTag;
        public String event;
        public @Nullable CustomEventType customEventType;
        public @Nullable EventType eventType;
        public EventPriority priority;
        public boolean receiveCanceled;
        public @Nullable String eventClass;

        public RegisterDetail(String functionRl, String tagRl, String event) {
            this(functionRl, tagRl, event, EventPriority.NORMAL, false, null);
        }
        public RegisterDetail(@Nullable String functionRl, @Nullable String tagRl, String event, EventPriority priority, boolean receiveCanceled, @Nullable String eventClass) {
            if (functionRl == null && tagRl == null) throw new IllegalArgumentException("RegisterDetail: one of functionRl and tagRl should be not null");
            this.functionRl = functionRl;
            this.tagRl = tagRl;
            if (this.functionRl != null) {
                this.rl = functionRl; this.isTag = false;
            } else {
                this.rl = tagRl; this.isTag = true;
            }
            this.event = event;
            this.customEventType = CustomEventType.fromString(event);
            this.eventType = EventType.fromString(event);
            this.priority = priority != null ? priority : EventPriority.NORMAL;
            this.receiveCanceled = receiveCanceled;
            this.eventClass = eventClass;
        }

        public RegisterDetail copy() {
            return new RegisterDetail(functionRl, tagRl, event, priority, receiveCanceled, eventClass);
        }
    }

    public RegisterEntry(boolean clearPreviousBeforeApply, List<RegisterDetail> registerDetails) {
        this.clearPreviousBeforeApply = clearPreviousBeforeApply;
        this.registerDetails = registerDetails;
    }
    @Override public @NotNull RegisterEntry copy() {
        List<RegisterDetail> registerDetailsCopy = new ArrayList<>(registerDetails.size());
        for (RegisterDetail registerDetail : registerDetails) {
            registerDetailsCopy.add(registerDetail.copy());
        }
        return new RegisterEntry(clearPreviousBeforeApply, registerDetailsCopy);
    }

    @Override
    public String getType() {
        return "registerEntry";
    }

    public static RegisterEntry fromJson(JsonObject jsonObject) {
        List<RegisterDetail> registerDetails = new ArrayList<>();
        boolean clearPreviousBeforeApply = JsonUtils.getJsonBool(jsonObject, RegisterEntryTag.CLEAR_PREVIOUS_BEFORE_APPLY, true);
        JsonArray detailsArray = JsonUtils.getJsonArray(jsonObject, RegisterEntryTag.DETAILS, null);
        if (detailsArray != null) {
            for (JsonElement element : detailsArray) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject detailObject = element.getAsJsonObject();
                String functionRl = JsonUtils.getJsonString(detailObject, RegisterEntryTag.FUNCTION_RL, null);
                String tagRl = JsonUtils.getJsonString(detailObject, RegisterEntryTag.TAG_RL, null);
                if (functionRl == null && tagRl == null) continue;
                String event = JsonUtils.getJsonString(detailObject, RegisterEntryTag.EVENT, null);
                if (event == null) continue;
                EventPriority priority = EventPriority.fromString(JsonUtils.getJsonString(detailObject, RegisterEntryTag.PRIORITY, null));
                boolean receiveCanceled = JsonUtils.getJsonBool(detailObject, RegisterEntryTag.RECEIVE_CANCELED, false);
                String eventClass = JsonUtils.getJsonString(detailObject, RegisterEntryTag.EVENT_CLASS, null);
                registerDetails.add(new RegisterDetail(functionRl, tagRl, event, priority, receiveCanceled, eventClass));
            }
        }

        return new RegisterEntry(clearPreviousBeforeApply, registerDetails);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        JsonArray detailArray = new JsonArray();
        for (RegisterDetail detail : registerDetails) {
            JsonObject detailObject = new JsonObject();
            detailObject.addProperty(detail.isTag ? RegisterEntryTag.TAG_RL : RegisterEntryTag.FUNCTION_RL, detail.rl);
            detailObject.addProperty(RegisterEntryTag.EVENT, detail.event);
            detailObject.addProperty(RegisterEntryTag.PRIORITY, detail.priority.getName());
            detailObject.addProperty(RegisterEntryTag.RECEIVE_CANCELED, detail.receiveCanceled);
            if (detail.eventClass != null) {
                detailObject.addProperty(RegisterEntryTag.EVENT_CLASS, detail.eventClass);
            }
        }
        jsonObject.add(RegisterEntryTag.DETAILS, detailArray);
        return jsonObject;
    }

    @Override
    public void applyDefault() {
        BattleRoyale.getServerManager().getFunctionManager().applyConfig(this);
    }
}
