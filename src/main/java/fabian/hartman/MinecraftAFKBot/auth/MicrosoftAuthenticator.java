package fabian.hartman.MinecraftAFKBot.auth;

import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.auth.service.MsaAuthenticationService;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fabian.hartman.MinecraftAFKBot.auth.msa.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.utils.reflect.MethodAccessor;
import fabian.hartman.MinecraftAFKBot.utils.reflect.Reflect;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MicrosoftAuthenticator implements IAuthenticator {
    private final static MethodAccessor LOGIN_RESPONSE_ACCESSOR = Reflect.getMethod(MsaAuthenticationService.class, "getLoginResponseFromToken", String.class);
    private final static MethodAccessor GET_PROFILE_ACCESSOR = Reflect.getMethod(MsaAuthenticationService.class, "getProfile");

    public static final String CLIENT_ID = "fef9faea-d962-4476-9ce7-4960c8baa946";
    private final static Gson GSON = new Gson();

    @Override
    public Optional<AuthData> authenticate() {
        System.out.println("Trying to authenticate at Microsoft...");
        String refreshToken = readRefreshToken();
        CompletableFuture<Alert> authDialog = null;

        try {
            if (refreshToken == null) {
                DeviceTokenCallback callback = DeviceTokenGenerator.createDeviceToken(CLIENT_ID);

                if (callback == null) {
                    System.out.println("Error while creating refresh token... Please try again");
                    return Optional.empty();
                }

                MinecraftAFKBot.getLog().warning(" ");
                MinecraftAFKBot.getLog().warning(" ");
                for (String line : MessageFormat.format("Please log in with your Microsoft account using the following code {0} at {1}.\\n\\\n" +
                        "This is a one-time step to allow AFKBot to access your account and log in to Minecraft servers.\\n\\\n" +
                        "The data will only be stored on your computer, the developers have no access to your data!", callback.getUserCode(), callback.getVerificationUrl()).split("\n")) {
                    MinecraftAFKBot.getLog().warning(line);
                }

                MinecraftAFKBot.getLog().warning(" ");

                try {
                    refreshToken = RefreshTokenCallback.await(callback, CLIENT_ID);
                } catch (ObtainTokenException ex) {
                    ex.printStackTrace();
                    closeDialog(authDialog);
                    MinecraftAFKBot.getInstance().getCurrentBot().setPreventStartup(true);
                    return Optional.empty();
                }
            } else {
                System.out.println(MessageFormat.format("Found refreshToken at {0}", MinecraftAFKBot.getInstance().getRefreshTokenFile().getAbsolutePath()));
            }

            if (refreshToken == null) {
                System.out.println("Error while creating refresh token... Please try again");
                closeDialog(authDialog);
                return Optional.empty();
            }

            setDialogProgress(authDialog, 0.1);

            try {
                Files.write(refreshToken, MinecraftAFKBot.getInstance().getRefreshTokenFile(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }

            AuthenticationService authService = new MsaAuthenticationService(CLIENT_ID, refreshToken);
            setDialogProgress(authDialog, 0.2);
            AccessTokenCallback callback = AccessTokenGenerator.createAccessToken(refreshToken, CLIENT_ID);

            if (callback == null) {
                System.out.println("Error while creating access token... Please try again");
                closeDialog(authDialog);
                return Optional.empty();
            }
            setDialogProgress(authDialog, 0.5);
            JsonObject object = GSON.toJsonTree(LOGIN_RESPONSE_ACCESSOR.invoke(authService, "d=" + callback.getAccessToken())).getAsJsonObject();
            authService.setAccessToken(object.get("access_token").getAsString());
            setDialogProgress(authDialog, 0.9);
            MicrosoftAuthenticator.GET_PROFILE_ACCESSOR.invoke(authService);
            closeDialog(authDialog);
            return Optional.of(new AuthData(authService.getAccessToken(), authService.getSelectedProfile().getIdAsString(), authService.getSelectedProfile().getName()));
        } catch (Throwable e) {
            e.printStackTrace();
            closeDialog(authDialog);
            return Optional.empty();
        }
    }

    private void setDialogProgress(CompletableFuture<Alert> authDialog, double progress) {
        modifyDialog(authDialog, alert -> {
            Node flowPane = alert.getDialogPane().contentProperty().get();
            if (flowPane instanceof FlowPane) {
                ObservableList<Node> children = ((FlowPane) flowPane).getChildren();
                if (children.size() >= 3) {
                    Node progressBar = children.get(2);
                    if (progressBar instanceof ProgressBar) {
                        ((ProgressBar) progressBar).setProgress(progress);
                    }
                }
            }
        });
    }

    private void closeDialog(CompletableFuture<Alert> authDialog) {
        modifyDialog(authDialog, alert -> {
            alert.setResult(ButtonType.OK);
            alert.close();
        });
    }

    private void modifyDialog(CompletableFuture<Alert> authDialog, Consumer<Alert> consumer) {
        if (authDialog != null) {
            Alert alert = authDialog.join();
            Platform.runLater(() -> consumer.accept(alert));
        }
    }

    private String readRefreshToken() {
        File file = MinecraftAFKBot.getInstance().getRefreshTokenFile();
        
        if (!file.exists()) {
            return null;
        }

        try {
            return Files.readFirstLine(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
