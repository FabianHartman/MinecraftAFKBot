package fabian.hartman.MinecraftAFKBot.bot.registry.registries;

import fabian.hartman.MinecraftAFKBot.bot.registry.MetaRegistry;
import fabian.hartman.MinecraftAFKBot.bot.registry.Registry;
import fabian.hartman.MinecraftAFKBot.bot.registry.RegistryLoader;
import fabian.hartman.MinecraftAFKBot.bot.registry.legacy.LegacyMaterial;

import java.util.Arrays;
import java.util.Optional;

public class ItemRegistry extends MetaRegistry<Integer, String> {
    public ItemRegistry() {
        Registry<Integer, String> legacyRegistry = new Registry<>();
        Arrays.stream(LegacyMaterial.values()).forEach(legacyMaterial -> {
            legacyRegistry.registerElement(legacyMaterial.getId(), legacyMaterial.name());
        });

        load(RegistryLoader.simple("minecraft:item"), legacyRegistry);
    }

    public String getItemName(int id, int protocol) {
        return Optional.ofNullable(getElement(id, protocol)).orElse("Modded Item (" + id + ")");
    }
}