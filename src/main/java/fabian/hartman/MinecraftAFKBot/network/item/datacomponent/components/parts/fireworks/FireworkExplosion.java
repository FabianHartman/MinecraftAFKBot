package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.fireworks;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponentPart;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class FireworkExplosion implements DataComponentPart {
    private int shape;
    private int[] colors;
    private int[] fadeColors;
    private boolean hasTrail;
    private boolean hasTwinkle;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(shape, out);

        Packet.writeVarInt(colors == null ? 0 : colors.length, out);
        for (int color : colors) {
            out.writeInt(color);
        }

        Packet.writeVarInt(fadeColors == null ? 0 : fadeColors.length, out);
        for (int fadeColor : fadeColors) {
            out.writeInt(fadeColor);
        }

        out.writeBoolean(hasTrail);
        out.writeBoolean(hasTwinkle);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.shape = Packet.readVarInt(in);

        int count = Packet.readVarInt(in);
        this.colors = new int[count];
        for (int i = 0; i < count; i++) {
            colors[i] = in.readInt();
        }

        count = Packet.readVarInt(in);
        this.fadeColors = new int[count];
        for (int i = 0; i < count; i++) {
            fadeColors[i] = in.readInt();
        }

        this.hasTrail = in.readBoolean();
        this.hasTwinkle = in.readBoolean();
    }
}