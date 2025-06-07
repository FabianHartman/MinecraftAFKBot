package fabian.hartman.MinecraftAFKBot.network.item.datacomponent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import fabian.hartman.MinecraftAFKBot.bot.registry.Registries;

@Getter
@RequiredArgsConstructor
public abstract class DataComponent implements DataComponentPart {
    private final int componentTypeId;

    @Override
    public String toString(int protocolId) {
        return Registries.DATA_COMPONENT_TYPE.getElement(componentTypeId, protocolId);
    }
}
