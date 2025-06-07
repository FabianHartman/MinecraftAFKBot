package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.banner;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponentPart;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class BannerPattern implements DataComponentPart {

    private int pattern;
    private int dyeColor;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(pattern, out);
        Packet.writeVarInt(dyeColor, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.pattern = Packet.readVarInt(in);
        this.dyeColor = Packet.readVarInt(in);
    }
}