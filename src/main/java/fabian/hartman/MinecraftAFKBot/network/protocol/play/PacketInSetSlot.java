package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.bot.Slot;
import fabian.hartman.MinecraftAFKBot.event.play.UpdateSlotEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@NoArgsConstructor
@Getter
public class PacketInSetSlot extends Packet {
    private int windowId;
    private short slotId;
    private Slot slot;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        this.windowId = Packet.readContainerIdSigned(in, protocolId);
        if (protocolId >= ProtocolConstants.MC_1_17_1)
            readVarInt(in); // revision
        this.slotId = in.readShort();
        if (MinecraftAFKBot.getInstance().getConfig().isLogItemData())
            MinecraftAFKBot.getLog().info("Start reading PacketInSetSlot slot");
        this.slot = readSlot(in, protocolId, networkHandler.getDataComponentRegistry());
        if (MinecraftAFKBot.getInstance().getConfig().isLogItemData())
            MinecraftAFKBot.getLog().info("End of reading PacketInSetSlot slot");
        if (in.getAvailable() > 0)
            MinecraftAFKBot.getLog().warning("End of reading PacketInSetSlot has " + in.getAvailable() + " byte(s) left");
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdateSlotEvent(windowId, slotId, slot));
    }
}