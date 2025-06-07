package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.bees;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponentPart;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;
import fabian.hartman.MinecraftAFKBot.utils.nbt.NBTTag;

@Getter
@NoArgsConstructor
public class Bee implements DataComponentPart {

    private NBTTag entityData;
    private int ticksInHive;
    private int minTicksInHive;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeNBT(entityData, out);
        Packet.writeVarInt(ticksInHive, out);
        Packet.writeVarInt(minTicksInHive, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.entityData = Packet.readNBT(in, protocolId);
        this.ticksInHive = Packet.readVarInt(in);
        this.minTicksInHive = Packet.readVarInt(in);
    }
}