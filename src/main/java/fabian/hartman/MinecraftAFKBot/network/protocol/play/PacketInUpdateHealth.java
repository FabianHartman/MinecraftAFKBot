package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.play.UpdateHealthEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@Getter
@NoArgsConstructor
public class PacketInUpdateHealth extends Packet {
    private float health;
    private int food;
    private float saturation;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        this.health = in.readFloat();
        this.food = readVarInt(in);
        this.saturation = in.readFloat();

        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdateHealthEvent(MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().getEntityID(), getHealth(), getFood(), getSaturation()));
    }
}