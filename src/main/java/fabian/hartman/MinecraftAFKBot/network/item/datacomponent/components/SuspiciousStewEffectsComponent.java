package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.suspiciousstew.SuspiciousStewEffect;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
public class SuspiciousStewEffectsComponent extends DataComponent {
    private List<SuspiciousStewEffect> effects = Collections.emptyList();

    public SuspiciousStewEffectsComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(effects.size(), out);
        for (SuspiciousStewEffect effect : effects) {
            effect.write(out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.effects = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            SuspiciousStewEffect effect = new SuspiciousStewEffect();
            effect.read(in, protocolId);
            effects.add(effect);
        }
    }
}