package com.github.phantompowered.proxy.api.events.connection;

import com.github.phantompowered.proxy.api.event.Event;
import com.github.phantompowered.proxy.api.network.channel.NetworkChannel;
import com.github.phantompowered.proxy.api.ping.ServerPing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PingEvent extends Event {

    private final NetworkChannel channel;
    private ServerPing response;

    public PingEvent(@NotNull NetworkChannel channel, @Nullable ServerPing response) {
        this.channel = channel;
        this.response = response;
    }

    @NotNull
    public NetworkChannel getChannel() {
        return this.channel;
    }

    @Nullable
    public ServerPing getResponse() {
        return this.response;
    }

    public void setResponse(@Nullable ServerPing response) {
        this.response = response;
    }
}
