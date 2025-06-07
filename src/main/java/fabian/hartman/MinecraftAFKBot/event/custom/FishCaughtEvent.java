package fabian.hartman.MinecraftAFKBot.event.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.bot.Item;
import fabian.hartman.MinecraftAFKBot.bot.loot.LootItem;
import fabian.hartman.MinecraftAFKBot.event.Event;

@Getter
@AllArgsConstructor
public class FishCaughtEvent extends Event {
    private Item item;
    private LootItem lootItem;
}