package fabian.hartman.MinecraftAFKBot.bot.loot;

import lombok.AllArgsConstructor;
import lombok.Data;
import fabian.hartman.MinecraftAFKBot.bot.Enchantment;

import java.util.List;

@Data
@AllArgsConstructor
public class LootItem {
    private String name;
    private String displayName;
    private int count;
    private List<Enchantment> enchantments;
    private ImagedName imagedName;
}