package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.auth.AuthData;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;
import fabian.hartman.MinecraftAFKBot.network.utils.CryptManager;

import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class PacketOutChatCommand extends Packet {

    private final String command;
    private List<CryptManager.SignableArgument> arguments;

    public PacketOutChatCommand(String command) {
        this.command = command;
    }

    public PacketOutChatCommand(String command, List<CryptManager.SignableArgument> arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        writeString(getCommand(), out);
        // this is most likely very illegal, but it seems like the server does not care about the signature
        // UPDATE: It's not working in a signed context (only no argument commands work)
        AuthData.ProfileKeys keys = MinecraftAFKBot.getInstance().getCurrentBot().getAuthData().getProfileKeys();

        if (arguments == null || arguments.isEmpty() || keys == null) {
            out.writeLong(System.currentTimeMillis());  // timestamp
            out.writeLong(System.currentTimeMillis());  // arg sig salt
            writeVarInt(0, out);                  // arg sig map
            if (protocolId < ProtocolConstants.MC_1_19_3) {
                out.writeBoolean(false);                 // signed preview
                if (protocolId >= ProtocolConstants.MC_1_19_1) {
                    writeVarInt(0, out);              // acknowledgements lastSeen (LastSeenMessageList.write(buf))
                    out.writeBoolean(false);             // acknowledgements lastReceived (Optional<LastSeenMessageList.Entry>)
                }
            } else {
                writeVarInt(0, out); // offset
                writeFixedBitSet(new BitSet(), 20, out);
            }
        } else {
            UUID signer = null;
            try {
                signer = UUID.fromString(MinecraftAFKBot.getInstance().getCurrentBot().getAuthData().getUuid());
            } catch (Exception ignore) {
            }

            CryptManager.ArgumentSignatures signatures = CryptManager.signCommandArguments(keys, signer, arguments);
            out.writeLong(signatures.getTimestamp().toEpochMilli());
            out.writeLong(signatures.getSalt());
            writeVarInt(signatures.getArgumentSignatures().size(), out);
            for (int i = 0; i < signatures.getArgumentSignatures().size(); i++) {
                CryptManager.ArgumentSignature signature = signatures.getArgumentSignatures().get(i);
                writeString(signature.getName(), out);
                if (protocolId < ProtocolConstants.MC_1_19_3)
                    writeVarInt(signature.getSignature().length, out);
                out.write(signature.getSignature());
                if (protocolId < ProtocolConstants.MC_1_19_3)
                    out.writeBoolean(false);
                if (protocolId >= ProtocolConstants.MC_1_19_1) {
                    writeVarInt(0, out);
                    if (protocolId < ProtocolConstants.MC_1_19_3)
                        out.writeBoolean(false);
                    else
                        writeFixedBitSet(new BitSet(), 20, out);
                }
            }
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        // Only outgoing packet
    }
}