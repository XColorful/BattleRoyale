package xiao.battleroyale.config.common.game.spawn.type.detail;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.spawn.type.detail.SpawnDetailTag;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class TeleportDetailEntry extends AbstractDetailEntry<TeleportDetailEntry> {

    public final List<Vec3> fixedPos;
    public boolean teamTogether;
    public boolean findGround;
    public double randomRange;
    public int hangTime;

    public TeleportDetailEntry(List<Vec3> fixedPos, boolean teamTogether, boolean findGround, double randomRange, int hangTime) {
        this.fixedPos = fixedPos;
        this.teamTogether = teamTogether;
        this.findGround = findGround;
        this.randomRange = randomRange;
        this.hangTime = hangTime;
    }

    @Override
    public void toJson(JsonObject jsonObject, CommonDetailType detailType) {
        switch (detailType) {
            case FIXED -> jsonObject.add(SpawnDetailTag.GROUND_FIXED_POS, JsonUtils.writeVec3ListToJson(fixedPos));
            case RANDOM -> {}
        }
        jsonObject.addProperty(SpawnDetailTag.GROUND_TEAM_TOGETHER, teamTogether);
        jsonObject.addProperty(SpawnDetailTag.GROUND_FIND_GROUND, findGround);
        jsonObject.addProperty(SpawnDetailTag.GROUND_RANDOM_RANGE, randomRange);
        jsonObject.addProperty(SpawnDetailTag.GROUND_HANG_TIME, hangTime);
    }

    public static @NotNull TeleportDetailEntry fromJson(JsonObject jsonObject, CommonDetailType detailType) {
        List<Vec3> fixedPos = new ArrayList<>();
        switch (detailType) {
            case FIXED -> fixedPos = JsonUtils.getJsonVecList(jsonObject, SpawnDetailTag.GROUND_FIXED_POS);
            case RANDOM -> {}
        }
        boolean teamTogether = JsonUtils.getJsonBool(jsonObject, SpawnDetailTag.GROUND_TEAM_TOGETHER, false);
        boolean findGround = JsonUtils.getJsonBool(jsonObject, SpawnDetailTag.GROUND_FIND_GROUND, false);
        double range = JsonUtils.getJsonDouble(jsonObject, SpawnDetailTag.GROUND_FIND_GROUND, 0);
        int hangTime = JsonUtils.getJsonInt(jsonObject, SpawnDetailTag.GROUND_HANG_TIME, 20 * 15);

        return new TeleportDetailEntry(fixedPos, teamTogether, findGround, range, hangTime);
    }
}
