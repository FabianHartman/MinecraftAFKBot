package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.trim.TrimMaterial;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.trim.TrimPattern;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

public class TrimComponent extends DataComponent {
    private TrimMaterial trimMaterial;
    private TrimPattern trimPattern;
    private boolean showInTooltip;

    public TrimComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        trimMaterial.write(out, protocolId);
        trimPattern.write(out, protocolId);
        if (protocolId < ProtocolConstants.MC_1_21_5)
            out.writeBoolean(showInTooltip);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        TrimMaterial trimMaterial = new TrimMaterial();
        trimMaterial.read(in, protocolId);
        this.trimMaterial = trimMaterial;

        TrimPattern trimPattern = new TrimPattern();
        trimPattern.read(in, protocolId);
        this.trimPattern = trimPattern;

        if (protocolId < ProtocolConstants.MC_1_21_5)
            this.showInTooltip = in.readBoolean();
    }
}