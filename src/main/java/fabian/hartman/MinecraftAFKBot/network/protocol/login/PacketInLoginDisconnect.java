package fabian.hartman.MinecraftAFKBot.network.protocol.login;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.login.LoginDisconnectEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

public class PacketInLoginDisconnect extends Packet {
    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        String errorMessage = readChatComponent(in, ProtocolConstants.MC_1_20_2); // always only a string, don't ask why
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new LoginDisconnectEvent(errorMessage));
    }
}