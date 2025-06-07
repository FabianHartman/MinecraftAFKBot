package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.play.InventoryCloseEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@Getter
@AllArgsConstructor
public class PacketOutCloseInventory extends Packet {
    private int windowId;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        writeContainerId(windowId, out, protocolId);

        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new InventoryCloseEvent(getWindowId()));
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }
}