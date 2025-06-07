package fabian.hartman.MinecraftAFKBot.network.protocol.common;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.common.ResourcePackEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class PacketInResourcePack extends Packet {
    private UUID uuid;
    private String url;
    private String hash;
    private boolean forced;
    private String prompt;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        if (protocolId >= ProtocolConstants.MC_1_20_3)
            this.uuid = readUUID(in);
        this.url = readString(in);
        this.hash = readString(in);
        this.forced = in.readBoolean();
        if (in.readBoolean())
            this.prompt = readChatComponent(in, protocolId);
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new ResourcePackEvent(uuid, url, hash, forced, prompt));
    }
}