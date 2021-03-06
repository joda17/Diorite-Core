package org.diorite.impl.connection.packets.play.in;

import java.io.IOException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.impl.connection.EnumProtocol;
import org.diorite.impl.connection.EnumProtocolDirection;
import org.diorite.impl.connection.packets.PacketClass;
import org.diorite.impl.connection.packets.PacketDataSerializer;
import org.diorite.impl.connection.packets.play.PacketPlayInListener;

@PacketClass(id = 0x01, protocol = EnumProtocol.PLAY, direction = EnumProtocolDirection.SERVERBOUND)
public class PacketPlayInChat extends PacketPlayIn
{
    private String content;

    public PacketPlayInChat()
    {
    }

    public PacketPlayInChat(final String content)
    {
        this.content = content;
    }

    @Override
    public void readPacket(final PacketDataSerializer data) throws IOException
    {
        this.content = data.readText(100);
    }

    @Override
    public void writeFields(final PacketDataSerializer data) throws IOException
    {
        data.writeText(this.content);
    }

    @Override
    public void handle(final PacketPlayInListener listener)
    {
        listener.handle(this);
    }

    public String getContent()
    {
        return this.content;
    }

    public void setContent(final String content)
    {
        this.content = content;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("content", this.content).toString();
    }
}
