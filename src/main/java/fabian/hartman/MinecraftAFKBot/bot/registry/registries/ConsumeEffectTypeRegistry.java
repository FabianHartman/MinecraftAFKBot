package fabian.hartman.MinecraftAFKBot.bot.registry.registries;

import fabian.hartman.MinecraftAFKBot.bot.registry.MetaRegistry;
import fabian.hartman.MinecraftAFKBot.bot.registry.RegistryLoader;

public class ConsumeEffectTypeRegistry extends MetaRegistry<Integer, String> {
    public ConsumeEffectTypeRegistry() {
        load(RegistryLoader.simple("minecraft:consume_effect_type"));
    }

    public String getConsumeEffectTypeName(int consumeEffectTypeId, int protocol) {
        return getElement(consumeEffectTypeId, protocol);
    }
}
