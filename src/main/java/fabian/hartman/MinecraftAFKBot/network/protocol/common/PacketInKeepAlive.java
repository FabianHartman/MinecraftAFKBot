package fabian.hartman.MinecraftAFKBot.network.protocol.common;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.common.KeepAliveEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class PacketInKeepAlive extends Packet {
    private long id;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        switch (protocolId) {
            case ProtocolConstants.MC_1_12_1:
            case ProtocolConstants.MC_1_12:
            case ProtocolConstants.MC_1_11_1:
            case ProtocolConstants.MC_1_11:
            case ProtocolConstants.MC_1_10:
            case ProtocolConstants.MC_1_9_4:
            case ProtocolConstants.MC_1_9_2:
            case ProtocolConstants.MC_1_9_1:
            case ProtocolConstants.MC_1_9:
            case ProtocolConstants.MC_1_8: {
                this.id = Integer.valueOf(readVarInt(in)).longValue();
                break;
            }
            case ProtocolConstants.MC_1_13_2:
            case ProtocolConstants.MC_1_13_1:
            case ProtocolConstants.MC_1_13:
            case ProtocolConstants.MC_1_12_2:
            case ProtocolConstants.MC_1_14:
            case ProtocolConstants.MC_1_14_1:
            case ProtocolConstants.MC_1_14_2:
            case ProtocolConstants.MC_1_14_3:
            case ProtocolConstants.MC_1_14_4:
            default: {
                this.id = in.readLong();
                break;
            }
        }
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new KeepAliveEvent(getId()));
    }
}