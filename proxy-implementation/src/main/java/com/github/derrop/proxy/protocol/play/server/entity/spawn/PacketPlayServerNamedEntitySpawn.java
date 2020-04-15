package com.github.derrop.proxy.protocol.play.server.entity.spawn;

import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.util.DataWatcher;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketPlayServerNamedEntitySpawn extends DefinedPacket implements PositionedPacket {

    private int entityId;
    private UUID playerId;
    private int x;
    private int y;
    private int z;
    private byte yaw;
    private byte pitch;
    private short currentItem;
    private List<DataWatcher.WatchableObject> watchableObjects;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.entityId = readVarInt(buf);
        this.playerId = readUUID(buf);
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.yaw = buf.readByte();
        this.pitch = buf.readByte();
        this.currentItem = buf.readShort();
        try {
            this.watchableObjects = DataWatcher.readWatchedListFromByteBuf(buf);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeVarInt(this.entityId, buf);
        writeUUID(this.playerId, buf);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        buf.writeShort(this.currentItem);
        try {
            DataWatcher.writeWatchedListToByteBuf(this.watchableObjects, buf);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }

    @Override
    public int getId() {
        return ProtocolIds.ClientBound.Play.NAMED_ENTITY_SPAWN;
    }
}