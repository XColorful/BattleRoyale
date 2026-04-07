package xiao.battleroyale.api.event.game.team;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractSenderEvent extends AbstractGameEvent {

    protected final ServerPlayer sender;
    protected final ServerPlayer targetPlayer;

    public AbstractSenderEvent(IGameManager gameManager, ServerPlayer sender, ServerPlayer targetPlayer) {
        super(gameManager);
        this.sender = sender;
        this.targetPlayer = targetPlayer;
    }

    public ServerPlayer getSender() {
        return this.sender;
    }

    public ServerPlayer getTargetPlayer() {
        return this.targetPlayer;
    }

    @Override
    public @NotNull CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        return super.createCommandSourceStack(source)
                .withRotation(sender.getRotationVector())
                .withEntity(sender);
    }

    @Override public String getTextName() {
        return sender.getName().getString();
    }
    @Override public Component getDisplayName() {
        return sender.getDisplayName();
    }
}
