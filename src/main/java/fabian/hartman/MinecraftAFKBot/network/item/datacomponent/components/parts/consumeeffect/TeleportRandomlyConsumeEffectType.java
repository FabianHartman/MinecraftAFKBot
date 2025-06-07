package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.consumeeffect;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponentPart;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class TeleportRandomlyConsumeEffectType implements DataComponentPart {
    private float diameter;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeFloat(diameter);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.diameter = in.readFloat();
    }
}