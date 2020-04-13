package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.api.connection.PacketSender;
import net.md_5.bungee.protocol.DefinedPacket;

public class SimplePacketCache implements PacketCacheHandler {

    private int packetId;

    private DefinedPacket lastPacket;
    private boolean sendOnSwitch;

    public SimplePacketCache(int packetId) {
        this(packetId, true);
    }

    public SimplePacketCache(int packetId, boolean sendOnSwitch) {
        this.packetId = packetId;
        this.sendOnSwitch = sendOnSwitch;
    }

    public DefinedPacket getLastPacket() {
        return lastPacket;
    }

    @Override
    public int[] getPacketIDs() {
        return new int[]{this.packetId};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        this.lastPacket = newPacket.getDeserializedPacket();
    }

    @Override
    public boolean sendOnSwitch() {
        return this.sendOnSwitch;
    }

    @Override
    public void sendCached(PacketSender con) {
        if (this.lastPacket != null) {
            con.sendPacket(this.lastPacket);
        }
    }
}