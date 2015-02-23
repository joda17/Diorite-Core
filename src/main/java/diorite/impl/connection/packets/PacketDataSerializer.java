package diorite.impl.connection.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.UUID;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import diorite.chat.BaseComponent;
import diorite.chat.serialize.ComponentSerializer;

public class PacketDataSerializer extends ByteBuf
// TODO: add methods to serialize items and other stuff.
{
    private final ByteBuf byteBuf;

    public PacketDataSerializer(final ByteBuf bytebuf)
    {
        this.byteBuf = bytebuf;
    }

    public static int neededBytes(final int i)
    {
        for (int j = 1; j < 5; j++)
        {
            if ((i & (- 1 << (j * 7))) == 0)
            {
                return j;
            }
        }
        return 5;
    }

    public BaseComponent readBaseComponent()
    {
        return ComponentSerializer.parseOne(this.readText(Short.MAX_VALUE));
    }

    public void writeBaseComponent(final BaseComponent baseComponent)
    {
        this.writeText(ComponentSerializer.toString(baseComponent));
    }

    public void writeByteWord(final byte[] abyte)
    {
        this.writeVarInt(abyte.length);
        this.writeBytes(abyte);
    }

    public byte[] readByteWord()
    {
        final byte[] abyte = new byte[this.readVarInt()];

        this.readBytes(abyte);
        return abyte;
    }

    public <T extends Enum<T>> Enum<T> readEnum(final Class<T> oclass)
    {
        return oclass.getEnumConstants()[this.readVarInt()];
    }

    public void writeEnum(final Enum<?> oenum)
    {
        this.writeVarInt(oenum.ordinal());
        this.readInt();
    }

    @SuppressWarnings("MagicNumber")
    public int readVarInt()
    {
        int i = 0;
        int j = 0;
        byte b0;
        do
        {
            b0 = this.readByte();
            i |= (b0 & 0x7F) << (j++ * 7);
            if (j > 5)
            {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b0 & 0x80) == 128);
        return i;
    }

    @SuppressWarnings("MagicNumber")
    public long readVarLong()
    {
        long i = 0L;
        int j = 0;
        byte b0;
        do
        {
            b0 = this.readByte();
            i |= (b0 & 0x7F) << (j++ * 7);
            if (j > 10)
            {
                throw new RuntimeException("VarLong too big");
            }
        } while ((b0 & 0x80) == 128);
        return i;
    }

    public void writeUUID(final UUID uuid)
    {
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
    }

    public UUID readUUID()
    {
        return new UUID(this.readLong(), this.readLong());
    }

    @SuppressWarnings("MagicNumber")
    public void writeVarInt(int i)
    {
        while ((i & 0xFFFFFF80) != 0)
        {
            this.writeByte((i & 0x7F) | 0x80);
            i >>>= 7;
        }
        this.writeByte(i);
    }

    @SuppressWarnings("MagicNumber")
    public void writeVarLong(long i)
    {
        while ((i & 0xFFFFFF80) != 0L)
        {
            this.writeByte((int) (i & 0x7F) | 0x80);
            i >>>= 7;
        }
        this.writeByte((int) i);
    }

    public String readText(final int i)
    {
        final int j = this.readVarInt();
        if (j > (i << 2))
        {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + (i << 2) + ")");
        }
        if (j < 0)
        {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        }
        final String s = new String(this.readBytes(j).array(), Charsets.UTF_8);
        if (s.length() > i)
        {
            throw new DecoderException("The received string length is longer than maximum allowed (" + j + " > " + i + ")");
        }
        return s;
    }

    public PacketDataSerializer writeText(final String s)
    {
        final byte[] abyte = s.getBytes(Charsets.UTF_8);
        if (abyte.length > Short.MAX_VALUE)
        {
            throw new EncoderException("String too big (was " + s.length() + " bytes encoded, max " + Short.MAX_VALUE + ")");
        }
        this.writeVarInt(abyte.length);
        this.writeBytes(abyte);
        return this;
    }

    @Override
    public int capacity()
    {
        return this.byteBuf.capacity();
    }

    @Override
    public ByteBuf capacity(final int i)
    {
        return this.byteBuf.capacity(i);
    }

    @Override
    public int maxCapacity()
    {
        return this.byteBuf.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc()
    {
        return this.byteBuf.alloc();
    }

    @Override
    public ByteOrder order()
    {
        return this.byteBuf.order();
    }

    @Override
    public ByteBuf order(final ByteOrder byteorder)
    {
        return this.byteBuf.order(byteorder);
    }

    @Override
    public ByteBuf unwrap()
    {
        return this.byteBuf.unwrap();
    }

    @Override
    public boolean isDirect()
    {
        return this.byteBuf.isDirect();
    }

    @Override
    public int readerIndex()
    {
        return this.byteBuf.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(final int i)
    {
        return this.byteBuf.readerIndex(i);
    }

    @Override
    public int writerIndex()
    {
        return this.byteBuf.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(final int i)
    {
        return this.byteBuf.writerIndex(i);
    }

    @Override
    public ByteBuf setIndex(final int i, final int j)
    {
        return this.byteBuf.setIndex(i, j);
    }

    @Override
    public int readableBytes()
    {
        return this.byteBuf.readableBytes();
    }

    @Override
    public int writableBytes()
    {
        return this.byteBuf.writableBytes();
    }

    @Override
    public int maxWritableBytes()
    {
        return this.byteBuf.maxWritableBytes();
    }

    @Override
    public boolean isReadable()
    {
        return this.byteBuf.isReadable();
    }

    @Override
    public boolean isReadable(final int i)
    {
        return this.byteBuf.isReadable(i);
    }

    @Override
    public boolean isWritable()
    {
        return this.byteBuf.isWritable();
    }

    @Override
    public boolean isWritable(final int i)
    {
        return this.byteBuf.isWritable(i);
    }

    @Override
    public ByteBuf clear()
    {
        return this.byteBuf.clear();
    }

    @Override
    public ByteBuf markReaderIndex()
    {
        return this.byteBuf.markReaderIndex();
    }

    @Override
    public ByteBuf resetReaderIndex()
    {
        return this.byteBuf.resetReaderIndex();
    }

    @Override
    public ByteBuf markWriterIndex()
    {
        return this.byteBuf.markWriterIndex();
    }

    @Override
    public ByteBuf resetWriterIndex()
    {
        return this.byteBuf.resetWriterIndex();
    }

    @Override
    public ByteBuf discardReadBytes()
    {
        return this.byteBuf.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes()
    {
        return this.byteBuf.discardSomeReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(final int i)
    {
        return this.byteBuf.ensureWritable(i);
    }

    @Override
    public int ensureWritable(final int i, final boolean flag)
    {
        return this.byteBuf.ensureWritable(i, flag);
    }

    @Override
    public boolean getBoolean(final int i)
    {
        return this.byteBuf.getBoolean(i);
    }

    @Override
    public byte getByte(final int i)
    {
        return this.byteBuf.getByte(i);
    }

    @Override
    public short getUnsignedByte(final int i)
    {
        return this.byteBuf.getUnsignedByte(i);
    }

    @Override
    public short getShort(final int i)
    {
        return this.byteBuf.getShort(i);
    }

    @Override
    public int getUnsignedShort(final int i)
    {
        return this.byteBuf.getUnsignedShort(i);
    }

    @Override
    public int getMedium(final int i)
    {
        return this.byteBuf.getMedium(i);
    }

    @Override
    public int getUnsignedMedium(final int i)
    {
        return this.byteBuf.getUnsignedMedium(i);
    }

    @Override
    public int getInt(final int i)
    {
        return this.byteBuf.getInt(i);
    }

    @Override
    public long getUnsignedInt(final int i)
    {
        return this.byteBuf.getUnsignedInt(i);
    }

    @Override
    public long getLong(final int i)
    {
        return this.byteBuf.getLong(i);
    }

    @Override
    public char getChar(final int i)
    {
        return this.byteBuf.getChar(i);
    }

    @Override
    public float getFloat(final int i)
    {
        return this.byteBuf.getFloat(i);
    }

    @Override
    public double getDouble(final int i)
    {
        return this.byteBuf.getDouble(i);
    }

    @Override
    public ByteBuf getBytes(final int i, final ByteBuf bytebuf)
    {
        return this.byteBuf.getBytes(i, bytebuf);
    }

    @Override
    public ByteBuf getBytes(final int i, final ByteBuf bytebuf, final int j)
    {
        return this.byteBuf.getBytes(i, bytebuf, j);
    }

    @Override
    public ByteBuf getBytes(final int i, final ByteBuf bytebuf, final int j, final int k)
    {
        return this.byteBuf.getBytes(i, bytebuf, j, k);
    }

    @Override
    public ByteBuf getBytes(final int i, final byte[] abyte)
    {
        return this.byteBuf.getBytes(i, abyte);
    }

    @Override
    public ByteBuf getBytes(final int i, final byte[] abyte, final int j, final int k)
    {
        return this.byteBuf.getBytes(i, abyte, j, k);
    }

    @Override
    public ByteBuf getBytes(final int i, final ByteBuffer bytebuffer)
    {
        return this.byteBuf.getBytes(i, bytebuffer);
    }

    @Override
    public ByteBuf getBytes(final int i, final OutputStream outputstream, final int j) throws IOException
    {
        return this.byteBuf.getBytes(i, outputstream, j);
    }

    @Override
    public int getBytes(final int i, final GatheringByteChannel gatheringbytechannel, final int j) throws IOException
    {
        return this.byteBuf.getBytes(i, gatheringbytechannel, j);
    }

    @Override
    public ByteBuf setBoolean(final int i, final boolean flag)
    {
        return this.byteBuf.setBoolean(i, flag);
    }

    @Override
    public ByteBuf setByte(final int i, final int j)
    {
        return this.byteBuf.setByte(i, j);
    }

    @Override
    public ByteBuf setShort(final int i, final int j)
    {
        return this.byteBuf.setShort(i, j);
    }

    @Override
    public ByteBuf setMedium(final int i, final int j)
    {
        return this.byteBuf.setMedium(i, j);
    }

    @Override
    public ByteBuf setInt(final int i, final int j)
    {
        return this.byteBuf.setInt(i, j);
    }

    @Override
    public ByteBuf setLong(final int i, final long j)
    {
        return this.byteBuf.setLong(i, j);
    }

    @Override
    public ByteBuf setChar(final int i, final int j)
    {
        return this.byteBuf.setChar(i, j);
    }

    @Override
    public ByteBuf setFloat(final int i, final float f)
    {
        return this.byteBuf.setFloat(i, f);
    }

    @Override
    public ByteBuf setDouble(final int i, final double d0)
    {
        return this.byteBuf.setDouble(i, d0);
    }

    @Override
    public ByteBuf setBytes(final int i, final ByteBuf bytebuf)
    {
        return this.byteBuf.setBytes(i, bytebuf);
    }

    @Override
    public ByteBuf setBytes(final int i, final ByteBuf bytebuf, final int j)
    {
        return this.byteBuf.setBytes(i, bytebuf, j);
    }

    @Override
    public ByteBuf setBytes(final int i, final ByteBuf bytebuf, final int j, final int k)
    {
        return this.byteBuf.setBytes(i, bytebuf, j, k);
    }

    @Override
    public ByteBuf setBytes(final int i, final byte[] abyte)
    {
        return this.byteBuf.setBytes(i, abyte);
    }

    @Override
    public ByteBuf setBytes(final int i, final byte[] abyte, final int j, final int k)
    {
        return this.byteBuf.setBytes(i, abyte, j, k);
    }

    @Override
    public ByteBuf setBytes(final int i, final ByteBuffer bytebuffer)
    {
        return this.byteBuf.setBytes(i, bytebuffer);
    }

    @Override
    public int setBytes(final int i, final InputStream inputstream, final int j)
            throws IOException
    {
        return this.byteBuf.setBytes(i, inputstream, j);
    }

    @Override
    public int setBytes(final int i, final ScatteringByteChannel scatteringbytechannel, final int j)
            throws IOException
    {
        return this.byteBuf.setBytes(i, scatteringbytechannel, j);
    }

    @Override
    public ByteBuf setZero(final int i, final int j)
    {
        return this.byteBuf.setZero(i, j);
    }

    @Override
    public boolean readBoolean()
    {
        return this.byteBuf.readBoolean();
    }

    @Override
    public byte readByte()
    {
        return this.byteBuf.readByte();
    }

    @Override
    public short readUnsignedByte()
    {
        return this.byteBuf.readUnsignedByte();
    }

    @Override
    public short readShort()
    {
        return this.byteBuf.readShort();
    }

    @Override
    public int readUnsignedShort()
    {
        return this.byteBuf.readUnsignedShort();
    }

    @Override
    public int readMedium()
    {
        return this.byteBuf.readMedium();
    }

    @Override
    public int readUnsignedMedium()
    {
        return this.byteBuf.readUnsignedMedium();
    }

    @Override
    public int readInt()
    {
        return this.byteBuf.readInt();
    }

    @Override
    public long readUnsignedInt()
    {
        return this.byteBuf.readUnsignedInt();
    }

    @Override
    public long readLong()
    {
        return this.byteBuf.readLong();
    }

    @Override
    public char readChar()
    {
        return this.byteBuf.readChar();
    }

    @Override
    public float readFloat()
    {
        return this.byteBuf.readFloat();
    }

    @Override
    public double readDouble()
    {
        return this.byteBuf.readDouble();
    }

    @Override
    public ByteBuf readBytes(final int i)
    {
        return this.byteBuf.readBytes(i);
    }

    @Override
    public ByteBuf readSlice(final int i)
    {
        return this.byteBuf.readSlice(i);
    }

    @Override
    public ByteBuf readBytes(final ByteBuf bytebuf)
    {
        return this.byteBuf.readBytes(bytebuf);
    }

    @Override
    public ByteBuf readBytes(final ByteBuf bytebuf, final int i)
    {
        return this.byteBuf.readBytes(bytebuf, i);
    }

    @Override
    public ByteBuf readBytes(final ByteBuf bytebuf, final int i, final int j)
    {
        return this.byteBuf.readBytes(bytebuf, i, j);
    }

    @Override
    public ByteBuf readBytes(final byte[] abyte)
    {
        return this.byteBuf.readBytes(abyte);
    }

    @Override
    public ByteBuf readBytes(final byte[] abyte, final int i, final int j)
    {
        return this.byteBuf.readBytes(abyte, i, j);
    }

    @Override
    public ByteBuf readBytes(final ByteBuffer bytebuffer)
    {
        return this.byteBuf.readBytes(bytebuffer);
    }

    @Override
    public ByteBuf readBytes(final OutputStream outputstream, final int i)
            throws IOException
    {
        return this.byteBuf.readBytes(outputstream, i);
    }

    @Override
    public int readBytes(final GatheringByteChannel gatheringbytechannel, final int i)
            throws IOException
    {
        return this.byteBuf.readBytes(gatheringbytechannel, i);
    }

    @Override
    public ByteBuf skipBytes(final int i)
    {
        return this.byteBuf.skipBytes(i);
    }

    @Override
    public ByteBuf writeBoolean(final boolean flag)
    {
        return this.byteBuf.writeBoolean(flag);
    }

    @Override
    public ByteBuf writeByte(final int i)
    {
        return this.byteBuf.writeByte(i);
    }

    @Override
    public ByteBuf writeShort(final int i)
    {
        return this.byteBuf.writeShort(i);
    }

    @Override
    public ByteBuf writeMedium(final int i)
    {
        return this.byteBuf.writeMedium(i);
    }

    @Override
    public ByteBuf writeInt(final int i)
    {
        return this.byteBuf.writeInt(i);
    }

    @Override
    public ByteBuf writeLong(final long i)
    {
        return this.byteBuf.writeLong(i);
    }

    @Override
    public ByteBuf writeChar(final int i)
    {
        return this.byteBuf.writeChar(i);
    }

    @Override
    public ByteBuf writeFloat(final float f)
    {
        return this.byteBuf.writeFloat(f);
    }

    @Override
    public ByteBuf writeDouble(final double d0)
    {
        return this.byteBuf.writeDouble(d0);
    }

    @Override
    public ByteBuf writeBytes(final ByteBuf bytebuf)
    {
        return this.byteBuf.writeBytes(bytebuf);
    }

    @Override
    public ByteBuf writeBytes(final ByteBuf bytebuf, final int i)
    {
        return this.byteBuf.writeBytes(bytebuf, i);
    }

    @Override
    public ByteBuf writeBytes(final ByteBuf bytebuf, final int i, final int j)
    {
        return this.byteBuf.writeBytes(bytebuf, i, j);
    }

    @Override
    public ByteBuf writeBytes(final byte[] abyte)
    {
        return this.byteBuf.writeBytes(abyte);
    }

    @Override
    public ByteBuf writeBytes(final byte[] abyte, final int i, final int j)
    {
        return this.byteBuf.writeBytes(abyte, i, j);
    }

    @Override
    public ByteBuf writeBytes(final ByteBuffer bytebuffer)
    {
        return this.byteBuf.writeBytes(bytebuffer);
    }

    @Override
    public int writeBytes(final InputStream inputstream, final int i)
            throws IOException
    {
        return this.byteBuf.writeBytes(inputstream, i);
    }

    @Override
    public int writeBytes(final ScatteringByteChannel scatteringbytechannel, final int i)
            throws IOException
    {
        return this.byteBuf.writeBytes(scatteringbytechannel, i);
    }

    @Override
    public ByteBuf writeZero(final int i)
    {
        return this.byteBuf.writeZero(i);
    }

    @Override
    public int indexOf(final int i, final int j, final byte b0)
    {
        return this.byteBuf.indexOf(i, j, b0);
    }

    @Override
    public int bytesBefore(final byte b0)
    {
        return this.byteBuf.bytesBefore(b0);
    }

    @Override
    public int bytesBefore(final int i, final byte b0)
    {
        return this.byteBuf.bytesBefore(i, b0);
    }

    @Override
    public int bytesBefore(final int i, final int j, final byte b0)
    {
        return this.byteBuf.bytesBefore(i, j, b0);
    }

    @Override
    public int forEachByte(final ByteBufProcessor bytebufprocessor)
    {
        return this.byteBuf.forEachByte(bytebufprocessor);
    }

    @Override
    public int forEachByte(final int i, final int j, final ByteBufProcessor bytebufprocessor)
    {
        return this.byteBuf.forEachByte(i, j, bytebufprocessor);
    }

    @Override
    public int forEachByteDesc(final ByteBufProcessor bytebufprocessor)
    {
        return this.byteBuf.forEachByteDesc(bytebufprocessor);
    }

    @Override
    public int forEachByteDesc(final int i, final int j, final ByteBufProcessor bytebufprocessor)
    {
        return this.byteBuf.forEachByteDesc(i, j, bytebufprocessor);
    }

    @Override
    public ByteBuf copy()
    {
        return this.byteBuf.copy();
    }

    @Override
    public ByteBuf copy(final int i, final int j)
    {
        return this.byteBuf.copy(i, j);
    }

    @Override
    public ByteBuf slice()
    {
        return this.byteBuf.slice();
    }

    @Override
    public ByteBuf slice(final int i, final int j)
    {
        return this.byteBuf.slice(i, j);
    }

    @Override
    public ByteBuf duplicate()
    {
        return this.byteBuf.duplicate();
    }

    @Override
    public int nioBufferCount()
    {
        return this.byteBuf.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer()
    {
        return this.byteBuf.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(final int i, final int j)
    {
        return this.byteBuf.nioBuffer(i, j);
    }

    @Override
    public ByteBuffer internalNioBuffer(final int i, final int j)
    {
        return this.byteBuf.internalNioBuffer(i, j);
    }

    @Override
    public ByteBuffer[] nioBuffers()
    {
        return this.byteBuf.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(final int i, final int j)
    {
        return this.byteBuf.nioBuffers(i, j);
    }

    @Override
    public boolean hasArray()
    {
        return this.byteBuf.hasArray();
    }

    @Override
    public byte[] array()
    {
        return this.byteBuf.array();
    }

    @Override
    public int arrayOffset()
    {
        return this.byteBuf.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress()
    {
        return this.byteBuf.hasMemoryAddress();
    }

    @Override
    public long memoryAddress()
    {
        return this.byteBuf.memoryAddress();
    }

    @Override
    public String toString(final Charset charset)
    {
        return this.byteBuf.toString(charset);
    }

    @Override
    public String toString(final int i, final int j, final Charset charset)
    {
        return this.byteBuf.toString(i, j, charset);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (! (o instanceof PacketDataSerializer))
        {
            return false;
        }

        final PacketDataSerializer that = (PacketDataSerializer) o;

        return this.byteBuf.equals(that.byteBuf);

    }

    @Override
    public int hashCode()
    {
        return this.byteBuf.hashCode();
    }

    @Override
    public int compareTo(final ByteBuf bytebuf)
    {
        return this.byteBuf.compareTo(bytebuf);
    }

    public String toString()
    {
        return this.byteBuf.toString();
    }

    @Override
    public ByteBuf retain(final int i)
    {
        return this.byteBuf.retain(i);
    }

    @Override
    public ByteBuf retain()
    {
        return this.byteBuf.retain();
    }

    @Override
    public int refCnt()
    {
        return this.byteBuf.refCnt();
    }

    @Override
    public boolean release()
    {
        return this.byteBuf.release();
    }

    @Override
    public boolean release(final int i)
    {
        return this.byteBuf.release(i);
    }
}
