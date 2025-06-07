package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.lodestone;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponentPart;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class BlockPos implements DataComponentPart {
    private long pos;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeLong(pos);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.pos = in.readLong();
    }
}