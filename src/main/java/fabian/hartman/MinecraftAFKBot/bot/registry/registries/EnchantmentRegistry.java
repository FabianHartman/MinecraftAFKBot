package fabian.hartman.MinecraftAFKBot.bot.registry.registries;

import fabian.hartman.MinecraftAFKBot.bot.registry.MetaRegistry;
import fabian.hartman.MinecraftAFKBot.bot.registry.Registry;
import fabian.hartman.MinecraftAFKBot.bot.registry.RegistryLoader;
import fabian.hartman.MinecraftAFKBot.bot.registry.legacy.LegacyEnchantmentType;

import java.util.Optional;

public class EnchantmentRegistry extends MetaRegistry<Integer, String> {
    public EnchantmentRegistry() {
        Registry<Integer, String> legacyRegistry = new Registry<>();
        for (LegacyEnchantmentType legacyEnchantmentType : LegacyEnchantmentType.values()) {
            String name = "minecraft:" + legacyEnchantmentType.getName();
            legacyRegistry.registerElement(legacyEnchantmentType.getLegacyId(), name);
        }

        load(RegistryLoader.simple("minecraft:enchantment"), legacyRegistry);
    }

    public String getEnchantmentName(int id, int protocol) {
        return Optional.ofNullable(getElement(id, protocol)).orElse("Unknown Enchantment");
    }
}