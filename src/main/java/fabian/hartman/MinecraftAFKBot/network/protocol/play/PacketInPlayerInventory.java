package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.bot.Slot;
import fabian.hartman.MinecraftAFKBot.event.play.UpdateSlotEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@Getter
@NoArgsConstructor
public class PacketInPlayerInventory extends Packet {
    private int slotId;
    private Slot item;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        this.slotId = readVarInt(in);
        if (MinecraftAFKBot.getInstance().getConfig().isLogItemData())
            MinecraftAFKBot.getLog().info("Start reading PacketInPlayerInventory slot");
        this.item = readSlot(in, protocolId, networkHandler.getDataComponentRegistry());
        if (MinecraftAFKBot.getInstance().getConfig().isLogItemData())
            MinecraftAFKBot.getLog().info("End of reading PacketInPlayerInventory slot");
        if (in.getAvailable() > 0)
            MinecraftAFKBot.getLog().warning("End of reading PacketInPlayerInventory has " + in.getAvailable() + " byte(s) left");
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdateSlotEvent(0, Integer.valueOf(slotId).shortValue(), item));
    }
}