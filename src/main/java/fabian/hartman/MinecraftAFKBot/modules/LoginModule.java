package fabian.hartman.MinecraftAFKBot.modules;

import fabian.hartman.MinecraftAFKBot.event.login.*;
import lombok.Getter;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.EventHandler;
import fabian.hartman.MinecraftAFKBot.event.Listener;
import fabian.hartman.MinecraftAFKBot.event.configuration.ConfigurationFinishEvent;
import fabian.hartman.MinecraftAFKBot.event.configuration.ConfigurationStartEvent;
import fabian.hartman.MinecraftAFKBot.event.configuration.KnownPacksRequestedEvent;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolState;
import fabian.hartman.MinecraftAFKBot.network.protocol.configuration.PacketOutFinishConfiguration;
import fabian.hartman.MinecraftAFKBot.network.protocol.configuration.PacketOutKnownPacks;
import fabian.hartman.MinecraftAFKBot.network.protocol.configuration.PacketOutPluginMessage;
import fabian.hartman.MinecraftAFKBot.network.protocol.login.PacketOutEncryptionResponse;
import fabian.hartman.MinecraftAFKBot.network.protocol.login.PacketOutLoginAcknowledge;
import fabian.hartman.MinecraftAFKBot.network.protocol.login.PacketOutLoginPluginResponse;
import fabian.hartman.MinecraftAFKBot.network.protocol.login.PacketOutLoginStart;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutAcknowledgeConfiguration;
import fabian.hartman.MinecraftAFKBot.network.utils.CryptManager;
import fabian.hartman.MinecraftAFKBot.utils.UUIDUtils;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;

public class LoginModule extends Module implements Listener {
    @Getter private String userName;

    public LoginModule(String userName) {
        this.userName = userName;
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
    }

    @Override
    public void onEnable() {
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutLoginStart(getUserName()));
    }

    @Override
    public void onDisable() {
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().unregisterListener(this);
    }

    @EventHandler
    public void onEncryptionRequest(EncryptionRequestEvent event) {
        NetworkHandler networkHandler = MinecraftAFKBot.getInstance().getCurrentBot().getNet();

        // Set public key
        networkHandler.setPublicKey(event.getPublicKey());

        // Generate & Set secret key
        SecretKey secretKey = CryptManager.createNewSharedKey();
        networkHandler.setSecretKey(secretKey);

        byte[] serverIdHash = CryptManager.getServerIdHash(event.getServerId().trim(), event.getPublicKey(), secretKey);
        if(serverIdHash == null) {
            System.out.println("The server id could not be hashed. The bot will be stopped...");
            MinecraftAFKBot.getInstance().getCurrentBot().setRunning(false);
            return;
        }

        String var5 = new BigInteger(serverIdHash).toString(16);
        sendSessionRequest(MinecraftAFKBot.getInstance().getCurrentBot().getAuthData().getUsername(),
                "token:" + MinecraftAFKBot.getInstance().getCurrentBot().getAuthData().getAccessToken() + ":" + UUIDUtils.withoutDashes(MinecraftAFKBot.getInstance().getCurrentBot().getAuthData().getUuid()), var5);

        networkHandler.sendPacket(new PacketOutEncryptionResponse(event.getServerId(), event.getPublicKey(), event.getVerifyToken(), secretKey));
        networkHandler.activateEncryption();
        networkHandler.decryptInputStream();
    }

    @EventHandler
    public void onLoginDisconnect(LoginDisconnectEvent event) {
        System.out.println(MessageFormat.format("Login failed: {0}", event.getErrorMessage()));
        MinecraftAFKBot.getInstance().getCurrentBot().setRunning(false);
        MinecraftAFKBot.getInstance().getCurrentBot().setAuthData(null);
    }

    @EventHandler
    public void onSetCompression(SetCompressionEvent event) {
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().setThreshold(event.getThreshold());
    }

    @EventHandler
    public void onLoginPluginRequest(LoginPluginRequestEvent event) {
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutLoginPluginResponse(event.getMsgId(), false, null));
    }

    @EventHandler
    public void onLoginSuccess(LoginSuccessEvent event) {
        System.out.println(MessageFormat.format("Login successful!\\n\\\n" +
                "Name: {0}\\n\\\n" +
                "UUID: {1}",event.getUserName(), event.getUuid().toString()));
        if (MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol() < ProtocolConstants.MC_1_20_2) {
            MinecraftAFKBot.getInstance().getCurrentBot().getNet().setState(ProtocolState.PLAY);
        } else {
            MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutLoginAcknowledge());
            MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new ConfigurationStartEvent());
        }
        MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().setUuid(event.getUuid());
    }

    @EventHandler
    public void onConfigurationStart(ConfigurationStartEvent event) {
        if (MinecraftAFKBot.getInstance().getCurrentBot() != null && MinecraftAFKBot.getInstance().getCurrentBot().getNet() != null
                && MinecraftAFKBot.getInstance().getCurrentBot().getNet().getState() == ProtocolState.PLAY)
            MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutAcknowledgeConfiguration());
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().setState(ProtocolState.CONFIGURATION);
    }

    @EventHandler
    public void onConfigurationFinish(ConfigurationFinishEvent event) {
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutPluginMessage("minecraft:brand", (out, protocol) -> Packet.writeString("afkbot", out)));
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutFinishConfiguration());
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().setState(ProtocolState.PLAY);
    }

    @EventHandler
    public void onKnownPacksRequested(KnownPacksRequestedEvent event) {
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutKnownPacks(event.getKnownPacks()));
    }

    private String sendSessionRequest(String user, String session, String serverid) {
        try {
            return sendGetRequest("http://session.minecraft.net/game/joinserver.jsp"
                    + "?user=" + urlEncode(user)
                    + "&sessionId=" + urlEncode(session)
                    + "&serverId=" + urlEncode(serverid));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String sendGetRequest(String url) {
        try {
            URL var4 = new URL(url);
            BufferedReader var5 = new BufferedReader(new InputStreamReader(var4.openStream()));
            String var6 = var5.readLine();
            var5.close();
            return var6;
        } catch (IOException var7) {
            return var7.toString();
        }
    }

    private String urlEncode(String par0Str) throws IOException {
        return URLEncoder.encode(par0Str, "UTF-8");
    }
}