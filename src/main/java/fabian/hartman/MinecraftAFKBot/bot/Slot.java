package fabian.hartman.MinecraftAFKBot.bot;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import fabian.hartman.MinecraftAFKBot.network.item.ComponentItemData;
import fabian.hartman.MinecraftAFKBot.network.item.ItemData;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.DamageComponent;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;

@Getter
@AllArgsConstructor
@ToString
public class Slot {
    public static final Slot EMPTY = new Slot(false, -1, (byte) -1, -1, null);

    private boolean present;
    private int itemId;
    private byte itemCount;
    private int itemDamage;
    private ItemData itemData;

    public int getItemDamage() {
        if (itemDamage == -1 && itemData != null && itemData instanceof ComponentItemData) {
            ComponentItemData componentItemData = (ComponentItemData) itemData;
            return this.itemDamage = componentItemData.getPresentComponents().stream()
                    .filter(dataComponent -> dataComponent instanceof DamageComponent)
                    .mapToInt(dataComponent -> ((DamageComponent) dataComponent).getDamage())
                    .findAny().orElse(-1);
        }
        return itemDamage;
    }

    public void writeItemData(ByteArrayDataOutput output, int protocolId) {
        if (itemData == null) {
            if (protocolId >= ProtocolConstants.MC_1_20_5) {
                Packet.writeVarInt(0, output);
                Packet.writeVarInt(0, output);
            }
            return;
        }
        itemData.write(output, protocolId);
    }

    public void writeHashedItemData(ByteArrayDataOutput output, int protocolId) {
        if (protocolId < ProtocolConstants.MC_1_21_5) return; // should not happen
        if (itemData == null) {
            Packet.writeVarInt(0, output);
            Packet.writeVarInt(0, output);
            return;
        }
        itemData.writeHashes(output, protocolId);
    }
}