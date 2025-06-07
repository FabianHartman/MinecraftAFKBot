package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.play.ConfirmTransactionEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@Getter
@NoArgsConstructor
public class PacketInConfirmTransaction extends Packet {
    private byte windowId;
    private short action;
    private boolean accepted;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        this.windowId = in.readByte();
        this.action = in.readShort();
        this.accepted = in.readBoolean();

        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new ConfirmTransactionEvent(getWindowId(), getAction(), isAccepted()));
    }
}