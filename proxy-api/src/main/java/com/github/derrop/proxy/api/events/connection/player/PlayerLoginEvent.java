package com.github.derrop.proxy.api.events.connection.player;

import com.github.derrop.proxy.api.connection.ProxiedPlayer;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerLoginEvent extends PlayerEvent implements Cancelable { // TODO

    private boolean cancel;

    private ServiceConnection targetConnection;

    public PlayerLoginEvent(@NotNull ProxiedPlayer player, @Nullable ServiceConnection targetConnection) {
        super(player);
        this.targetConnection = targetConnection;
    }

    public void setTargetConnection(@Nullable ServiceConnection targetConnection) {
        if (targetConnection != null && targetConnection.getPlayer() != null) {
            throw new IllegalArgumentException("Cannot connect player to an already used connection");
        }
        this.targetConnection = targetConnection;
    }

    @Nullable
    public ServiceConnection getTargetConnection() {
        return this.targetConnection;
    }

    @Override
    public void cancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }
}