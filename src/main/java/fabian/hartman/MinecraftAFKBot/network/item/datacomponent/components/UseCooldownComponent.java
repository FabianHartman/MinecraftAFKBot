package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.util.Optional;

public class UseCooldownComponent extends DataComponent {
    private float seconds;
    private Optional<String> cooldownGroup = Optional.empty();

    public UseCooldownComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeFloat(seconds);
        if (cooldownGroup.isPresent()) {
            out.writeBoolean(true);
            Packet.writeString(cooldownGroup.get(), out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.seconds = in.readFloat();
        if (in.readBoolean()) {
            this.cooldownGroup = Optional.of(Packet.readString(in));
        } else {
            this.cooldownGroup = Optional.empty();
        }
    }

    @Override
    public String toString(int protocolId) {
        return super.toString(protocolId) + "[seconds=" + seconds + (cooldownGroup.map(s -> ",cooldownGroup=" + s).orElse("")) + "]";
    }
}