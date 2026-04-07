package xiao.battleroyale.api.server.utilitity;

import net.minecraft.world.phys.Vec3;

public interface ISurvivalLobbyManager extends ILobbyReadApi, ILobbyFuncApi {

    boolean setLobby(String levelKeyString, boolean allowGamePlayerTeleport,
                     Vec3 lobbyPos, Vec3 lobbyDimension, boolean lobbyMuteki, boolean lobbyHeal, boolean changeGamemode,
                     boolean teleportDropInventory, boolean dropGameItemOnly, boolean teleportClearInventory, boolean clearGameItemOnly);
}
