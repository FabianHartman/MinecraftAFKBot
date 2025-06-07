package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.play.EntityTeleportEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@NoArgsConstructor
@Getter
public class PacketInEntityTeleport extends Packet {
    private int entityId;
    private double x;
    private double y;
    private double z;
    private byte yaw;
    private byte pitch;
    private boolean onGround;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        this.entityId = readVarInt(in);
        if (protocolId <= ProtocolConstants.MC_1_8) {
            this.x = in.readInt() / 32.0;
            this.y = in.readInt() / 32.0;
            this.z = in.readInt() / 32.0;
        } else {
            this.x = in.readDouble();
            this.y = in.readDouble();
            this.z = in.readDouble();
            if (protocolId >= ProtocolConstants.MC_1_21_2) {
                in.readDouble(); //dx
                in.readDouble(); //dy
                in.readDouble(); //dz
            }
        }
        int relatives = 0;
        if (protocolId <= ProtocolConstants.MC_1_21) {
            this.yaw = in.readByte();
            this.pitch = in.readByte();
        } else {
            this.yaw = Double.valueOf(Math.floor(in.readFloat() * 256.0F / 360.0F)).byteValue();
            this.pitch = Double.valueOf(Math.floor(in.readFloat() * 256.0F / 360.0F)).byteValue();
            relatives = in.readInt();
        }
        this.onGround = in.readBoolean();
        if (relatives == 0)
            MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new EntityTeleportEvent(entityId, x, y, z, yaw, pitch, onGround));
    }
}