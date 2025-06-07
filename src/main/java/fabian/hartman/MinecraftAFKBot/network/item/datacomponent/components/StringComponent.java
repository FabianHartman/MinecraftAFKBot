package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@Getter
public class StringComponent extends DataComponent {
    private String value;

    public StringComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeString(value, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.value = Packet.readString(in);
    }

    @Override
    public String toString(int protocolId) {
        return super.toString(protocolId) + "[value=" + value + "]";
    }
}