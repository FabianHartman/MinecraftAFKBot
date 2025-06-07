package fabian.hartman.MinecraftAFKBot.network.protocol.login;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.auth.AuthData;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class PacketOutLoginStart extends Packet {
    private String userName;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        writeString(userName, out);
        if (protocolId >= ProtocolConstants.MC_1_19) {
            if (protocolId <= ProtocolConstants.MC_1_19_1) {
                AuthData.ProfileKeys keys = MinecraftAFKBot.getInstance().getCurrentBot().getAuthData().getProfileKeys();
                out.writeBoolean(keys != null);
                if (keys != null) {
                    out.writeLong(keys.getExpiresAt());
                    byte[] pubKey = keys.getPublicKey().getEncoded();
                    writeVarInt(pubKey.length, out);
                    out.write(pubKey);
                    byte[] signature = ByteBuffer.wrap(Base64.getDecoder().decode(keys.getPublicKeySignature())).array();
                    writeVarInt(signature.length, out);
                    out.write(signature);
                }
            }
            if (protocolId >= ProtocolConstants.MC_1_19_1) {
                UUID uuid = null;
                try {
                    uuid = UUID.fromString(MinecraftAFKBot.getInstance().getCurrentBot().getAuthData().getUuid());
                } catch (Exception ignore) {
                }
                if (protocolId < ProtocolConstants.MC_1_20_2)
                    out.writeBoolean(uuid != null);
                if (uuid != null) {
                    writeUUID(uuid, out);
                }
            }
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) { }
}