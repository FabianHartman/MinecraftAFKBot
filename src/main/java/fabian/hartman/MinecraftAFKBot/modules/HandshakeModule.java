package fabian.hartman.MinecraftAFKBot.modules;

import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolState;
import fabian.hartman.MinecraftAFKBot.network.protocol.handshake.PacketOutHandshake;

@Getter
public class HandshakeModule extends Module {

    private final String serverName;
    private final int serverPort;

    public HandshakeModule(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    @Override
    public void onEnable() {
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutHandshake(serverName, serverPort));
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().setState(ProtocolState.LOGIN);
    }

    @Override
    public void onDisable() {
        System.out.println("Cannot disable the HandshakeModule.");
    }
}