package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.SoundEvent;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.consumeeffect.ConsumeEffect;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConsumableComponent extends DataComponent {
    private float consumeSeconds;
    private int animation;
    private SoundEvent sound;
    private boolean consumeParticles;
    private List<ConsumeEffect> consumeEffects = Collections.emptyList();

    public ConsumableComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeFloat(consumeSeconds);
        Packet.writeVarInt(animation, out);
        sound.write(out, protocolId);
        out.writeBoolean(consumeParticles);
        Packet.writeVarInt(consumeEffects.size(), out);
        for (ConsumeEffect effect : consumeEffects) {
            effect.write(out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.consumeSeconds = in.readFloat();
        this.animation = Packet.readVarInt(in);
        this.sound = new SoundEvent();
        sound.read(in, protocolId);
        this.consumeParticles = in.readBoolean();
        int count = Packet.readVarInt(in);
        this.consumeEffects = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ConsumeEffect consumeEffect = new ConsumeEffect();
            consumeEffect.read(in, protocolId);
            consumeEffects.add(consumeEffect);
        }
    }

    @Override
    public String toString(int protocolId) {
        return super.toString(protocolId) + "[consume_seconds=" + consumeSeconds + ",animation=" + animation + ",sound=" + sound.toString(protocolId) + ",consumeParticles=" + consumeParticles + ",consumeEffects=[" + consumeEffects.stream().map(consumeEffect -> consumeEffect.toString(protocolId)).collect(Collectors.joining(",")) + "]]";
    }
}