package com.github.derrop.proxy.network.channel;

import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.network.NetworkUtils;
import com.github.derrop.proxy.network.compression.PacketCompressor;
import com.github.derrop.proxy.network.compression.PacketDeCompressor;
import com.github.derrop.proxy.network.minecraft.MinecraftDecoder;
import com.github.derrop.proxy.network.wrapper.DecodedPacket;
import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class DefaultNetworkChannel implements NetworkChannel {

    private SocketAddress address;

    private final Channel channel;

    private boolean closing = false;

    private boolean closed = false;

    public DefaultNetworkChannel(@NotNull ChannelHandlerContext channelHandlerContext) {
        this(channelHandlerContext.channel());
    }

    public DefaultNetworkChannel(@NotNull Channel channel) {
        this.channel = channel;
        this.address = (this.channel.remoteAddress() == null) ? this.channel.parent().localAddress() : this.channel.remoteAddress();
    }

    @Override
    public void write(@NotNull Object packet) {
        if (this.isClosed() || !this.channel.isActive()) {
            return;
        }

        if (packet instanceof DecodedPacket) {
            this.channel.writeAndFlush(((DecodedPacket) packet).getByteBuf(), this.channel.voidPromise());
        } else {
            this.channel.writeAndFlush(packet, this.channel.voidPromise());
        }
    }

    @Override
    public void setProtocolState(@NotNull ProtocolState state) {
        this.channel.pipeline().get(MinecraftDecoder.class).setProtocolState(state);
    }

    @Override
    public @NotNull ProtocolState getProtocolState() {
        return this.channel.pipeline().get(MinecraftDecoder.class).getProtocolState();
    }

    @Override
    public @NotNull SocketAddress getAddress() {
        return this.address;
    }

    @Override
    public void close(@Nullable Object goodbyeMessage) {
        if (!this.closed) {
            this.closed = this.closing = true;

            if (goodbyeMessage != null && this.channel.isActive()) {
                this.channel.writeAndFlush(goodbyeMessage).addListener(ChannelFutureListener.CLOSE);
            } else {
                this.channel.flush().close();
            }
        }
    }

    @Override
    public void delayedClose(@Nullable Packet goodbyeMessage) {
        if (!this.closing) {
            this.closing = true;
            this.channel.eventLoop().schedule(() -> this.close(goodbyeMessage), 250, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void setCompression(int compression) {
        if (this.channel.pipeline().get(PacketCompressor.class) == null && compression != -1) {
            this.addBefore(NetworkUtils.PACKET_ENCODER, NetworkUtils.COMPRESSOR, new PacketCompressor(compression));
        }

        if (compression != -1) {
            this.channel.pipeline().get(PacketCompressor.class).setThreshold(compression);
        } else {
            this.channel.pipeline().remove(NetworkUtils.COMPRESSOR);
        }

        if (this.channel.pipeline().get(PacketDeCompressor.class) == null && compression != -1) {
            this.addBefore(NetworkUtils.PACKET_DECODER, NetworkUtils.DE_COMPRESSOR, new PacketDeCompressor(compression));
        }

        if (compression == -1) {
            this.channel.pipeline().remove(NetworkUtils.DE_COMPRESSOR);
        }
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public boolean isClosing() {
        return this.closing;
    }

    @Override
    public @NotNull Channel getWrappedChannel() {
        return this.channel;
    }

    public void addBefore(String baseName, String name, ChannelHandler handler) {
        Preconditions.checkState(this.channel.eventLoop().inEventLoop(), "cannot add handler outside of event loop");
        this.channel.pipeline().flush();
        this.channel.pipeline().addBefore(baseName, name, handler);
    }

    public void markClosed() {
        this.closed = this.closing = true;
    }

    public void setAddress(SocketAddress address) {
        this.address = address;
    }
}
