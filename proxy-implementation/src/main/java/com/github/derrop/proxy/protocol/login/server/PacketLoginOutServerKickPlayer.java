package com.github.derrop.proxy.protocol.login.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketLoginOutServerKickPlayer implements Packet {

    private String message;

    public PacketLoginOutServerKickPlayer(String message) {
        this.message = message;
    }

    public PacketLoginOutServerKickPlayer() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Login.DISCONNECT;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.message = protoBuf.readString();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.message);
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return "PacketLoginOutServerKickPlayer(message=" + this.getMessage() + ")";
    }
}
