package fabian.hartman.MinecraftAFKBot.bot;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "eid")
public class Item {
    private int eid;
    @Setter private Integer itemId;
    @Setter private String name;
    @Setter private List<Enchantment> enchantments;
    @Setter private int motX;
    @Setter private int motY;
    @Setter private int motZ;
    private final double originX;
    private final double originY;
    private final double originZ;

    @Override
    public String toString() {
        return eid + ":" + name + " (" + motX + "/" + motY + "/" + motZ + ")";
    }

    public int getMaxMot() {
        return getMaxMot(motX, motY, motZ);
    }

    public String getDisplayName() {
        return MinecraftAFKBot.getInstance().getCurrentBot().getMinecraftTranslations().getItemName(getName());
    }

    public static int getMaxMot(int motX, int motY, int motZ) {
        return Math.abs(motX) + Math.abs(motY) + Math.abs(motZ);
    }
}
