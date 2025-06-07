package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponentPart;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class PossibleEffect implements DataComponentPart {
    private Effect effect;
    private float probability;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        effect.write(out, protocolId);
        out.writeFloat(probability);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.effect = new Effect();
        effect.read(in, protocolId);
        this.probability = in.readFloat();
    }
}