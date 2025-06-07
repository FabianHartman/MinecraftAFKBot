package fabian.hartman.MinecraftAFKBot.network.protocol.login;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.primitives.Longs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.auth.AuthData;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;
import fabian.hartman.MinecraftAFKBot.network.utils.CryptManager;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Random;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PacketOutEncryptionResponse extends Packet {
    private String serverId;
    private PublicKey publicKey;
    private byte[] verifyToken;
    private SecretKey secretKey;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        byte[] sharedSecret = CryptManager.encryptData(getPublicKey(), getSecretKey().getEncoded());
        byte[] verifyToken = CryptManager.encryptData(getPublicKey(), getVerifyToken());
        writeVarInt(sharedSecret.length, out);
        out.write(sharedSecret);
        if (protocolId < ProtocolConstants.MC_1_19 || protocolId > ProtocolConstants.MC_1_19_1) {
            writeVarInt(verifyToken.length, out);
            out.write(verifyToken);
        } else {
            AuthData.ProfileKeys keys = MinecraftAFKBot.getInstance().getCurrentBot().getAuthData().getProfileKeys();
            out.writeBoolean(keys == null);
            if (keys == null) {
                writeVarInt(verifyToken.length, out);
                out.write(verifyToken);
            } else {
                long salt = new Random().nextLong();
                byte[] signed = CryptManager.sign(keys, signature -> {
                    try {
                        signature.update(getVerifyToken());
                        signature.update(Longs.toByteArray(salt));
                    } catch (SignatureException e) {
                        e.printStackTrace();
                    }
                });
                out.writeLong(salt);
                writeVarInt(signed.length, out);
                out.write(signed);
            }
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException { }
}