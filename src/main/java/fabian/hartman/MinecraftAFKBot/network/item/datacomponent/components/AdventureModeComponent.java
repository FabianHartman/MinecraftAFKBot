package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.adventuremode.BlockPredicate;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AdventureModeComponent extends DataComponent {
    private List<BlockPredicate> blockPredicates = Collections.emptyList();
    private boolean showInTooltip;

    public AdventureModeComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(blockPredicates.size(), out);
        for (BlockPredicate blockPredicate : blockPredicates) {
            blockPredicate.write(out, protocolId);
        }
        if (protocolId < ProtocolConstants.MC_1_21_5)
            out.writeBoolean(showInTooltip);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.blockPredicates = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            BlockPredicate predicate = new BlockPredicate();
            predicate.read(in, protocolId);
            blockPredicates.add(predicate);
        }
        if (protocolId < ProtocolConstants.MC_1_21_5)
            this.showInTooltip = in.readBoolean();
    }
}