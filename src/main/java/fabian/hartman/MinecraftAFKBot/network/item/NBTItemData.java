package fabian.hartman.MinecraftAFKBot.network.item;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.utils.nbt.*;
import lombok.RequiredArgsConstructor;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;

@RequiredArgsConstructor
public class NBTItemData implements ItemData {
    private final NBTTag nbtData;

    @Override
    public void write(ByteArrayDataOutput output, int protocolId) {
        Packet.writeNBT(nbtData, output);
    }
}