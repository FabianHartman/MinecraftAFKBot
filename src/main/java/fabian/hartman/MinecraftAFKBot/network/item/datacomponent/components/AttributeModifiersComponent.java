package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.attributemodifier.AttributeModifier;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
public class AttributeModifiersComponent extends DataComponent {
    private List<AttributeModifier> modifiers = Collections.emptyList();
    private boolean showInTooltip = true;

    public AttributeModifiersComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(modifiers.size(), out);
        for (AttributeModifier modifier : modifiers) {
            modifier.write(out, protocolId);
        }

        if (protocolId < ProtocolConstants.MC_1_21_5)
            out.writeBoolean(showInTooltip);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        // modifiers
        this.modifiers = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            AttributeModifier modifier = new AttributeModifier();
            modifier.read(in, protocolId);
            modifiers.add(modifier);
        }

        // show_in_tooltip
        if (protocolId < ProtocolConstants.MC_1_21_5)
            this.showInTooltip = in.readBoolean();
    }
}