package fabian.hartman.MinecraftAFKBot.bot.registry.registries;

import fabian.hartman.MinecraftAFKBot.bot.registry.MetaRegistry;
import fabian.hartman.MinecraftAFKBot.bot.registry.RegistryLoader;

import java.util.Optional;

public class ConsumeEffectTypeRegistry extends MetaRegistry<Integer, String> {
    public ConsumeEffectTypeRegistry() {
        load(RegistryLoader.simple("minecraft:consume_effect_type"));
    }

    public int getConsumeEffectTypeId(String consumeEffectTypeName, int protocol) {
        return Optional.ofNullable(findKey(consumeEffectTypeName, protocol)).orElse(0);
    }

    public String getConsumeEffectTypeName(int consumeEffectTypeId, int protocol) {
        return getElement(consumeEffectTypeId, protocol);
    }
}
