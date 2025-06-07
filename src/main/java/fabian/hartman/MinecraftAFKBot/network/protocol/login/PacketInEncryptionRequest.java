package fabian.hartman.MinecraftAFKBot.network.protocol.login;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.login.EncryptionRequestEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;
import fabian.hartman.MinecraftAFKBot.network.utils.CryptManager;

import java.io.IOException;
import java.security.PublicKey;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PacketInEncryptionRequest extends Packet {
    private String serverId;
    private PublicKey publicKey;
    private byte[] verifyToken;
    private boolean shouldAuthenticate;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        this.serverId = readString(in);
        this.publicKey = CryptManager.decodePublicKey(readBytesFromStream(in));
        this.verifyToken = readBytesFromStream(in);

        if (protocolId >= ProtocolConstants.MC_1_20_5)
            this.shouldAuthenticate = in.readBoolean();

        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new EncryptionRequestEvent(serverId, publicKey, verifyToken, shouldAuthenticate));
    }
}