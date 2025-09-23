package xiao.battleroyale.api.utilitity;

import net.minecraft.world.phys.Vec3;

public interface ILobbyReadApi {

    boolean isLobbyCreated();
    Vec3 lobbyPos();
    Vec3 lobbyDimension();
    boolean lobbyMuteki();
    boolean lobbyHeal();
    boolean lobbyChangeGamemode();
    boolean teleportDropInventory();
    boolean teleportClearInventory();

    boolean isInLobbyRange(Vec3 pos);
}