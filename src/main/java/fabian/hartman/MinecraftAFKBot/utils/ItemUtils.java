package fabian.hartman.MinecraftAFKBot.utils;

import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.bot.Slot;
import fabian.hartman.MinecraftAFKBot.bot.registry.Registries;
import fabian.hartman.MinecraftAFKBot.bot.registry.Registry;
import fabian.hartman.MinecraftAFKBot.bot.registry.legacy.LegacyMaterial;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;

import java.util.*;

public class ItemUtils {
    private static final Map<Integer, Integer> rodId = new HashMap<>();

    public static int getRodId(int protocolId) {
        if (rodId.containsKey(protocolId))
            return rodId.get(protocolId);

        Registry<Integer, String> itemRegistry = Registries.ITEM.getRegistry(protocolId);
        if (itemRegistry.containsValue("minecraft:fishing_rod")) {
            int id = itemRegistry.findKey("minecraft:fishing_rod");
            rodId.put(protocolId, id);
            return id;
        }
        return 563;
    }

    public static boolean isFishingRod(Slot slot) {
        if (slot == null)
            return false;
        if (!slot.isPresent())
            return false;
        int protocol = ProtocolConstants.getLatest();
        if (MinecraftAFKBot.getInstance().getCurrentBot() != null)
            protocol = MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol();

        if (protocol < ProtocolConstants.MC_1_13)
            return LegacyMaterial.getMaterial(slot.getItemId()) == LegacyMaterial.FISHING_ROD;
        else
            return getRodId(protocol) == slot.getItemId();
    }

    public static String getItemName(Slot slot) {
        if (MinecraftAFKBot.getInstance().getCurrentBot() == null || !slot.isPresent())
            return "N/A";
        int version = MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol();
        if (version <= ProtocolConstants.MC_1_12_2) {
            return LegacyMaterial.getMaterialName(slot.getItemId(), Integer.valueOf(slot.getItemDamage()).shortValue());
        } else {
            return Registries.ITEM.getItemName(slot.getItemId(), version).replace("minecraft:", "");
        }
    }
}