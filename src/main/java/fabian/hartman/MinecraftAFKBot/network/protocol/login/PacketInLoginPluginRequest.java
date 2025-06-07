package fabian.hartman.MinecraftAFKBot.network.protocol.login;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.login.LoginPluginRequestEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

public class PacketInLoginPluginRequest extends Packet {
    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        int msgId = readVarInt(in);
        String channel = readString(in);
        byte[] data = new byte[in.getAvailable()];
        in.readFully(data);
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new LoginPluginRequestEvent(msgId, channel, data));
    }
}