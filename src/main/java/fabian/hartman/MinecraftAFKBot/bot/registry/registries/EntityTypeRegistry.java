package fabian.hartman.MinecraftAFKBot.bot.registry.registries;

import fabian.hartman.MinecraftAFKBot.bot.registry.MetaRegistry;
import fabian.hartman.MinecraftAFKBot.bot.registry.Registry;
import fabian.hartman.MinecraftAFKBot.bot.registry.RegistryLoader;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;

public class EntityTypeRegistry extends MetaRegistry<Integer, String> {
    public EntityTypeRegistry() {
        Registry<Integer, String> legacy1_8Registry = new Registry<>();
        legacy1_8Registry.registerElement(90, "minecraft:fishing_bobber");
        legacy1_8Registry.registerElement(2, "minecraft:item");
        addRegistry(ProtocolConstants.MC_1_8, legacy1_8Registry);

        load(RegistryLoader.simple("minecraft:entity_type"));
    }
}