package fabian.hartman.MinecraftAFKBot.network.protocol;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;
import fabian.hartman.MinecraftAFKBot.network.utils.CryptManager;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.PublicKey;
import java.text.MessageFormat;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

@Getter
public class NetworkHandler {
    private DataOutputStream out;
    private DataInputStream in;

    @Setter private ProtocolState state;
    private PacketRegistry handshakeRegistry;
    private PacketRegistry loginRegistryIn;
    private PacketRegistry loginRegistryOut;
    private PacketRegistry configurationRegistryIn;
    private PacketRegistry configurationRegistryOut;
    private PacketRegistry playRegistryIn;
    private PacketRegistry playRegistryOut;

    @Setter private int threshold = -1;
    @Setter private PublicKey publicKey;
    @Setter private SecretKey secretKey;
    @Setter private boolean outputEncrypted;

    public NetworkHandler() {
        try {
            this.out = new DataOutputStream(MinecraftAFKBot.getInstance().getCurrentBot().getSocket().getOutputStream());
            this.in = new DataInputStream(MinecraftAFKBot.getInstance().getCurrentBot().getSocket().getInputStream());

            this.state = ProtocolState.HANDSHAKE;
            initPacketRegistries();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(MessageFormat.format("An unexpected error occured: {0}\\n\\\n" +
                    "The bot will be stopped...", e.getMessage()));
        }
    }

    private void initPacketRegistries() {
        int protocolId = MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol();

        this.handshakeRegistry = new PacketRegistry(protocolId, ProtocolState.HANDSHAKE, ProtocolFlow.OUTGOING_PACKET);
        this.loginRegistryIn = new PacketRegistry(protocolId, ProtocolState.LOGIN, ProtocolFlow.INCOMING_PACKET);
        this.loginRegistryOut = new PacketRegistry(protocolId, ProtocolState.LOGIN, ProtocolFlow.OUTGOING_PACKET);
        this.configurationRegistryIn = new PacketRegistry(protocolId, ProtocolState.CONFIGURATION, ProtocolFlow.INCOMING_PACKET);
        this.configurationRegistryOut = new PacketRegistry(protocolId, ProtocolState.CONFIGURATION, ProtocolFlow.OUTGOING_PACKET);
        this.playRegistryIn = new PacketRegistry(protocolId, ProtocolState.PLAY, ProtocolFlow.INCOMING_PACKET);
        this.playRegistryOut = new PacketRegistry(protocolId, ProtocolState.PLAY, ProtocolFlow.OUTGOING_PACKET);
    }

    public void sendPacket(Packet packet) {
        ByteArrayDataOutput buf = ByteStreams.newDataOutput();

        // Add Packet ID from serverProtocol-specific PacketRegistry
        switch (getState()) {
            case HANDSHAKE:
                Packet.writeVarInt(getHandshakeRegistry().getId(packet.getClass()), buf);
                break;
            case LOGIN:
                Packet.writeVarInt(getLoginRegistryOut().getId(packet.getClass()), buf);
                break;
            case PLAY:
                Packet.writeVarInt(getPlayRegistryOut().getId(packet.getClass()), buf);
                break;
            case CONFIGURATION:
                Packet.writeVarInt(getConfigurationRegistryOut().getId(packet.getClass()), buf);
                break;
            default:
                return;
        }

        // Add packet payload
        try {
            packet.write(buf, MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol());
        } catch (IOException e) {
            MinecraftAFKBot.getLog().warning("Could not instantiate " + packet.getClass().getSimpleName());
        }

        if (getThreshold() >= 0) {
            // Send packet (with 0 threshold, no compression)
            ByteArrayDataOutput send1 = ByteStreams.newDataOutput();
            Packet.writeVarInt(0, send1);
            send1.write(buf.toByteArray());
            ByteArrayDataOutput send2 = ByteStreams.newDataOutput();
            Packet.writeVarInt(send1.toByteArray().length, send2);
            send2.write(send1.toByteArray());
            try {
                out.write(send2.toByteArray());
                out.flush();
            } catch (IOException e) {
                MinecraftAFKBot.getLog().severe("Error while trying to send: " + packet.getClass().getSimpleName());
            }
        } else {
            // Send packet (without threshold)
            ByteArrayDataOutput send = ByteStreams.newDataOutput();
            Packet.writeVarInt(buf.toByteArray().length, send);
            send.write(buf.toByteArray());
            try {
                out.write(send.toByteArray());
                out.flush();
            } catch (IOException e) {
                MinecraftAFKBot.getLog().severe("Error while trying to send: " + packet.getClass().getSimpleName());
                e.printStackTrace();
            }
        }
        if (MinecraftAFKBot.getInstance().getCurrentBot().getConfig().isLogPackets())
            MinecraftAFKBot.getLog().info("[" + getState().name().toUpperCase() + "]  C  >>> |S|: " + packet.getClass().getSimpleName());
    }

