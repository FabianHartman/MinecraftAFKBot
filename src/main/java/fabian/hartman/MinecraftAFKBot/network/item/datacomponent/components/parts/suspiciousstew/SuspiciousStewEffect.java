package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.suspiciousstew;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponentPart;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class SuspiciousStewEffect implements DataComponentPart {
    private int effectId;
    private int duration;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(effectId, out);
        Packet.writeVarInt(duration, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.effectId = Packet.readVarInt(in);
        this.duration = Packet.readVarInt(in);
    }
}