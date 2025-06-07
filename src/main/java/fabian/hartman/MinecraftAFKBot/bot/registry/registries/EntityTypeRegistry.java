package fabian.hartman.MinecraftAFKBot.bot.registry.registries;

import fabian.hartman.MinecraftAFKBot.bot.registry.MetaRegistry;
import fabian.hartman.MinecraftAFKBot.bot.registry.Registry;
import fabian.hartman.MinecraftAFKBot.bot.registry.RegistryLoader;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;

import java.util.Optional;

public class EntityTypeRegistry extends MetaRegistry<Integer, String> {
    public EntityTypeRegistry() {
        Registry<Integer, String> legacy1_8Registry = new Registry<>();
        legacy1_8Registry.registerElement(90, "minecraft:fishing_bobber");
        legacy1_8Registry.registerElement(2, "minecraft:item");
        addRegistry(ProtocolConstants.MC_1_8, legacy1_8Registry);

        load(RegistryLoader.simple("minecraft:entity_type"));
    }

    public int getEntityType(String entityName, int protocol) {
        return Optional.ofNullable(findKey(entityName, protocol)).orElse(0);
    }

    public String getEntityName(int entityTypeId, int protocol) {
        return Optional.ofNullable(getElement(entityTypeId, protocol)).orElse("Unknown Entity");
    }
}