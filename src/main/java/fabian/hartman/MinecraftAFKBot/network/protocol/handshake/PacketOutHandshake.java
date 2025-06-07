package fabian.hartman.MinecraftAFKBot.network.protocol.handshake;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@AllArgsConstructor
public class PacketOutHandshake extends Packet {
    private String serverName;
    private int serverPort;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        writeVarInt(MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol(), out);
        if (MinecraftAFKBot.getInstance().getCurrentBot().getConfig().isSpoofForge())
            writeString(serverName + "\0FML\0", out);
        else
            writeString(serverName, out);
        out.writeShort(serverPort);
        writeVarInt(2, out); //next State = 2 -> LOGIN
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) { }
}