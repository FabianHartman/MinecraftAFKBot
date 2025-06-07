package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.tool.Rule;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ToolComponent extends DataComponent {
    private List<Rule> rules = Collections.emptyList();
    private float defaultMiningSpeed;
    private int damagePerBlock;
    private boolean canDestroyBlocksInCreative;

    public ToolComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(rules.size(), out);
        for (Rule rule : rules) {
            rule.write(out, protocolId);
        }
        out.writeFloat(defaultMiningSpeed);
        Packet.writeVarInt(damagePerBlock, out);
        if (protocolId >= ProtocolConstants.MC_1_21_5)
            out.writeBoolean(canDestroyBlocksInCreative);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.rules = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            Rule rule = new Rule();
            rule.read(in, protocolId);
            rules.add(rule);
        }
        this.defaultMiningSpeed = in.readFloat();
        this.damagePerBlock = Packet.readVarInt(in);
        if (protocolId >= ProtocolConstants.MC_1_21_5)
            this.canDestroyBlocksInCreative = in.readBoolean();
    }
}