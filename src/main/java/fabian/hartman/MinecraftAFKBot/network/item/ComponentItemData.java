package fabian.hartman.MinecraftAFKBot.network.item;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import fabian.hartman.MinecraftAFKBot.bot.Enchantment;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.EnchantmentsComponent;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class ComponentItemData implements ItemData {
    private final List<DataComponent> presentComponents;
    private final List<DataComponent> removedComponents;

    @Override
    public void write(ByteArrayDataOutput output, int protocolId) {
        Packet.writeVarInt(presentComponents.size(), output);
        Packet.writeVarInt(removedComponents.size(), output);
        for (DataComponent presentComponent : presentComponents) {
            Packet.writeVarInt(presentComponent.getComponentTypeId(), output);
            presentComponent.write(output, protocolId);
        }
        for (DataComponent emptyComponent : removedComponents) {
            Packet.writeVarInt(emptyComponent.getComponentTypeId(), output);
        }
    }
}