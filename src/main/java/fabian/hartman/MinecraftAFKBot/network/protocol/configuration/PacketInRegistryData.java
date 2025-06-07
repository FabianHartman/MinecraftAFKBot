package fabian.hartman.MinecraftAFKBot.network.protocol.configuration;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.configuration.RegistryDataEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;
import fabian.hartman.MinecraftAFKBot.utils.nbt.NBTTag;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

@Getter
@NoArgsConstructor
public class PacketInRegistryData extends Packet {
    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        String registryId = readString(in);
        int count = readVarInt(in);
        SortedMap<String, @Nullable NBTTag> data = new TreeMap<>();
        for (int i = 0; i < count; i++) {
            String identifier = readString(in);
            if (in.readBoolean()) {
                data.put(identifier, readNBT(in, protocolId));
            } else {
                data.put(identifier, null);
            }
        }
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new RegistryDataEvent(registryId, data));
    }
}