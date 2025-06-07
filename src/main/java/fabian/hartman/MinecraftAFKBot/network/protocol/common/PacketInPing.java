package fabian.hartman.MinecraftAFKBot.network.protocol.common;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.common.PingPacketEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class PacketInPing extends Packet {
    private int id;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        this.id = in.readInt();
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new PingPacketEvent(id));
    }
}