package fabian.hartman.MinecraftAFKBot.bot.registry.registries;

import fabian.hartman.MinecraftAFKBot.bot.registry.MetaRegistry;
import fabian.hartman.MinecraftAFKBot.bot.registry.RegistryLoader;

import java.util.Optional;

public class ParticleTypeRegistry extends MetaRegistry<Integer, String> {
    public ParticleTypeRegistry() {
        load(RegistryLoader.simple("minecraft:particle_type"));
    }

    public int getParticleType(String particleName, int protocol) {
        return Optional.ofNullable(findKey(particleName, protocol)).orElse(0);
    }

    public String getParticleName(int particleTypeId, int protocol) {
        return getElement(particleTypeId, protocol);
    }
}