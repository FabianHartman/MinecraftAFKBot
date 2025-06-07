package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.auth.AuthData;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;
import fabian.hartman.MinecraftAFKBot.network.utils.CryptManager;

import java.util.BitSet;
import java.util.Optional;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PacketOutChatMessage extends Packet {
    private String message;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        writeString(getMessage(), out);
        if (protocolId >= ProtocolConstants.MC_1_19 && protocolId <= ProtocolConstants.MC_1_19_1) {
            AuthData.ProfileKeys keys = MinecraftAFKBot.getInstance().getCurrentBot().getAuthData().getProfileKeys();
            UUID signer = null;
            try {
                signer = UUID.fromString(MinecraftAFKBot.getInstance().getCurrentBot().getAuthData().getUuid());
            } catch (Exception ignore) {}

            if (keys == null || signer == null) {
                out.writeLong(System.currentTimeMillis());  // timestamp
                // this is most likely very illegal, but it seems like the server does not care about the signature
                out.writeLong(System.currentTimeMillis());  // sig pair long
                writeVarInt(1, out);                  // sig pair bytearray
                out.write(new byte[]{1});                   // sig pair bytearray
                out.writeBoolean(false);                 // signed preview
            } else {
                CryptManager.MessageSignature signature = CryptManager.signChatMessage(keys, signer, message);
                out.writeLong(signature.getTimestamp().toEpochMilli());
                out.writeLong(signature.getSalt());
                writeVarInt(signature.getSignature().length, out);
                out.write(signature.getSignature());
                out.writeBoolean(false);
            }

            if (protocolId >= ProtocolConstants.MC_1_19_1) {
                writeVarInt(0, out);
                out.writeBoolean(false);
            }
        } else if (protocolId >= ProtocolConstants.MC_1_19_3) {
            AuthData.ProfileKeys keys = MinecraftAFKBot.getInstance().getCurrentBot().getAuthData().getProfileKeys();
            UUID signer = null;
            try {
                signer = UUID.fromString(MinecraftAFKBot.getInstance().getCurrentBot().getAuthData().getUuid());
            } catch (Exception ignore) {}


            if (keys == null || signer == null) {
                out.writeLong(System.currentTimeMillis());  // timestamp
                // this is most likely very illegal, but it seems like the server does not care about the signature
                out.writeLong(System.currentTimeMillis());  // sig pair long
                out.writeBoolean(false);                 // no sig present
                writeVarInt(0, out);                  // lastSeen sigs offset?
                writeFixedBitSet(new BitSet(), 20, out);
                if (protocolId >= ProtocolConstants.MC_1_21_5)
                    out.writeByte(0);                        // checksum, always 0 should be ok
            } else {
                CryptManager.MessageSignature signature = CryptManager.signChatMessage(keys, signer, message);
                out.writeLong(signature.getTimestamp().toEpochMilli());
                out.writeLong(signature.getSalt());
                out.writeBoolean(true);
                out.write(signature.getSignature());
                writeVarInt(0, out);
                writeFixedBitSet(new BitSet(), 20, out);
                if (protocolId >= ProtocolConstants.MC_1_21_5) {
                    out.writeByte(0);
                }
            }
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        // Only outgoing packet
    }
}