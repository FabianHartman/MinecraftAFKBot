package fabian.hartman.MinecraftAFKBot.network.protocol;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.network.protocol.common.PacketInDisconnect;
import fabian.hartman.MinecraftAFKBot.network.protocol.common.PacketInKeepAlive;
import fabian.hartman.MinecraftAFKBot.network.protocol.common.PacketInPing;
import fabian.hartman.MinecraftAFKBot.network.protocol.common.PacketInResourcePack;
import fabian.hartman.MinecraftAFKBot.network.protocol.common.PacketOutClientSettings;
import fabian.hartman.MinecraftAFKBot.network.protocol.common.PacketOutKeepAlive;
import fabian.hartman.MinecraftAFKBot.network.protocol.common.PacketOutPing;
import fabian.hartman.MinecraftAFKBot.network.protocol.common.PacketOutResourcePackResponse;
import fabian.hartman.MinecraftAFKBot.network.protocol.configuration.PacketInFinishConfiguration;
import fabian.hartman.MinecraftAFKBot.network.protocol.configuration.PacketInKnownPacks;
import fabian.hartman.MinecraftAFKBot.network.protocol.configuration.PacketOutFinishConfiguration;
import fabian.hartman.MinecraftAFKBot.network.protocol.configuration.PacketOutKnownPacks;
import fabian.hartman.MinecraftAFKBot.network.protocol.configuration.PacketOutPluginMessage;
import fabian.hartman.MinecraftAFKBot.network.protocol.handshake.PacketOutHandshake;
import fabian.hartman.MinecraftAFKBot.network.protocol.login.PacketInEncryptionRequest;
import fabian.hartman.MinecraftAFKBot.network.protocol.login.PacketInLoginDisconnect;
import fabian.hartman.MinecraftAFKBot.network.protocol.login.PacketInLoginPluginRequest;
import fabian.hartman.MinecraftAFKBot.network.protocol.login.PacketInLoginSuccess;
import fabian.hartman.MinecraftAFKBot.network.protocol.login.PacketInSetCompression;
import fabian.hartman.MinecraftAFKBot.network.protocol.login.PacketOutEncryptionResponse;
import fabian.hartman.MinecraftAFKBot.network.protocol.login.PacketOutLoginAcknowledge;
import fabian.hartman.MinecraftAFKBot.network.protocol.login.PacketOutLoginPluginResponse;
import fabian.hartman.MinecraftAFKBot.network.protocol.login.PacketOutLoginStart;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketInChatPlayer;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketInChatSystem;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketInCommands;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketInConfirmTransaction;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketInEntityTeleport;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketInEntityVelocity;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketInJoinGame;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketInSetCompressionLegacy;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketInStartConfiguration;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketInUpdateHealth;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutAcknowledgeConfiguration;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutArmAnimation;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutChatCommand;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutChatMessage;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutChatSessionUpdate;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutChunkBatchReceived;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutClientStatus;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutConfirmTransaction;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutEntityAction;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutPlayerLoaded;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutPosition;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutTeleportConfirm;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutUnsignedChatCommand;
import fabian.hartman.MinecraftAFKBot.network.utils.InvalidPacketException;

import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Getter
public class PacketRegistry {
    private final int protocolId;
    private final ProtocolState state;
    private final ProtocolFlow flow;
    private final JsonParser parser = new JsonParser();

    private final BiMap<Integer, Class<? extends Packet>> registeredPackets = HashBiMap.create();
    private final Map<Integer, String> idToMojMapName = new HashMap<>();

