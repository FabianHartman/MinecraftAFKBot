package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponentPart;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class Effect implements DataComponentPart {
    private int effectId;
    private EffectDetails details;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(effectId, out);
        details.write(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.effectId = Packet.readVarInt(in);
        EffectDetails details = new EffectDetails();
        details.read(in, protocolId);
        this.details = details;
    }
}