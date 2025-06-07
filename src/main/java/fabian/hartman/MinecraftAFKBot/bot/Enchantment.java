package fabian.hartman.MinecraftAFKBot.bot;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.bot.registry.Registries;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponentPart;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Enchantment implements DataComponentPart {
    private int enchantmentId = -1;
    private String enchantmentType;
    private int level;

    public Enchantment(String enchantmentType, int level) {
        this.enchantmentType = enchantmentType;
        this.level = level;
    }

    public String getEnchantmentNameWithoutNamespace() {
        return enchantmentType.replace("minecraft:", "");
    }

    public String getDisplayName() {
        return MinecraftAFKBot.getInstance().getCurrentBot().getMinecraftTranslations().getEnchantmentName(enchantmentType);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(enchantmentId, out);
        Packet.writeVarInt(level, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.enchantmentId = Packet.readVarInt(in);
        this.level = Packet.readVarInt(in);
        this.enchantmentType = Registries.ENCHANTMENT.getEnchantmentName(enchantmentId, protocolId);
    }

    @Override
    public String toString(int protocolId) {
        return "{enchantment=" + getEnchantmentType() + ",level=" + level + "}";
    }
}
