package fabian.hartman.MinecraftAFKBot.network.protocol;

import com.google.common.base.Charsets;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;
import fabian.hartman.MinecraftAFKBot.network.utils.InvalidPacketException;
import fabian.hartman.MinecraftAFKBot.network.utils.OverflowPacketException;
import fabian.hartman.MinecraftAFKBot.utils.ChatComponentUtils;
import fabian.hartman.MinecraftAFKBot.utils.nbt.NBTTag;
import fabian.hartman.MinecraftAFKBot.utils.nbt.StringTag;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.UUID;

public abstract class Packet {
    private static final JsonParser PARSER = new JsonParser();

    public abstract void write(ByteArrayDataOutput out, int protocolId) throws IOException;

    public abstract void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException;

    public static void writeString(String s, ByteArrayDataOutput buf) {
        if (s.length() > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format("Cannot send string longer than Short.MAX_VALUE (got %s characters)", s.length()));
        }

        byte[] b = s.getBytes(Charsets.UTF_8);
        writeVarInt(b.length, buf);
        buf.write(b);
    }

    public static String readString(ByteArrayDataInputWrapper buf) {
        int len = readVarInt(buf);
        if (len > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format("Cannot receive string longer than Short.MAX_VALUE (got %s characters)", len));
        }

        byte[] b = new byte[len];
        buf.readBytes(b);

        return new String(b, Charsets.UTF_8);
    }

    public static int readVarInt(ByteArrayDataInputWrapper input) {
        return readVarInt(input, 5);
    }

    private static int readVarInt(ByteArrayDataInputWrapper input, int maxBytes) {
        int out = 0;
        int bytes = 0;
        byte in;
        while (true) {
            in = input.readByte();

            out |= (in & 0x7F) << (bytes++ * 7);

            if (bytes > maxBytes) {
                throw new InvalidPacketException("VarInt too big");
            }

            if ((in & 0x80) != 0x80) {
                break;
            }
        }

        return out;
    }

    public static void writeVarInt(int value, ByteArrayDataOutput output) {
        int part;
        while (true) {
            part = value & 0x7F;

            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }

            output.writeByte(part);

            if (value == 0) {
                break;
            }
        }
    }

    public static void writeUUID(UUID uuid, ByteArrayDataOutput output) {
        output.writeLong(uuid.getMostSignificantBits());
        output.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteArrayDataInputWrapper input) {
        return new UUID(input.readLong(), input.readLong());
    }

    public byte[] readBytesFromStream(ByteArrayDataInputWrapper par0DataInputStream) {
        int var1 = readVarInt(par0DataInputStream);
        if (var1 < 0) {
            throw new OverflowPacketException("Key was smaller than nothing! Weird key!");
        } else {
            byte[] var2 = new byte[var1];
            par0DataInputStream.readFully(var2);
            return var2;
        }
    }

    public static int readVarInt(DataInputStream in) throws IOException { //reads a varint from the stream
        int i = 0;
        int j = 0;
        while (true){
            int k = in.read();

            i |= (k & 0x7F) << j++ * 7;

            if (j > 5) throw new InvalidPacketException("VarInt too big");

            if ((k & 0x80) != 128) break;
        }

        return i;
    }

    public static int[] readVarIntt(DataInputStream in) throws IOException { //reads a varint from the stream, returning both the length and the value
        int i = 0;
        int j = 0;
        int b = 0;
        while (true){
            int k = in.read();
            b += 1;
            i |= (k & 0x7F) << j++ * 7;

            if (j > 5) throw new InvalidPacketException("VarInt too big");

            if ((k & 0x80) != 128) break;
        }

        int[] result = {i,b};
        return result;
    }

    public static long readVarLong(ByteArrayDataInput input) {
        long i = 0L;
        int j = 0;

        byte b0;

        do {
            b0 = input.readByte();
            i |= (long) (b0 & 127) << j++ * 7;
            if (j > 10) {
                throw new RuntimeException("VarLong too big");
            }
        } while ((b0 & 128) == 128);

        return i;
    }

    public static String readString(DataInputStream in) {
        int length;
        String s = "";
        try {
            length = readVarInt(in);
            if (length < 0) {
                throw new IOException(
                        "Received string length is less than zero! Weird string!");
            }

            if(length == 0){
                return "";
            }
            byte[] b = new byte[length];
            in.readFully(b, 0, length);
            s = new String(b, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static NBTTag readNBT(ByteArrayDataInputWrapper input, int protocolId) {
        return new NBTTag(input, protocolId);
    }

    public static void writeNBT(NBTTag tag, ByteArrayDataOutput output) {
        output.write(tag.getData());
    }

    public static String readChatComponent(ByteArrayDataInputWrapper input, int protocolId) {
        JsonObject chatComponent = null;
        try {
            if (protocolId < ProtocolConstants.MC_1_20_3) {
                String text = readString(input);
                if (text.startsWith("\"") && text.endsWith("\"")) {
                    chatComponent = new JsonObject();
                    chatComponent.addProperty("text", text.substring(1, text.length() - 1));
                } else {
                    chatComponent = PARSER.parse(text).getAsJsonObject();
                }
            } else {
                NBTTag nbt = readNBT(input, protocolId);
                if (nbt.getTag() instanceof StringTag)
                    return ((StringTag) nbt.getTag()).getValue();
                chatComponent = nbt.getTag().toJson().getAsJsonObject();
            }
        } catch (Exception ignored) {
            // Ignored
        }
        return ChatComponentUtils.toPlainText(chatComponent);
    }

    public static <E extends Enum<E>> EnumSet<E> readEnumSet(Class<E> type, ByteArrayDataInput input) {
        E[] ae = type.getEnumConstants();
        BitSet bitset = readFixedBitSet(ae.length, input);
        EnumSet<E> enumset = EnumSet.noneOf(type);

        for (int i = 0; i < ae.length; ++i) {
            if (bitset.get(i)) {
                enumset.add(ae[i]);
            }
        }

        return enumset;
    }

    public static BitSet readFixedBitSet(int size, ByteArrayDataInput input) {
        byte[] abyte = new byte[-Math.floorDiv(-size, 8)];

        input.readFully(abyte);
        return BitSet.valueOf(abyte);
    }

    public static void writeFixedBitSet(BitSet bitSet, int size, ByteArrayDataOutput output) {
        if (bitSet.length() <= size) {
            byte[] abyte = bitSet.toByteArray();

            output.write(Arrays.copyOf(abyte, -Math.floorDiv(-size, 8)));
        }
    }

    public static int readContainerIdUnsigned(ByteArrayDataInputWrapper in, int protocolId) {
        return protocolId < ProtocolConstants.MC_1_21_2 ? in.readUnsignedByte() : readVarInt(in);
    }

    public static int readContainerIdSigned(ByteArrayDataInputWrapper in, int protocolId) {
        return protocolId < ProtocolConstants.MC_1_21_2 ? in.readByte() : readVarInt(in);
    }

    public static int readContainerIdVarInt(ByteArrayDataInputWrapper in, int protocolId) {
        return readVarInt(in);
    }

    public static void writeContainerId(int containerId, ByteArrayDataOutput out, int protocolId) {
        if (protocolId < ProtocolConstants.MC_1_21_2)
            out.writeByte(containerId);
        else
            writeVarInt(containerId, out);
    }

}
