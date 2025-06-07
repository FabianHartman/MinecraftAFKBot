package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;


@AllArgsConstructor
@Getter
public class PacketOutEntityAction extends Packet {
    private final EntityAction entityAction;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        if (entityAction == EntityAction.OTHER) {
            throw new IllegalArgumentException("EntityAction#OTHER is not allowed in PacketOutEntityAction.");
        }

        Packet.writeVarInt(MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().getEntityID(), out);   // Entity ID
        Packet.writeVarInt(entityAction.ordinal(), out);                               // Action ID (only supported 0-4, see EntityAction enum)
        Packet.writeVarInt(0, out);                                              // Horse jump strength
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }

    public enum EntityAction {
        START_SNEAKING,
        STOP_SNEAKING,
        LEAVE_BED,
        START_SPRINTING,
        STOP_SPRINTING,
        OTHER
    }
}