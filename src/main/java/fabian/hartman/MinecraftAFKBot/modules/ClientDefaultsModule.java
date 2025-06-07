package fabian.hartman.MinecraftAFKBot.modules;

import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.auth.AuthData;
import fabian.hartman.MinecraftAFKBot.bot.Player;
import fabian.hartman.MinecraftAFKBot.bot.registry.MetaRegistry;
import fabian.hartman.MinecraftAFKBot.bot.registry.Registries;
import fabian.hartman.MinecraftAFKBot.bot.registry.RegistryLoader;
import fabian.hartman.MinecraftAFKBot.event.EventHandler;
import fabian.hartman.MinecraftAFKBot.event.Listener;
import fabian.hartman.MinecraftAFKBot.event.common.KeepAliveEvent;
import fabian.hartman.MinecraftAFKBot.event.common.PingPacketEvent;
import fabian.hartman.MinecraftAFKBot.event.common.ResourcePackEvent;
import fabian.hartman.MinecraftAFKBot.event.configuration.ConfigurationStartEvent;
import fabian.hartman.MinecraftAFKBot.event.configuration.RegistryDataEvent;
import fabian.hartman.MinecraftAFKBot.event.play.ChunkBatchFinishedEvent;
import fabian.hartman.MinecraftAFKBot.event.play.ConfirmTransactionEvent;
import fabian.hartman.MinecraftAFKBot.event.play.DisconnectEvent;
import fabian.hartman.MinecraftAFKBot.event.play.EntityDataEvent;
import fabian.hartman.MinecraftAFKBot.event.play.JoinGameEvent;
import fabian.hartman.MinecraftAFKBot.event.play.OpenWindowEvent;
import fabian.hartman.MinecraftAFKBot.event.play.UpdateHealthEvent;
import fabian.hartman.MinecraftAFKBot.event.play.UpdatePlayerListEvent;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.ConsoleCommandExecutor;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolState;
import fabian.hartman.MinecraftAFKBot.network.protocol.common.PacketOutClientSettings;
import fabian.hartman.MinecraftAFKBot.network.protocol.common.PacketOutKeepAlive;
import fabian.hartman.MinecraftAFKBot.network.protocol.common.PacketOutPing;
import fabian.hartman.MinecraftAFKBot.network.protocol.common.PacketOutResourcePackResponse;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutChatSessionUpdate;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutChunkBatchReceived;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutConfirmTransaction;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutPlayerLoaded;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutPosLook;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class ClientDefaultsModule extends Module implements Listener {

    private Thread positionThread;
    private boolean joined;
    private Set<UUID> onlinePlayers = new HashSet<>();

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
    public void onResourcePack(ResourcePackEvent event) {
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutResourcePackResponse(event.getUuid(), PacketOutResourcePackResponse.Result.ACCEPTED));
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutResourcePackResponse(event.getUuid(), PacketOutResourcePackResponse.Result.SUCCESSFULLY_LOADED));
    }

    @EventHandler
    public void onUpdatePlayerList(UpdatePlayerListEvent event) {
        switch (event.getAction()) {
            case REPLACE: {
                onlinePlayers = event.getPlayers();
                break;
            }
            case ADD: {
                onlinePlayers.addAll(event.getPlayers());
                break;
            }
            case REMOVE: {
                onlinePlayers.removeAll(event.getPlayers());
                break;
            }
        }
        if (MinecraftAFKBot.getInstance().getCurrentBot().getConfig().isAutoDisconnect() && onlinePlayers.size() > MinecraftAFKBot.getInstance().getCurrentBot().getConfig().getAutoDisconnectPlayersThreshold()) {
            System.out.println("The server is full. Bot will be stopped!");
            MinecraftAFKBot.getInstance().getCurrentBot().setWontConnect(true);
            MinecraftAFKBot.getInstance().getCurrentBot().setRunning(false);
        }
    }

    @EventHandler
    public void onConfirmTransaction(ConfirmTransactionEvent event) {
        if (!event.isAccepted())
            MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutConfirmTransaction(event.getWindowId(), event.getAction(), true));
    }

    @EventHandler
    public void onOpenWindow(OpenWindowEvent e) {
        System.out.println(MessageFormat.format("New inventory opened: {0}", e.getTitle()));
    }

    private void startPositionUpdate(NetworkHandler networkHandler) {
        if (positionThread != null)
            positionThread.interrupt();
        positionThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Player player = MinecraftAFKBot.getInstance().getCurrentBot().getPlayer();
                if (networkHandler != null && networkHandler.getState() == ProtocolState.PLAY)
                    networkHandler.sendPacket(new PacketOutPosLook(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), true, true));
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

    @EventHandler
    public void onEntityData(EntityDataEvent event) {
        event.getData().stream()
                .filter(element -> element.getElement().getInternalId().equals("float"))
                .forEach(element -> {
                    int protocolId = MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol();
                    if ((protocolId < ProtocolConstants.MC_1_10 && element.getElementIndex() == 6)
                            || (protocolId < ProtocolConstants.MC_1_14_4 && element.getElementIndex() == 7)
                            || (protocolId < ProtocolConstants.MC_1_17 && element.getElementIndex() == 8)
                            || (protocolId >= ProtocolConstants.MC_1_17 && element.getElementIndex() == 9)) {
                        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdateHealthEvent(event.getEntityId(), (float) element.getElement().getValue(), -1 ,-1));
                    }
                });
    }

    @EventHandler
    public void onRegistryData(RegistryDataEvent event) {
        MetaRegistry<Integer, String> metaRegistry = Registries.getByIdentifier(event.getRegistryId(), MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol());
        if (metaRegistry == null) return;
        metaRegistry.load(event.getRegistryData(), RegistryLoader.mapped(), MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol());
    }

    @EventHandler
    public void onChunkBatchFinished(ChunkBatchFinishedEvent event) {
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChunkBatchReceived(20));
    }
}