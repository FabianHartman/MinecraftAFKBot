package fabian.hartman.MinecraftAFKBot.network.protocol.configuration;

import com.google.common.io.ByteArrayDataOutput;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.configuration.ConfigurationFinishEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@NoArgsConstructor
public class PacketInFinishConfiguration extends Packet {
    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new ConfigurationFinishEvent());
    }
}