package com.github.phantompowered.proxy.connection.handler;

import com.github.phantompowered.proxy.ImplementationUtil;
import com.github.phantompowered.proxy.api.network.channel.NetworkChannel;
import com.github.phantompowered.proxy.connection.ConnectedProxyClient;
import com.github.phantompowered.proxy.network.channel.ChannelListener;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ServerChannelListener implements ChannelListener {

    private final ConnectedProxyClient client;

    public ServerChannelListener(ConnectedProxyClient client) {
        this.client = client;
    }

    @Override
    public void handleException(@NotNull NetworkChannel channel, @NotNull Throwable cause) {
        System.err.println("Exception on proxy client " + this.client.getAccountName() + "!");
        if (!(cause instanceof IOException)) {
            cause.printStackTrace();
        }

        this.client.handleDisconnect(Component.text("§c" + ImplementationUtil.stringifyException(cause)));
        this.client.getConnection().close();
    }

    @Override
    public void handleChannelInactive(@NotNull NetworkChannel channel) {
        this.client.handleDisconnect(Component.text("§cNo reason given"));
        this.client.getConnection().close();
    }

}