    public PacketRegistry(int protocolId, ProtocolState state, ProtocolFlow flow) {
        this.protocolId = protocolId;
        this.state = state;
        this.flow = flow;
        JsonObject data = loadBundledPacketRegistry(protocolId);
        if (data == null) {
            protocolId = ProtocolConstants.getLatest();
            data = loadBundledPacketRegistry(protocolId);
        }
        if (data == null) throw new IllegalArgumentException("Could not load bundled packets for " + ProtocolConstants.getVersionString(protocolId));
        JsonObject stateObj = data.getAsJsonObject(state.getId());
        if (stateObj == null) throw new IllegalArgumentException("Could not load bundled packets for " + state.getId() + "/" + ProtocolConstants.getVersionString(protocolId));
        JsonObject flowObj = stateObj.getAsJsonObject(flow.getId());
        if (flowObj == null) throw new IllegalArgumentException("Could not load bundled packets for " + state.getId() + "/" + flow.getId() + "/" + ProtocolConstants.getVersionString(protocolId));
        for (String packetId : flowObj.keySet()) {
            JsonObject packetData = flowObj.getAsJsonObject(packetId);
            int packetProtocolId = packetData.getAsJsonPrimitive("protocol_id").getAsInt();

            idToMojMapName.put(packetProtocolId, packetId);

            Class<? extends Packet> packetClazz = mapMojangPacketId(packetId);
            if (packetClazz == null) {
                if (MinecraftAFKBot.getInstance().getCurrentBot().getConfig().isLogPackets())
                    MinecraftAFKBot.getLog().warning("Could not map packet id " + packetId + " in " + state.name() + " (" + flow.name() + ")");
                continue;
            }

            registerPacket(packetProtocolId, packetClazz);
        }
    }

