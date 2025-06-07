package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@Getter
@AllArgsConstructor
public class PacketOutPosLook extends Packet {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean onGround;
    private boolean horizontalCollision;

    public PacketOutPosLook(float yaw, float pitch) {
        this(MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().getX(), MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().getY(), MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().getZ(), yaw, pitch, true, true);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        out.writeDouble(getX());
        out.writeDouble(getY());
        out.writeDouble(getZ());
        out.writeFloat(getYaw());
        out.writeFloat(getPitch());
        if (protocolId >= ProtocolConstants.MC_1_21_2)
            out.writeByte(PacketOutPosition.packFlags(onGround, horizontalCollision));
        else
            out.writeBoolean(onGround);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }
}