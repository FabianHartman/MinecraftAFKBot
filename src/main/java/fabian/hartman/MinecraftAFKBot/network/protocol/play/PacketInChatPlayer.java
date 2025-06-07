package fabian.hartman.MinecraftAFKBot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.play.ChatEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;
import fabian.hartman.MinecraftAFKBot.utils.ChatComponentUtils;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class PacketInChatPlayer extends Packet {
    private String text;
    private UUID sender;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        if (protocolId < ProtocolConstants.MC_1_19) {
            this.text = readChatComponent(in, protocolId);
            if (text != null)
                MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new ChatEvent(getText(), getSender()));
        } else {
            try {
                if (protocolId >= ProtocolConstants.MC_1_21_5) {
                    readVarInt(in); // global index
                }
                if (protocolId >= ProtocolConstants.MC_1_19_3) {
                    this.sender = readUUID(in); // sender
                    readVarInt(in); // index
                }
                if (in.readBoolean()) {
                    if (protocolId >= ProtocolConstants.MC_1_19_3) {
                        in.skipBytes(256);
                    } else {
                        int sigLength = readVarInt(in);
                        in.skipBytes(sigLength);
                    }
                }
                if (protocolId <= ProtocolConstants.MC_1_19_1) {
                    this.sender = readUUID(in);
                    int sigLength = readVarInt(in);
                    in.skipBytes(sigLength);
                }
                String actualMessage = readString(in); //plain
                if (protocolId <= ProtocolConstants.MC_1_19_1 && in.readBoolean())
                    readString(in);
                in.readLong();
                in.readLong();
                int prevMsgs = readVarInt(in);
                for (int i = 0; i < prevMsgs; i++) {
                    if (protocolId <= ProtocolConstants.MC_1_19_1) {
                        this.sender = readUUID(in);
                        int prevMsgSig = readVarInt(in);
                        in.skipBytes(prevMsgSig);
                    } else {
                        int index = readVarInt(in);
                        if (index == 0)
                            in.skipBytes(256);
                    }
                }
                // unsigned content
                if (in.readBoolean())
                    readChatComponent(in, protocolId);
                int filterMask = readVarInt(in);
                if (filterMask == 2) {
                    int bitSetLength = readVarInt(in);
                    for (int i = 0; i < bitSetLength; i++)
                        in.readLong();
                }
                int chatType = readVarInt(in); // chat type
                String userName = readChatComponent(in, protocolId);
                String targetName = "";
                // target name
                if (in.readBoolean())
                    targetName = readChatComponent(in, protocolId);
                this.text = ChatComponentUtils.sillyTransformWithChatType(protocolId, chatType, userName, targetName, actualMessage);

                MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new ChatEvent(getText(), getSender()));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}