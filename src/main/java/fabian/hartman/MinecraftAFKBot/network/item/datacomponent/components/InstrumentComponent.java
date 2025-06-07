package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.instrument.Instrument;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

public class InstrumentComponent extends DataComponent {
    private Instrument instrument;

    public InstrumentComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        instrument.write(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.instrument = new Instrument();
        instrument.read(in, protocolId);
    }
}