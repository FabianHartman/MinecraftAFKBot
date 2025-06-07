package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.play.OpenWindowEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@Getter
@NoArgsConstructor
@ToString
public class PacketInOpenWindow extends Packet {
    private int windowId;
    private int windowType;
    private String title;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        if (protocolId <= ProtocolConstants.MC_1_13_2) {
            this.windowId = in.readUnsignedByte();
            this.windowType = readString(in).hashCode();
            this.title = readChatComponent(in, protocolId);
            in.readUnsignedByte(); // slots
        } else {
            this.windowId = readContainerIdVarInt(in, protocolId);
            this.windowType = readVarInt(in);
            this.title = readChatComponent(in, protocolId);
        }

        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new OpenWindowEvent(windowId, windowType, title));
    }
}