    private JsonObject loadBundledPacketRegistry(int protocolId) {
        String file = getRegistryFileName(protocolId);
        if (file == null) return null;
        try {
            return parser.parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(file))).getAsJsonObject();
        } catch (Throwable ex) {
            return null;
        }
    }

    private Class<? extends Packet> mapMojangPacketId(String mojangPacketId) {
        if (state == ProtocolState.HANDSHAKE && flow == ProtocolFlow.OUTGOING_PACKET) {
            if (mojangPacketId.equals("minecraft:intention"))
                return PacketOutHandshake.class;
        } else if (state == ProtocolState.LOGIN) {
            if (flow == ProtocolFlow.INCOMING_PACKET) {
                switch (mojangPacketId) {
                    case "minecraft:login_disconnect": return PacketInLoginDisconnect.class;
                    case "minecraft:hello": return PacketInEncryptionRequest.class;
                    case "minecraft:login_finished":
                    case "minecraft:game_profile": return PacketInLoginSuccess.class;
                    case "minecraft:login_compression": return PacketInSetCompression.class;
                    case "minecraft:custom_query": return PacketInLoginPluginRequest.class;
                }
            } else if (flow == ProtocolFlow.OUTGOING_PACKET) {
                switch (mojangPacketId) {
                    case "minecraft:hello": return PacketOutLoginStart.class;
                    case "minecraft:key": return PacketOutEncryptionResponse.class;
                    case "minecraft:custom_query_answer": return PacketOutLoginPluginResponse.class;
                    case "minecraft:login_acknowledged": return PacketOutLoginAcknowledge.class;
                }
            }
        } else if (state == ProtocolState.CONFIGURATION) {
            if (flow == ProtocolFlow.INCOMING_PACKET) {
                switch (mojangPacketId) {
                    case "minecraft:finish_configuration": return PacketInFinishConfiguration.class;
                    case "minecraft:keep_alive": return PacketInKeepAlive.class;
                    case "minecraft:ping": return PacketInPing.class;
                    case "minecraft:resource_pack_push": return PacketInResourcePack.class;
                    case "minecraft:select_known_packs": return PacketInKnownPacks.class;
                    case "minecraft:disconnect": return PacketInDisconnect.class;
                }
            } else if (flow == ProtocolFlow.OUTGOING_PACKET) {
                switch (mojangPacketId) {
                    case "minecraft:custom_payload": return PacketOutPluginMessage.class;
                    case "minecraft:finish_configuration": return PacketOutFinishConfiguration.class;
                    case "minecraft:keep_alive": return PacketOutKeepAlive.class;
                    case "minecraft:pong": return PacketOutPing.class;
                    case "minecraft:resource_pack": return PacketOutResourcePackResponse.class;
                    case "minecraft:select_known_packs": return PacketOutKnownPacks.class;
                }
            }
        } else if (state == ProtocolState.PLAY) {
            if (flow == ProtocolFlow.INCOMING_PACKET) {
                switch (mojangPacketId) {
                    case "minecraft:commands": return PacketInCommands.class;
                    case "minecraft:disconnect": return PacketInDisconnect.class;
                    case "minecraft:keep_alive": return PacketInKeepAlive.class;
                    case "minecraft:login": return PacketInJoinGame.class;
                    case "minecraft:player_chat": return PacketInChatPlayer.class;
                    case "minecraft:resource_pack_push": return PacketInResourcePack.class;
                    case "minecraft:set_held_slot":
                    case "minecraft:set_entity_motion": return PacketInEntityVelocity.class;
                    case "minecraft:set_health": return PacketInUpdateHealth.class;
                    case "minecraft:start_configuration": return PacketInStartConfiguration.class;
                    case "minecraft:system_chat": return PacketInChatSystem.class;
                    case "minecraft:teleport_entity": return PacketInEntityTeleport.class;
                    case "minecraft:confirm_transaction": return PacketInConfirmTransaction.class;
                    case "minecraft:play_compression": return PacketInSetCompressionLegacy.class;
                    case "minecraft:ping": return PacketInPing.class;
                }
            } else if (flow == ProtocolFlow.OUTGOING_PACKET) {
                switch (mojangPacketId) {
                    case "minecraft:accept_teleportation": return PacketOutTeleportConfirm.class;
                    case "minecraft:chat_command": return PacketOutUnsignedChatCommand.class;
                    case "minecraft:chat_command_signed": return PacketOutChatCommand.class;
                    case "minecraft:chat": return PacketOutChatMessage.class;
                    case "minecraft:chat_session_update": return PacketOutChatSessionUpdate.class;
                    case "minecraft:client_command": return PacketOutClientStatus.class;
                    case "minecraft:client_information": return PacketOutClientSettings.class;
                    case "minecraft:configuration_acknowledged": return PacketOutAcknowledgeConfiguration.class;
                    case "minecraft:custom_payload": return PacketOutPluginMessage.class;
                    case "minecraft:keep_alive": return PacketOutKeepAlive.class;
                    case "minecraft:move_player_pos": return PacketOutPosition.class;
                    case "minecraft:player_command": return PacketOutEntityAction.class;
                    case "minecraft:resource_pack": return PacketOutResourcePackResponse.class;
                    case "minecraft:swing": return PacketOutArmAnimation.class;
                    case "minecraft:confirm_transaction": return PacketOutConfirmTransaction.class;
                    case "minecraft:pong": return PacketOutPing.class;
                    case "minecraft:chunk_batch_received": return PacketOutChunkBatchReceived.class;
                    case "minecraft:player_loaded": return PacketOutPlayerLoaded.class;
                }
            }
        }
        return null;
    }

    public String getRegistryFileName(int protocolId) {
        if (protocolId == ProtocolConstants.AUTOMATIC)
            protocolId = ProtocolConstants.getLatest();
        String version = ProtocolConstants.getVersionString(protocolId);
        if (version.contains("/"))
            version = version.split("/")[0];
        if (version.contains("-"))
            version = version.split("-")[0];
        version = version.replace(".", "_").trim();
        return "mc_data/" + version + "/packets.json";
    }

    private void registerPacket(int id, Class<? extends Packet> clazz) {
        if (clazz == null) {
            MinecraftAFKBot.getLog().severe("Tried to register null packet for: " + id);
            return;
        }
        if (registeredPackets.containsKey(id)) {
            MinecraftAFKBot.getLog().severe("Tried to register packet twice: " + id + " is registered as " + registeredPackets.get(id).getSimpleName() + " wants to register as " + clazz.getSimpleName());
            return;
        }
        if (registeredPackets.containsValue(clazz)) {
            MinecraftAFKBot.getLog().severe("Tried to register packet twice: " + id + " is registered as another packet with same class wants to register as " + clazz.getSimpleName());
            return;
        }
        registeredPackets.put(id, clazz);
    }

    public String getMojMapPacketName(int id) {
        return idToMojMapName.get(id);
    }

    public Class<? extends Packet> getPacket(int id) {
        return registeredPackets.get(id);
    }

    public int getId(Class<? extends Packet> clazz) throws InvalidPacketException {
        Integer id = registeredPackets.inverse().get(clazz);
        if (id == null) {
            System.out.println(MessageFormat.format("The packet id for {0} in {1} is not set",clazz.getSimpleName() + " (for " + state.name() +  ")", ProtocolConstants.getVersionString(MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol())));
            MinecraftAFKBot.getInstance().getCurrentBot().setRunning(false);
            MinecraftAFKBot.getInstance().getCurrentBot().setWontConnect(true);
            throw new InvalidPacketException("Packet not registered: " + clazz.getSimpleName());
        }
        return id;
    }
}