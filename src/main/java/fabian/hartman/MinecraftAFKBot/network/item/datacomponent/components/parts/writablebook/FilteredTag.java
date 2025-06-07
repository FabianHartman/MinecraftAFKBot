package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.writablebook;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponentPart;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;
import fabian.hartman.MinecraftAFKBot.utils.nbt.NBTTag;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class FilteredTag implements DataComponentPart {
    private NBTTag raw;
    private Optional<NBTTag> filtered;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeNBT(raw, out);
        out.writeBoolean(filtered.isPresent());
        filtered.ifPresent(s -> Packet.writeNBT(s, out));
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.raw = Packet.readNBT(in, protocolId);
        if (in.readBoolean())
            filtered = Optional.of(Packet.readNBT(in, protocolId));
        else
            filtered = Optional.empty();
    }
}