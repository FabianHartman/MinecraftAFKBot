package fabian.hartman.MinecraftAFKBot.bot.registry.registries;

import fabian.hartman.MinecraftAFKBot.bot.registry.MetaRegistry;
import fabian.hartman.MinecraftAFKBot.bot.registry.RegistryLoader;

public class DataComponentTypeRegistry extends MetaRegistry<Integer, String> {
    public DataComponentTypeRegistry() {
        load(RegistryLoader.simple("minecraft:data_component_type"));
    }
}