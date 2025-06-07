package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.bot.Slot;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponentRegistry;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ItemListComponent extends DataComponent {
    private final DataComponentRegistry dataComponentRegistry;
    private List<Slot> items = Collections.emptyList();

    public ItemListComponent(DataComponentRegistry dataComponentRegistry, int componentTypeId) {
        super(componentTypeId);
        this.dataComponentRegistry = dataComponentRegistry;
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(items.size(), out);
        for (Slot item : items) {
            Packet.writeSlot(item, out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.items = new LinkedList<>();
        int count = Packet.readVarInt(in);
        if (count <= 0) return;
        if (MinecraftAFKBot.getInstance().getConfig().isLogItemData()) {
            MinecraftAFKBot.getLog().info("Start reading item list component with " + count + " elements");
        }
        for (int i = 0; i < count; i++) {
            this.items.add(Packet.readSlot(in, protocolId, dataComponentRegistry));
        }
        if (MinecraftAFKBot.getInstance().getConfig().isLogItemData()) {
            MinecraftAFKBot.getLog().info("End of reading item list component with " + count + " elements");
        }
    }
}