    public void readData() throws IOException {
        if (getThreshold() >= 0) {
            int plen1 = Packet.readVarInt(in);
            int[] dlens = Packet.readVarIntt(in);
            int dlen = dlens[0];
            int plen = plen1 - dlens[1];
            if (dlen == 0) {
                readUncompressed(plen);
            } else {
                readCompressed(plen, dlen);
            }
        } else {
            readUncompressed();
        }

    }

    private void readUncompressed() throws IOException {
        int len1 = Packet.readVarInt(in);
        int[] types = Packet.readVarIntt(in);
        int type = types[0];
        int len = len1 - types[1];
        byte[] data = new byte[len];
        in.readFully(data, 0, len);
        readPacket(len, type, new ByteArrayDataInputWrapper(data));
    }

    private void readUncompressed(int len) throws IOException {
        byte[] data = new byte[len];
        in.readFully(data, 0, len);
        ByteArrayDataInputWrapper bf = new ByteArrayDataInputWrapper(data);
        int type = Packet.readVarInt(bf);
        readPacket(len, type, bf);
    }

    private void readCompressed(int plen, int dlen) throws IOException {
        if (dlen >= getThreshold()) {
            byte[] data = new byte[plen];
            in.readFully(data, 0, plen);
            Inflater inflater = new Inflater();
            inflater.setInput(data);
            byte[] uncompressed = new byte[dlen];
            try {
                inflater.inflate(uncompressed);
            } catch (DataFormatException dataformatexception) {
                dataformatexception.printStackTrace();
                throw new IOException("Bad compressed data format");
            } finally {
                inflater.end();
            }
            ByteArrayDataInputWrapper buf = new ByteArrayDataInputWrapper(uncompressed);
            int type = Packet.readVarInt(buf);
            readPacket(dlen, type, buf);
        } else {
            throw new IOException("Data was smaller than threshold!");
        }
    }

    private PacketRegistry getCurrentPacketRegistry() {
        switch (state) {
            case HANDSHAKE: return getHandshakeRegistry();
            case LOGIN: return getLoginRegistryIn();
            case PLAY: return getPlayRegistryIn();
            case CONFIGURATION: return getConfigurationRegistryIn();
        }
        return null;
    }

    private void readPacket(int len, int packetId, ByteArrayDataInputWrapper buf) throws IOException {
        PacketRegistry packetRegistry = getCurrentPacketRegistry();
        Class<? extends Packet> clazz = packetRegistry == null ? null : packetRegistry.getPacket(packetId);

        if (clazz == null) {
            if (MinecraftAFKBot.getInstance().getCurrentBot().getConfig().isLogPackets()) {
                byte[] bytes = new byte[buf.getAvailable()];
                buf.readFully(bytes);
                MinecraftAFKBot.getLog().info("[" + getState().name().toUpperCase() + "] |C| <<<  S : 0x" + Integer.toHexString(packetId) + " (" + (packetRegistry != null ? packetRegistry.getMojMapPacketName(packetId) : "") + ")");
            }
            return;
        } else if (MinecraftAFKBot.getInstance().getCurrentBot().getConfig().isLogPackets())
            MinecraftAFKBot.getLog().info("[" + getState().name().toUpperCase() + "] |C| <<<  S : " + clazz.getSimpleName());

        try {
            long startTime = System.currentTimeMillis();

            Packet packet = clazz.newInstance();
            packet.read(buf, this, len, MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol());

            long endTime = System.currentTimeMillis();

            if (MinecraftAFKBot.getInstance().getCurrentBot().getConfig().isLogPackets() && endTime - startTime > 2)
                MinecraftAFKBot.getLog().info("Handling packet " + clazz.getSimpleName() + " took " + (endTime - startTime) + "ms");
        } catch (InstantiationException | IllegalAccessException e) {
            MinecraftAFKBot.getLog().warning("Could not create new instance of " + clazz.getSimpleName());
            e.printStackTrace();
        }
    }

    public void activateEncryption() {
        try {
            out.flush();
            setOutputEncrypted(true);
            BufferedOutputStream var1 = new BufferedOutputStream(CryptManager.encryptOuputStream(getSecretKey(), MinecraftAFKBot.getInstance().getCurrentBot().getSocket().getOutputStream()), 5120);
            this.out = new DataOutputStream(var1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decryptInputStream() {
        try {
            InputStream var1;
            var1 = MinecraftAFKBot.getInstance().getCurrentBot().getSocket().getInputStream();
            this.in = new DataInputStream(CryptManager.decryptInputStream(getSecretKey(), var1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isEncrypted() {
        return outputEncrypted;
    }
}