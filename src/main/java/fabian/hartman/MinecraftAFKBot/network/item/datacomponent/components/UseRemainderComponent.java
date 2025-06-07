package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.bot.Slot;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponentRegistry;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

public class UseRemainderComponent extends DataComponent {
    private final DataComponentRegistry dataComponentRegistry;

    private Slot usingConvertsTo = Slot.EMPTY;

    public UseRemainderComponent(DataComponentRegistry dataComponentRegistry, int componentTypeId) {
        super(componentTypeId);
        this.dataComponentRegistry = dataComponentRegistry;
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeSlot(usingConvertsTo, out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        if (MinecraftAFKBot.getInstance().getConfig().isLogItemData()) {
            MinecraftAFKBot.getLog().info("Start reading UseRemainderComponent");
        }
        this.usingConvertsTo = Packet.readSlot(in, protocolId, dataComponentRegistry);
        if (MinecraftAFKBot.getInstance().getConfig().isLogItemData()) {
            MinecraftAFKBot.getLog().info("End of reading UseRemainderComponent");
        }
    }
}