package fabian.hartman.MinecraftAFKBot.network.item.datacomponent;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

public interface DataComponentPart {
    void write(ByteArrayDataOutput out, int protocolId);
    void read(ByteArrayDataInputWrapper in, int protocolId);
    default String toString(int protocolId) {
        return "";
    }
}