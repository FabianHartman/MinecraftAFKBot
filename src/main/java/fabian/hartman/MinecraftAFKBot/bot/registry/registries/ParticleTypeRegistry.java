package fabian.hartman.MinecraftAFKBot.bot.registry.registries;

import fabian.hartman.MinecraftAFKBot.bot.registry.MetaRegistry;
import fabian.hartman.MinecraftAFKBot.bot.registry.RegistryLoader;

public class ParticleTypeRegistry extends MetaRegistry<Integer, String> {
    public ParticleTypeRegistry() {
        load(RegistryLoader.simple("minecraft:particle_type"));
    }

    public String getParticleName(int particleTypeId, int protocol) {
        return getElement(particleTypeId, protocol);
    }
}