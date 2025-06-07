package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.jukebox;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponentPart;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.SoundEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;
import fabian.hartman.MinecraftAFKBot.utils.nbt.NBTTag;

@Getter
@NoArgsConstructor
public class JukeboxSong implements DataComponentPart {
    private int musicDiscId;

    private SoundEvent soundEvent;
    private NBTTag description;
    private float lengthInSeconds;
    private int comparatorOutput;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(musicDiscId, out);
        if (musicDiscId == 0) {
            soundEvent.write(out, protocolId);
            Packet.writeNBT(description, out);
            out.writeFloat(lengthInSeconds);
            Packet.writeVarInt(comparatorOutput, out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.musicDiscId = Packet.readVarInt(in);
        if (musicDiscId == 0) {
            this.soundEvent = new SoundEvent();
            soundEvent.read(in, protocolId);
            this.description = Packet.readNBT(in, protocolId);
            this.lengthInSeconds = in.readFloat();
            this.comparatorOutput = Packet.readVarInt(in);
        }
    }
}