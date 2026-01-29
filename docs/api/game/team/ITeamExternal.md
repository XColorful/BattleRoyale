```java
package xiao.battleroyale.api.game.team;

public interface ITeamExternal {
	void joinTeam(ServerPlayer player);
	void joinTeamSpecific(ServerPlayer player, int teamId);
	void kickPlayer(ServerPlayer sender, ServerPlayer targetPlayer);
	void invitePlayer(ServerPlayer sender, ServerPlayer targetPlayer);
	void acceptInvite(ServerPlayer player, ServerPlayer senderPlayer);
	void declineInvite(ServerPlayer player, ServerPlayer senderPlayer);
	void requestPlayer(ServerPlayer sender, ServerPlayer targetPlayer);
	void acceptRequest(ServerPlayer teamLeader, ServerPlayer senderPlayer);
	void declineRequest(ServerPlayer teamLeader, ServerPlayer senderPlayer);
	boolean leaveTeam(ServerPlayer player);
	boolean addToTeam(@Nullable CommandSourceStack source, LivingEntity player, int teamId);
	int buildTeamForAll(@Nullable CommandSourceStack source, List<LivingEntity> players, int targetSize, boolean forceRebuild);
}
```