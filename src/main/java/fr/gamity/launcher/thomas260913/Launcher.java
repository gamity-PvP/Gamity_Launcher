package fr.gamity.launcher.thomas260913;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowlogger.Logger;
import fr.gamity.launcher.thomas260913.ui.PanelManager;
import fr.gamity.launcher.thomas260913.ui.panels.pages.Splash;
import fr.gamity.launcher.thomas260913.ui.panels.pages.content.Parser.OptifineParser.OptifineJson;
import fr.gamity.launcher.thomas260913.ui.panels.pages.content.VersionList;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Launcher extends Application {
    private static final String version = "bêta v3.8.7";
    public static DiscordRichPresence presence = new DiscordRichPresence();
    public static DiscordRPC lib = DiscordRPC.INSTANCE;
    private static Launcher instance;
    private final ILogger logger;
    private final Path launcherDir = GameDirGenerator.createGameDir("gamity", true);
    private final Path ConfigDir = GameDirGenerator.createGameDir("gamity", true).resolve("versions").resolve("config");
    private final Path ClientDir = GameDirGenerator.createGameDir("gamity", true).resolve("versions").resolve("clients");
    private final Saver saver;
    private final List<AuthInfos> authInfos = new ArrayList<>();
    private final Integer maxAccount;
    private final StringBuilder logBuffer = new StringBuilder();
    private Stage stage;
    private PanelManager panelManager;
    private Path java21;
    private Path java17;
    private Path java8;
    private VersionList versionList;
    private List<OptifineJson.OptifineList> optifineList;

    public Launcher() {
        instance = this;
        this.logger = new Logger("[gamity]", this.launcherDir.resolve("gamity.log"));
        MultiOutputStream multiOutStream = new MultiOutputStream();
        multiOutStream.addOutputStream(System.out);
        multiOutStream.addOutputStream(new BufferedOutputStream(logBuffer));

        PrintStream printStream = new PrintStream(multiOutStream);
        System.setOut(printStream);
        System.setErr(printStream);
        if (Files.notExists(this.launcherDir)) {
            try {
                Files.createDirectory(this.launcherDir);
            } catch (IOException e) {
                this.logger.err("Unable to create launcher folder");
                this.logger.printStackTrace(e);
            }
        }
        if (Files.notExists(this.launcherDir.resolve("versions"))) {
            try {
                Files.createDirectory(this.launcherDir.resolve("versions"));
            } catch (IOException e) {
                this.logger.err("Unable to create versions folder");
                this.logger.printStackTrace(e);
            }
        }
        if (Files.notExists(this.ClientDir)) {
            try {
                Files.createDirectory(this.ClientDir);
            } catch (IOException e) {
                this.logger.err("Unable to create client folder");
                this.logger.printStackTrace(e);
            }
        }
        if (Files.notExists(this.ConfigDir)) {
            try {
                Files.createDirectory(this.ConfigDir);
            } catch (IOException e) {
                this.logger.err("Unable to create config folder");
                this.logger.printStackTrace(e);
            }
        }
        saver = new Saver(this.launcherDir.resolve("config.properties"));
        saver.load();
        if (saver.get("maxAccount") != null) {
            maxAccount = Math.min(Integer.parseInt(saver.get("maxAccount")), 20);
        } else {
            maxAccount = 3;
        }
        if (saver.get("selectAccount") == null) {
            saver.set("selectAccount", String.valueOf(0));
            saver.save();
        }
    }

    public static Launcher getInstance() {
        return instance;
    }

    public static String getLauncherVersion() {
        return version;
    }

    public void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("images/icon.png"));
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showErrorDialog(Exception e, Stage ownerStage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ownerStage);
        dialogStage.setTitle("Error");

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        textArea.setText(sw.toString());

        VBox.setVgrow(textArea, Priority.ALWAYS);

        Button copyButton = new Button("Copy to Clipboard");
        copyButton.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(textArea.getText());
            clipboard.setContent(content);
        });

        HBox hbox = new HBox(copyButton);
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(10));

        vbox.getChildren().addAll(textArea, hbox);

        Scene scene = new Scene(vbox, 600, 400);
        dialogStage.setScene(scene);
        dialogStage.getIcons().add(new Image("images/icon.png"));
        dialogStage.show();
    }

    public void showErrorDialog(Exception e) {
        Platform.runLater(()->{
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(this.stage);
            dialogStage.setTitle("Error");

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(10));
            vbox.setSpacing(10);

            TextArea textArea = new TextArea();
            textArea.setEditable(false);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            textArea.setText(sw.toString());

            VBox.setVgrow(textArea, Priority.ALWAYS);

            Button copyButton = new Button("Copy to Clipboard");
            copyButton.setOnAction(event -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(textArea.getText());
                clipboard.setContent(content);
            });

            HBox hbox = new HBox(copyButton);
            hbox.setSpacing(10);
            hbox.setPadding(new Insets(10));

            vbox.getChildren().addAll(textArea, hbox);

            Scene scene = new Scene(vbox, 600, 400);
            dialogStage.setScene(scene);
            dialogStage.getIcons().add(new Image("images/icon.png"));
            dialogStage.show();
        });
    }

    public void loadAccount() {
        panelManager.showPanel(new Splash());
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
        this.logger.info("Starting launcher");
        this.logger.info("Launcher verion : " + getLauncherVersion());
        this.panelManager = new PanelManager(this, stage);
        this.panelManager.init();
        String applicationId = "1169537671325028432";
        String steamId = "";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> this.logger.info("rpc start for " + user.username);
        lib.Discord_Initialize(applicationId, handlers, true, steamId);
        presence.startTimestamp = System.currentTimeMillis() / 1000;
        presence.largeImageKey = "gamity_launcher";
        Launcher.presence.state = "Dans le launcher";
        Launcher.presence.largeImageText = "Dans le launcher";
        lib.Discord_UpdatePresence(presence);
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                    lib.Discord_UpdatePresence(presence);
                } catch (InterruptedException ignored) {
                }
            }
        }, "RPC-Callback-Handler").start();
        loadAccount();
    }

    public boolean isUserAlreadyLoggedIn() {
        if (saver.get("msAccessToken" + saver.get("selectAccount")) != null && saver.get("msRefreshToken" + saver.get("selectAccount")) != null) {
            try {
                MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                MicrosoftAuthResult response = authenticator.loginWithRefreshToken(saver.get("msRefreshToken" + saver.get("selectAccount")));

                saver.set("msAccessToken" + saver.get("selectAccount"), response.getAccessToken());
                saver.set("msRefreshToken" + saver.get("selectAccount"), response.getRefreshToken());
                saver.save();
                this.setAuthInfos(new AuthInfos(
                        response.getProfile().getName(),
                        response.getAccessToken(),
                        response.getProfile().getId(),
                        response.getXuid(),
                        response.getClientId()
                ));
                return true;
            } catch (MicrosoftAuthenticationException e) {
                saver.remove("msAccessToken" + saver.get("selectAccount"));
                saver.remove("msRefreshToken" + saver.get("selectAccount"));
                saver.save();
            }
        } else if (saver.get("offline-username" + saver.get("selectAccount")) != null) {
            this.authInfos.set(Integer.parseInt(saver.get("selectAccount")), new AuthInfos(saver.get("offline-username" + saver.get("selectAccount")), UUID.randomUUID().toString(), UUID.randomUUID().toString()));
            return true;
        }

        return false;
    }

    public AuthInfos getAuthInfos() {
        return authInfos.get(Integer.parseInt(saver.get("selectAccount")));
    }

    public void setAuthInfos(AuthInfos authInfos) {
        this.authInfos.set(Integer.parseInt(saver.get("selectAccount")), authInfos);
    }

    public int getAuthInfosSize() {
        return authInfos.size();
    }

    public AuthInfos getAuthInfos(int id) {
        return authInfos.get(id);
    }

    public void rmAuthInfos() {
        for (int i = Integer.parseInt(saver.get("selectAccount")); i < Integer.parseInt(saver.get("maxAccount")); i++) {
            if (saver.get("msRefreshToken" + i) != null && saver.get("msAccessToken" + i) != null) {
                saver.set("msAccessToken" + (i - 1), saver.get("msAccessToken" + i));
                saver.set("msRefreshToken" + (i - 1), saver.get("msRefreshToken" + i));
                saver.remove("msRefreshToken" + i);
                saver.remove("msAccessToken" + i);
            } else if (saver.get("offline-username" + i) != null) {
                saver.set("offline-username" + (i - 1), saver.get("offline-username" + i));
                saver.remove("offline-username" + i);
            }
        }
        this.authInfos.remove(Integer.parseInt(saver.get("selectAccount")));
    }

    public void addAuthInfos(AuthInfos authInfos) {
        this.authInfos.add(authInfos);
    }

    public void addAuthInfos(AuthInfos authInfos, int id) {
        this.authInfos.add(id, authInfos);
    }

    public int getMaxAccount() {
        return maxAccount;
    }

    public ILogger getLogger() {
        return logger;
    }

    public Saver getSaver() {
        return saver;
    }

    public Path getLauncherDir() {
        return launcherDir;
    }

    public Path getClientDir() {
        return ClientDir;
    }

    public Path getConfigDir() {
        return ConfigDir;
    }

    public Path getJava21() {
        return java21;
    }

    public void setJava21(Path path) {
        java21 = path;
    }

    public Path getJava17() {
        return java17;
    }

    public void setJava17(Path path) {
        java17 = path;
    }

    public Path getJava8() {
        return java8;
    }

    public void setJava8(Path path) {
        java8 = path;
    }
    public void setVersionList(VersionList versionList){
        this.versionList = versionList;
    }
    public VersionList getVersionList(){
        return this.versionList;
    }
    public void setOptifineList(List<OptifineJson.OptifineList> optifineList){
        this.optifineList = optifineList;
    }
    public List<OptifineJson.OptifineList> getOptifineList(){
        return this.optifineList;
    }

    public StringBuilder getLogBuffer() {
        return logBuffer;
    }


    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }
}