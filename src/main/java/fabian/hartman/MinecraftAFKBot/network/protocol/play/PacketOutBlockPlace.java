package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.bot.MovingObjectPositionBlock;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;
import fabian.hartman.MinecraftAFKBot.utils.LocationUtils;

@Getter
@AllArgsConstructor
public class PacketOutBlockPlace extends Packet {
    private Hand hand;
    private int x;
    private int y;
    private int z;
    private BlockFace blockFace;
    private float cursorX;
    private float cursorY;
    private float cursorZ;
    private boolean insideBlock;
    private boolean worldBorderHit;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        if (protocolId >= ProtocolConstants.MC_1_14) {
            writeVarInt(hand.ordinal(), out);
            MovingObjectPositionBlock movObjPos = new MovingObjectPositionBlock(LocationUtils.toBlockPos(x, y, z), blockFace, cursorX, cursorY, cursorZ, insideBlock, worldBorderHit);
            writeMovingObjectPosition(movObjPos, out, protocolId);
            if (protocolId >= ProtocolConstants.MC_1_19)
                writeVarInt(0, out); // sequence
        } else if (protocolId >= ProtocolConstants.MC_1_9) {
            out.writeLong(LocationUtils.toBlockPos(x, y, z));
            writeVarInt(blockFace == PacketOutBlockPlace.BlockFace.UNSET ? 255 : blockFace.ordinal(), out);
            writeVarInt(hand.ordinal(), out);
            if (protocolId >= ProtocolConstants.MC_1_11) {
                out.writeFloat(cursorX);
                out.writeFloat(cursorY);
                out.writeFloat(cursorZ);
            } else {
                out.writeByte((int) cursorX);
                out.writeByte((int) cursorY);
                out.writeByte((int) cursorZ);
            }
        } else {
            System.err.println("Use PacketOutUseItem for 1.8");
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        // Only outgoing packet
    }

    public enum Hand {
        MAIN_HAND,
    }

    public enum BlockFace {
        TOP,
        NORTH,
        SOUTH,
        WEST,
        EAST,
        UNSET;
    }
}