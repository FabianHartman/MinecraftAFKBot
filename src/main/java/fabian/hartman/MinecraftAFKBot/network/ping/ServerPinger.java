package fabian.hartman.MinecraftAFKBot.network.ping;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.utils.ChatComponentUtils;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Hashtable;

@AllArgsConstructor
public class ServerPinger {

    private String serverName;
    private int serverPort;

    public void ping() {
        MinecraftAFKBot.getInstance().getCurrentBot().setServerProtocol(ProtocolConstants.getProtocolId(MinecraftAFKBot.getInstance().getCurrentBot().getConfig().getDefaultProtocol()));
        if (serverName == null || serverName.trim().isEmpty()) {
            System.out.println("The given server seems to be not existing. Please check the server address in the Settings (server.ip)");
            MinecraftAFKBot.getInstance().getCurrentBot().setRunning(false);
            MinecraftAFKBot.getInstance().getCurrentBot().setWontConnect(true);
            return;
        }

        updateWithSRV();

        if (MinecraftAFKBot.getInstance().getConfig().getRealmId() >= 0) {
            System.out.println(MessageFormat.format("The server {0} is not reachable with the Minecraft version. The automatic version detection may not work. Please set a default-protocol in the Settings.", serverName));
            if (MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol() == ProtocolConstants.AUTOMATIC)
                MinecraftAFKBot.getInstance().getCurrentBot().setServerProtocol(ProtocolConstants.getLatest());
            return;
        }

        try {

            Socket socket = new Socket(serverName, serverPort);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            int pingProtocol = (ProtocolConstants.getProtocolId(MinecraftAFKBot.getInstance().getCurrentBot().getConfig().getDefaultProtocol()));
            if (pingProtocol == ProtocolConstants.AUTOMATIC)
                pingProtocol = ProtocolConstants.getLatest();

            ByteArrayDataOutput buf = ByteStreams.newDataOutput();
            Packet.writeVarInt(0, buf);
            Packet.writeVarInt(pingProtocol, buf);
            Packet.writeString(serverName, buf);
            buf.writeShort(serverPort);
            Packet.writeVarInt(1, buf);

            send(buf, out);

            buf = ByteStreams.newDataOutput();
            Packet.writeVarInt(0, buf);
            send(buf, out);

            Packet.readVarInt(in); //ignore
            Packet.readVarInt(in); //id

            String pong = Packet.readString(in);
            JsonObject root = new JsonParser().parse(pong).getAsJsonObject();
            long protocolId = root.getAsJsonObject("version").get("protocol").getAsLong();
            long currPlayers = root.getAsJsonObject("players").get("online").getAsLong();

            if (MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol() == ProtocolConstants.AUTOMATIC)
                MinecraftAFKBot.getInstance().getCurrentBot().setServerProtocol(Long.valueOf(protocolId).intValue());
            else if (protocolId != MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol()) {
                System.out.println(MessageFormat.format("The server responded with a different protocol version {0} than what the bot selected {1}. You may need to change it in the Config.",
                        "\"" + ProtocolConstants.getVersionString(Long.valueOf(protocolId).intValue()) + "\" (" + protocolId + ")",
                        "\"" + ProtocolConstants.getVersionString(MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol()) + "\" (" + MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol() + ")"));
            }
            String description = "Unknown";
            try {
                try {
                    if (protocolId > ProtocolConstants.MC_1_8)
                        description = ChatComponentUtils.toPlainText(root.getAsJsonObject("description"));
                    else
                        description = root.getAsJsonPrimitive("description").getAsString();
                } catch (Exception ex) {
                    description = root.get("description").toString();
                }
            } catch (Exception ignored) {
            } finally {
                if (description == null || description.trim().isEmpty())
                    description = "Unknown";
            }

            System.out.println(MessageFormat.format("Received pong: {0}, version: {1} ({2}), players online: {3}", description, ProtocolConstants.getVersionString(Long.valueOf(protocolId).intValue()), String.valueOf(protocolId), String.valueOf(currPlayers)));
            if (currPlayers >= MinecraftAFKBot.getInstance().getCurrentBot().getConfig().getAutoDisconnectPlayersThreshold() && MinecraftAFKBot.getInstance().getCurrentBot().getConfig().isAutoDisconnect()) {
                System.out.println("The server is full. Bot will be stopped!");
                MinecraftAFKBot.getInstance().getCurrentBot().setWontConnect(true);
            }

            out.close();
            in.close();
            socket.close();

        } catch (UnknownHostException e) {
            System.out.println(MessageFormat.format("The server {0} is unknown.", serverName));
        } catch (Exception e) {
            System.out.println(MessageFormat.format("The server {0} is not reachable with the Minecraft version. The automatic version detection may not work. Please set a default-protocol in the Settings.", serverName));
            if (MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol() == ProtocolConstants.AUTOMATIC)
                MinecraftAFKBot.getInstance().getCurrentBot().setServerProtocol(ProtocolConstants.getLatest());
            e.printStackTrace();
        }
    }

    public void updateWithSRV() {
        // Getting SRV Record - changing data to correct ones
        if (serverPort == 25565 || serverPort < 1) {
            String[] serverData = getServerAddress(serverName);
            if (!serverData[0].equalsIgnoreCase(serverName))
                System.out.println(MessageFormat.format("The server host has been changed: {0}", serverData[0]));
            this.serverName = serverData[0];
            this.serverPort = Integer.valueOf(serverData[1]);
            if (serverPort != 25565)
                System.out.println(MessageFormat.format("The server port has been changed: {0}", serverPort));
        }

        MinecraftAFKBot.getInstance().getCurrentBot().setServerHost(serverName);
        MinecraftAFKBot.getInstance().getCurrentBot().setServerPort(serverPort);
    }

    private static String[] getServerAddress(String serverHost) {
        try {
            Class.forName("com.sun.jndi.dns.DnsContextFactory");
            Hashtable<String, String> hashtable = new Hashtable<>();
            hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            hashtable.put("java.naming.provider.url", "dns:");
            hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
            DirContext dircontext = new InitialDirContext(hashtable);
            Attributes attributes = dircontext.getAttributes("_minecraft._tcp." + serverHost, new String[]{"SRV"});
            String[] astring = attributes.get("srv").get().toString().split(" ", 4);
            return new String[]{astring[3], astring[2]};
        } catch (Throwable var6) {
            return new String[]{serverHost, Integer.toString(25565)};
        }
    }

    private void send(ByteArrayDataOutput buf, DataOutputStream out) throws IOException {
        ByteArrayDataOutput sender = ByteStreams.newDataOutput();
        Packet.writeVarInt(buf.toByteArray().length, sender);
        sender.write(buf.toByteArray());
        out.write(sender.toByteArray());
        out.flush();
    }
}