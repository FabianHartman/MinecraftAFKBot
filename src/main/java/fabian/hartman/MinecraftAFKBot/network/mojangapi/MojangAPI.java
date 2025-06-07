package fabian.hartman.MinecraftAFKBot.network.mojangapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.auth.AuthData;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.utils.UUIDUtils;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class MojangAPI {

    private final String REALMS_ENDPOINT = "https://pc.realms.minecraft.net";
    private final String MOJANG_ENDPOINT = "https://api.minecraftservices.com";

    private final HttpClient client;
    private final AuthData authData;

    public MojangAPI(AuthData authData, int protocolId) {
        this.authData = authData;
        BasicCookieStore cookies = new BasicCookieStore();

        BasicClientCookie sidCookie = new BasicClientCookie("sid", String.join(":", "token", authData.getAccessToken(), UUIDUtils.withoutDashes(authData.getUuid())));
        BasicClientCookie userCookie = new BasicClientCookie("user", authData.getUsername());
        BasicClientCookie versionCookie = new BasicClientCookie("version", ProtocolConstants.getExactVersionString(protocolId));

        sidCookie.setDomain(".pc.realms.minecraft.net");
        userCookie.setDomain(".pc.realms.minecraft.net");
        versionCookie.setDomain(".pc.realms.minecraft.net");

        sidCookie.setPath("/");
        userCookie.setPath("/");
        versionCookie.setPath("/");

        cookies.addCookie(sidCookie);
        cookies.addCookie(userCookie);
        cookies.addCookie(versionCookie);

        client = HttpClientBuilder.create()
                .setDefaultCookieStore(cookies)
                .build();
    }

    public void obtainCertificates() {
        HttpUriRequest request = RequestBuilder.post()
                .setUri(MOJANG_ENDPOINT + "/player/certificates")
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authData.getAccessToken())
                .setHeader(HttpHeaders.CONTENT_LENGTH, "0")
                .build();

        try {
            HttpResponse answer = client.execute(request);
            if (answer.getStatusLine().getStatusCode() != 200) {
                EntityUtils.consumeQuietly(answer.getEntity());
                System.out.println(MessageFormat.format("Could not retrieve the certificates required for chat signing from {0}: {1}", MOJANG_ENDPOINT, answer.getStatusLine().toString()));
                return;
            }
            JsonObject responseJson = new JsonParser().parse(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8)).getAsJsonObject();

            if (responseJson == null || !responseJson.has("keyPair")) {
                System.out.println(MessageFormat.format("Could not retrieve the certificates required for chat signing from {0}: {1}", MOJANG_ENDPOINT, answer.getStatusLine().toString()));
                return;
            }

            JsonObject keyPair = responseJson.getAsJsonObject("keyPair");
            String pubKeyContent = keyPair.get("publicKey").getAsString()
                    .replace("\n", "")
                    .replace("\\n", "")
                    .replace("-----BEGIN RSA PUBLIC KEY-----", "")
                    .replace("-----END RSA PUBLIC KEY-----", "");
            String privKeyContent = keyPair.get("privateKey").getAsString()
                    .replace("\n", "")
                    .replace("\\n", "")
                    .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                    .replace("-----END RSA PRIVATE KEY-----", "");
            String pubKeySig = responseJson.get(
                    MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MC_1_19_1
                            ? "publicKeySignatureV2"
                            : "publicKeySignature").getAsString();

            KeyFactory kf = KeyFactory.getInstance("RSA");

            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privKeyContent));
            PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);

            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(pubKeyContent));
            RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

            String expirationContent = responseJson.get("expiresAt").getAsString();
            TemporalAccessor temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(expirationContent);
            Instant instant = Instant.from(temporalAccessor);
            Date expiresAt = Date.from(instant);

            if (privKey == null || pubKey == null) {
                System.out.println(MessageFormat.format("Could not retrieve the certificates required for chat signing from {0}: {1}", MOJANG_ENDPOINT, answer.getStatusLine().toString()));
                return;
            }

            authData.setProfileKeys(new AuthData.ProfileKeys(pubKey, pubKeySig, privKey, expiresAt.getTime()));

            System.out.println("Obtained the required keys for chat signing!");
        } catch (IOException | JsonParseException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            System.out.println(MessageFormat.format("Could not retrieve the certificates required for chat signing from {0}: {1}", MOJANG_ENDPOINT, e.getMessage()));
        }
    }

    public List<Realm> getPossibleWorlds() {
        List<Realm> joinableRealms = new ArrayList<>();
        HttpUriRequest request = RequestBuilder.get()
                .setUri(REALMS_ENDPOINT + "/worlds")
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        try {
            HttpResponse answer = client.execute(request);
            if (answer.getStatusLine().getStatusCode() != 200) {
                EntityUtils.consumeQuietly(answer.getEntity());
                System.out.println(MessageFormat.format("Could not connect to the realms endpoint ({0}): {1}", REALMS_ENDPOINT, answer.getStatusLine().toString()));
                return joinableRealms;
            }
            JsonObject responseJson = new JsonParser().parse(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8)).getAsJsonObject();
            JsonArray servers = responseJson.getAsJsonArray("servers");
            if (servers.size() == 0)
                return joinableRealms;

            servers.forEach(server -> {
                JsonObject serverObj = server.getAsJsonObject();
                try {
                    long id = serverObj.get("id").getAsLong();
                    String owner = serverObj.get("owner").isJsonNull() ? "null" : serverObj.get("owner").getAsString();
                    String name = serverObj.get("name").isJsonNull() ? "null" : serverObj.get("name").getAsString();
                    String motd = serverObj.get("motd").isJsonNull() ? "null" : serverObj.get("motd").getAsString();
                    joinableRealms.add(new Realm(id, name, owner, motd));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println(MessageFormat.format("Could not parse realm server entry: {0}", serverObj.toString()));
                }
            });
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
            System.out.println(MessageFormat.format("Could not connect to the realms endpoint ({0}): {1}", REALMS_ENDPOINT, e.getMessage()));
        }
        return joinableRealms;
    }

    public void printRealms(List<Realm> realms) {
        if (realms.isEmpty()) {
            System.out.println("There are no realms available for you to join. Remember to accept an invitation before you can join the realm.");
            return;
        }
        realms.forEach(realm -> {
            MinecraftAFKBot.getLog().info("");
            MinecraftAFKBot.getLog().info("ID: " + realm.getId());
            MinecraftAFKBot.getLog().info("name: " + realm.getName());
            MinecraftAFKBot.getLog().info("motd: " + realm.getMotd());
            MinecraftAFKBot.getLog().info("owner: " + realm.getOwner());
        });
    }

    public void agreeTos() {
        HttpUriRequest request = RequestBuilder.post()
                .setUri(REALMS_ENDPOINT + "/mco/tos/agreed")
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        try {
            HttpResponse answer = client.execute(request);
            if (answer.getStatusLine().getStatusCode() != 204) {
                System.out.println(MessageFormat.format("The terms of service for realms could not be accepted: {0}", answer.getStatusLine()));
                return;
            } else
                System.out.println("The terms of service for realms have been accepted!");
            EntityUtils.consumeQuietly(answer.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServerIP(long serverId) {
        HttpUriRequest request = RequestBuilder.get()
                .setUri(REALMS_ENDPOINT + "/worlds/v1/" + serverId + "/join/pc")
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        try {
            HttpResponse answer = client.execute(request);
            if (answer.getStatusLine().getStatusCode() != 200) {
                EntityUtils.consumeQuietly(answer.getEntity());
                System.out.println(MessageFormat.format("Could not retrieve server address from {0}: {1}", REALMS_ENDPOINT, answer.getStatusLine()));
                return null;
            }
            JsonObject responseJson = new JsonParser().parse(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8)).getAsJsonObject();
            System.out.println(MessageFormat.format("Connecting to realm {0}...", responseJson.toString()));
            return responseJson.get("address").getAsString();
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
            System.out.println(MessageFormat.format("Could not connect to the realms endpoint ({0}): {1}", REALMS_ENDPOINT, e.getMessage()));
        }
        return null;
    }
}