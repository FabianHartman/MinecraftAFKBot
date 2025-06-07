package fabian.hartman.MinecraftAFKBot.network.item;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.bot.Enchantment;

import java.util.List;

public interface ItemData {
    void write(ByteArrayDataOutput output, int protocolId);
    default void writeHashes(ByteArrayDataOutput output, int protocolId) {}
}