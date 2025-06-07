package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.play.PosLookChangeEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@NoArgsConstructor
@Getter
public class PacketInPlayerPosLook extends Packet {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private int teleportId;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        if (protocolId < ProtocolConstants.MC_1_21_2) {
            double x = in.readDouble();
            double y = in.readDouble();
            double z = in.readDouble();
            float yaw = in.readFloat();
            float pitch = in.readFloat();
            if (in.readByte() == 0) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.yaw = yaw;
                this.pitch = pitch;
                if (protocolId >= ProtocolConstants.MC_1_9) {
                    this.teleportId = readVarInt(in); //tID
                }
                if (protocolId >= ProtocolConstants.MC_1_17 && protocolId <= ProtocolConstants.MC_1_19_3) {
                    in.readBoolean(); // should dismount
                }
                MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new PosLookChangeEvent(x, y, z, yaw, pitch, teleportId));
            }
        } else {
            this.teleportId = readVarInt(in);
            double x = in.readDouble();
            double y = in.readDouble();
            double z = in.readDouble();
            in.readDouble(); //dx
            in.readDouble(); //dy
            in.readDouble(); //dz
            float yaw = in.readFloat();
            float pitch = in.readFloat();
            int relatives = in.readInt();
            if (relatives == 0) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.yaw = yaw;
                this.pitch = pitch;
                MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new PosLookChangeEvent(x, y, z, yaw, pitch, teleportId));
            }
        }
    }
}