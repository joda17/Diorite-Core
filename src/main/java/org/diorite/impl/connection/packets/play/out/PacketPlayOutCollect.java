package org.diorite.impl.connection.packets.play.out;

import java.io.IOException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.impl.connection.EnumProtocol;
import org.diorite.impl.connection.EnumProtocolDirection;
import org.diorite.impl.connection.packets.PacketClass;
import org.diorite.impl.connection.packets.PacketDataSerializer;
import org.diorite.impl.connection.packets.play.PacketPlayOutListener;

@PacketClass(id = 0x0D, protocol = EnumProtocol.PLAY, direction = EnumProtocolDirection.CLIENTBOUND)
public class PacketPlayOutCollect extends PacketPlayOut
{
    private int collectedEntityId;
    private int collecterEntityId;

    public PacketPlayOutCollect()
    {
    }

    public PacketPlayOutCollect(final int collectedEntityId, final int collecterEntityId)
    {
        this.collectedEntityId = collectedEntityId;
        this.collecterEntityId = collecterEntityId;
    }

    public int getCollectedEntityId()
    {
        return this.collectedEntityId;
    }

    public void setCollectedEntityId(final int collectedEntityId)
    {
        this.collectedEntityId = collectedEntityId;
    }

    public int getCollecterEntityId()
    {
        return this.collecterEntityId;
    }

    public void setCollecterEntityId(final int collecterEntityId)
    {
        this.collecterEntityId = collecterEntityId;
    }

    @Override
    public void readPacket(final PacketDataSerializer data) throws IOException
    {
        this.collectedEntityId = data.readVarInt();
        this.collecterEntityId = data.readVarInt();
    }

    @Override
    public void writeFields(final PacketDataSerializer data) throws IOException
    {
        data.writeVarInt(this.collectedEntityId);
        data.writeVarInt(this.collecterEntityId);
    }

    @Override
    public void handle(final PacketPlayOutListener listener)
    {
        listener.handle(this);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("collectedEntityId", this.collectedEntityId).append("collecterEntityId", this.collecterEntityId).toString();
    }
}
