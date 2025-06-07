package fabian.hartman.MinecraftAFKBot.bot.loot;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LootHistory {
    private final List<LootItem> items = new ArrayList<>();
}