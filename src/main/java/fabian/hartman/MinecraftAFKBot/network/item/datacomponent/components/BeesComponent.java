package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.bees.Bee;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
public class BeesComponent extends DataComponent {
    private List<Bee> bees = Collections.emptyList();

    public BeesComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(bees.size(), out);
        for (Bee bee : bees) {
            bee.write(out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.bees = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            Bee bee = new Bee();
            bee.read(in, protocolId);
            bees.add(bee);
        }
    }
}