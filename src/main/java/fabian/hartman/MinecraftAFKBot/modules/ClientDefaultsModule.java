package fabian.hartman.MinecraftAFKBot.modules;

import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.auth.AuthData;
import fabian.hartman.MinecraftAFKBot.event.EventHandler;
import fabian.hartman.MinecraftAFKBot.event.Listener;
import fabian.hartman.MinecraftAFKBot.event.common.KeepAliveEvent;
import fabian.hartman.MinecraftAFKBot.event.common.PingPacketEvent;
import fabian.hartman.MinecraftAFKBot.event.configuration.ConfigurationStartEvent;
import fabian.hartman.MinecraftAFKBot.event.play.ConfirmTransactionEvent;
import fabian.hartman.MinecraftAFKBot.event.play.DisconnectEvent;
import fabian.hartman.MinecraftAFKBot.event.play.JoinGameEvent;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.ConsoleCommandExecutor;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.protocol.common.PacketOutClientSettings;
import fabian.hartman.MinecraftAFKBot.network.protocol.common.PacketOutKeepAlive;
import fabian.hartman.MinecraftAFKBot.network.protocol.common.PacketOutPing;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutChatSessionUpdate;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutConfirmTransaction;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutPlayerLoaded;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class ClientDefaultsModule extends Module implements Listener {

    private Thread positionThread;
    private boolean joined;
    private final Set<UUID> onlinePlayers = new HashSet<>();

    @Override
    public void onEnable() {
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
    }

    @Override
    public void onDisable() {
        if (getPositionThread() != null)
            getPositionThread().interrupt();
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().unregisterListener(this);
    }

    @EventHandler
    public void onDisconnect(DisconnectEvent event) {
        System.out.println(MessageFormat.format("Disconnected: {0}", event.getDisconnectMessage()));
        MinecraftAFKBot.getInstance().getCurrentBot().setRunning(false);
        onlinePlayers.clear();
    }

    @EventHandler
    public void onJoinGame(JoinGameEvent event) {
        onlinePlayers.clear();
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutClientSettings());
        if (MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MC_1_21_4)
            MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutPlayerLoaded());

        if (MinecraftAFKBot.getInstance().getCurrentBot().getNet().isEncrypted()
                && MinecraftAFKBot.getInstance().getCurrentBot().getAuthData().getProfileKeys() != null
                && MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol() > ProtocolConstants.MC_1_19_1) {
            AuthData.ProfileKeys keys = MinecraftAFKBot.getInstance().getCurrentBot().getAuthData().getProfileKeys();
            MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatSessionUpdate(keys));
        }
        if (isJoined())
            return;
        this.joined = true;
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ignore) { }

            // Send start texts
            if (MinecraftAFKBot.getInstance().getCurrentBot().getConfig().isStartTextEnabled()) {
                MinecraftAFKBot.getInstance().getCurrentBot().getConfig().getStartText().forEach(s -> {
                    MinecraftAFKBot.getInstance().getCurrentBot().runCommand(s, true, new ConsoleCommandExecutor());
                });
            }

            // Start position updates
            startPositionUpdate(MinecraftAFKBot.getInstance().getCurrentBot().getNet());
        }).start();
    }

    @EventHandler
    public void onKeepAlive(KeepAliveEvent event) {
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutKeepAlive(event.getId()));
    }

    @EventHandler
    public void onConfirmTransaction(ConfirmTransactionEvent event) {
        if (!event.isAccepted())
            MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutConfirmTransaction(event.getWindowId(), event.getAction(), true));
    }

    private void startPositionUpdate(NetworkHandler networkHandler) {
        if (positionThread != null)
            positionThread.interrupt();
        positionThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try { Thread.sleep(1000); } catch (InterruptedException e) { break; }
            }
        });
        positionThread.setName("positionThread");
        positionThread.start();
    }

    @EventHandler
    public void onConfigurationStart(ConfigurationStartEvent e) {
        if (positionThread != null)
            positionThread.interrupt();
    }

    @EventHandler
    public void onPing(PingPacketEvent e) {
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutPing(e.getId()));
    }
}