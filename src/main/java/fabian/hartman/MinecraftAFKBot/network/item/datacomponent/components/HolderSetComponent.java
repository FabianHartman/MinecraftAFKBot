package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.HolderSetComponentPart;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

public class HolderSetComponent extends DataComponent {
    private HolderSetComponentPart holderSet;

    public HolderSetComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        holderSet.write(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.holderSet = new HolderSetComponentPart();
        holderSet.read(in, protocolId);
    }
}