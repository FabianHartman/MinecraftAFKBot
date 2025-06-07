package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@Getter
public class DyedItemColorComponent extends DataComponent {
    private int color;
    private boolean showInTooltip;

    public DyedItemColorComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeInt(color);
        if (protocolId < ProtocolConstants.MC_1_21_5)
            out.writeBoolean(showInTooltip);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.color = in.readInt();
        if (protocolId < ProtocolConstants.MC_1_21_5)
            this.showInTooltip = in.readBoolean();
    }
}