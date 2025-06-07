package fabian.hartman.MinecraftAFKBot.network.item;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.utils.nbt.*;
import lombok.RequiredArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.bot.Enchantment;
import fabian.hartman.MinecraftAFKBot.bot.registry.Registries;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class NBTItemData implements ItemData {
    private final NBTTag nbtData;

    @Override
    public List<Enchantment> getEnchantments() {
        List<Enchantment> enchantmentList = new ArrayList<>();
        Tag<?> rootTag = nbtData.getTag();
        if (!(rootTag instanceof CompoundTag)) return enchantmentList;
        CompoundTag root = (CompoundTag) rootTag;
        String key;
        if (root.containsKey("StoredEnchantments"))
            key = "StoredEnchantments";
        else if (root.containsKey("ench"))
            key = "ench";
        else if (root.containsKey("Enchantments"))
            key = "Enchantments";
        else
            return enchantmentList;
        List<CompoundTag> enchants = root.get(key, ListTag.class).getValue().stream()
                .filter(tag -> tag instanceof CompoundTag)
                .map(tag -> (CompoundTag) tag)
                .collect(Collectors.toList());
        int protocolId = MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol();
        for (CompoundTag enchant : enchants) {
            String enchType;
            short level = enchant.get("lvl", ShortTag.class).getValue();
            if (protocolId >= ProtocolConstants.MC_1_13) {
                enchType = enchant.get("id", StringTag.class).getValue();
            } else {
                short id = enchant.get("id", ShortTag.class).getValue();
                enchType = Registries.ENCHANTMENT.getEnchantmentName(id, protocolId);
            }
            enchantmentList.add(new Enchantment(enchType, level));
        }
        return enchantmentList;
    }

    @Override
    public void write(ByteArrayDataOutput output, int protocolId) {
        Packet.writeNBT(nbtData, output);
    }
}