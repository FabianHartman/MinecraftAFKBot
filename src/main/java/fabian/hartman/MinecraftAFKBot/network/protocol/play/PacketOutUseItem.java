package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.bot.Player;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;
import fabian.hartman.MinecraftAFKBot.utils.LocationUtils;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PacketOutUseItem extends Packet {
    private int x = -1;   // ||
    private int y = 4095; // \/
    private int z = -1;   // blockpos = -1
    private byte cursorX = 0;
    private byte cursorY = 0;
    private byte cursorZ = 0;
    private float yaw = 0;
    private float pitch = 0;
    private PacketOutBlockPlace.BlockFace blockFace = PacketOutBlockPlace.BlockFace.UNSET;

    public PacketOutUseItem(Player player) {
        this.yaw = player.getYaw();
        this.pitch = player.getPitch();
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        if (protocolId == ProtocolConstants.MC_1_8) {
            out.writeLong(LocationUtils.toBlockPos(x, y, z));
            out.writeByte(blockFace == PacketOutBlockPlace.BlockFace.UNSET ? 255 : blockFace.ordinal());
            Packet.writeSlot(MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().getHeldItem(), out, protocolId);
            out.writeByte(cursorX);
            out.writeByte(cursorY);
            out.writeByte(cursorZ);
            new Thread(() -> {
                try { Thread.sleep(100); } catch (InterruptedException ignore) { }
                MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutArmAnimation());
            }).start();
        } else {
            writeVarInt(PacketOutBlockPlace.Hand.MAIN_HAND.ordinal(), out);
            if (protocolId >= ProtocolConstants.MC_1_19)
                writeVarInt(0, out); //sequence
            if (protocolId >= ProtocolConstants.MC_1_21) {
                out.writeFloat(yaw);
                out.writeFloat(pitch);
            }
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        // Only outgoing packet
    }
}