package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class PacketInSetExperience extends Packet {
	private int experience;
	private int level;
	
	@Override
	public void write(ByteArrayDataOutput out, int protocolId) {
		// Only incoming packet
	}
	
	@Override
	public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
		in.readFloat();	//XP bar (useless)
		level = readVarInt(in);
		experience = readVarInt(in);
	}
}