package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponentPart;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

public class SimpleMapperComponent extends DataComponent {
    private final DataComponentPart part;

    public SimpleMapperComponent(DataComponentPart part, int componentTypeId) {
        super(componentTypeId);
        this.part = part;
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        part.write(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        part.read(in, protocolId);
    }
}