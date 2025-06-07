package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@Getter
public class DamageComponent extends DataComponent {
    private int damage = -1;

    public DamageComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(damage, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.damage = Packet.readVarInt(in);
    }

    @Override
    public String toString(int protocolId) {
        return super.toString(protocolId) + "[damage=" + damage + "]";
    }
}