package fr.gamity.launcher.thomas260913;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowlogger.Logger;
import fr.gamity.launcher.thomas260913.ui.PanelManager;
import fr.gamity.launcher.thomas260913.ui.panels.pages.Splash;
import fr.gamity.launcher.thomas260913.game.Parser.OptifineParser.OptifineJson;
import fr.gamity.launcher.thomas260913.game.VersionList;
import fr.gamity.launcher.thomas260913.utils.JavaManager;
import fr.gamity.launcher.thomas260913.utils.MCAccount;
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

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.io.*;

public class Launcher extends Application {
    private static final String version = "v4.3.2";
    public static DiscordRichPresence presence = new DiscordRichPresence();
    public static DiscordRPC lib = DiscordRPC.INSTANCE;
    private static Launcher instance;
    private final ILogger logger;
    private Path storageFile;
    private Path ConfigDir;
    private Path ClientDir;
    private final Path launcherDir = GameDirGenerator.createGameDir("gamity", true);
    public JavaManager javaManager;
    private final Saver saver;
    private final Saver accountSaver;
    private final List<MCAccount> mcAccountsList = new ArrayList<>();
    private final Integer maxAccount;
    private final StringBuilder logBuffer = new StringBuilder();
    private Stage stage;
    private PanelManager panelManager;
    private VersionList versionList;
    private List<OptifineJson.OptifineList> optifineList;

    public Launcher() {
        instance = this;
        MultiOutputStream multiOutStream = new MultiOutputStream();
        multiOutStream.addOutputStream(System.out);
        multiOutStream.addOutputStream(new BufferedOutputStream(logBuffer));

        PrintStream printStream = new PrintStream(multiOutStream);
        System.setOut(printStream);
        System.setErr(printStream);

        saver = new Saver(this.launcherDir.resolve("config.properties"));
        saver.load();

        String filesPath = saver.get("filesPath");

        if (filesPath == null || filesPath.isEmpty()) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Sélectionnez un dossier");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setCurrentDirectory(this.launcherDir.toFile());

            int result = chooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                filesPath = chooser.getSelectedFile().getAbsolutePath();
                if(Paths.get(filesPath) != this.launcherDir){
                    moveFiles(this.launcherDir,Paths.get(filesPath));
                }

                // Sauvegarder le chemin pour éviter de redemander plus tard
                saver.set("filesPath", filesPath);
                saver.save();
            } else {
                // L'utilisateur a annulé
                System.out.println("Aucun dossier sélectionné");
                Platform.exit();
                System.exit(0);
            }
        }

        if(!Paths.get(filesPath).toAbsolutePath().toString().equals(this.launcherDir.toAbsolutePath().toString())){
            storageFile = Paths.get(filesPath).resolve("gamity");
        }else{
            storageFile = this.launcherDir.toAbsolutePath();
        }
        this.logger = new Logger("[gamity]", storageFile.resolve("gamity.log"));
        javaManager = new JavaManager(launcherDir.resolve("java"));
        ConfigDir = storageFile.resolve("versions").resolve("config");
        ClientDir = storageFile.resolve("versions").resolve("clients");
        if (Files.notExists(this.storageFile)) {
            try {
                Files.createDirectory(this.storageFile);
            } catch (IOException e) {
                this.logger.err("Unable to create launcher folder");
                this.logger.printStackTrace(e);
            }
        }
        if (Files.notExists(this.storageFile.resolve("versions"))) {
            try {
                Files.createDirectory(this.storageFile.resolve("versions"));
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
        accountSaver = new Saver(storageFile.resolve("accounts.properties"));
        accountSaver.load();

        if (accountSaver.get("maxAccount") != null) {
            maxAccount = Math.min(Integer.parseInt(accountSaver.get("maxAccount")), 20);
        } else {
            maxAccount = 3;
            accountSaver.set("maxAccount","3");
            accountSaver.save();
        }
        if (accountSaver.get("selectAccount") == null) {
            accountSaver.set("selectAccount", String.valueOf(0));
            accountSaver.save();
        }
        if (saver.get("weblink") == null) {
            saver.set("weblink", "https://gamity-pvp.fr");
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
        Platform.runLater(()-> {
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
        });
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
        int index = Integer.parseInt(accountSaver.get("selectAccount"));
        return index >= 0
                && index < this.mcAccountsList.size()
                && this.mcAccountsList.get(index) != null;
    }

    public MCAccount getMCAccount() {
        return mcAccountsList.get(Integer.parseInt(accountSaver.get("selectAccount")));
    }

    public void setMCAccount(MCAccount mcAccount) {
        this.mcAccountsList.set(Integer.parseInt(accountSaver.get("selectAccount")), mcAccount);
    }

    public int getMCAccountSize() {
        return mcAccountsList.size();
    }

    public MCAccount getMCAccount(int id) {
        return mcAccountsList.get(id);
    }

    public void rmMCAccount() {
        for (int i = Integer.parseInt(accountSaver.get("selectAccount")); i < Integer.parseInt(accountSaver.get("maxAccount")); i++) {
            if (accountSaver.get("msRefreshToken" + i) != null && accountSaver.get("msAccessToken" + i) != null) {
                accountSaver.set("msAccessToken" + (i - 1), accountSaver.get("msAccessToken" + i));
                accountSaver.set("msRefreshToken" + (i - 1), accountSaver.get("msRefreshToken" + i));
                accountSaver.remove("msRefreshToken" + i);
                accountSaver.remove("msAccessToken" + i);
            } else if (accountSaver.get("offline-username" + i) != null) {
                accountSaver.set("offline-username" + (i - 1), accountSaver.get("offline-username" + i));
                accountSaver.remove("offline-username" + i);
            }
        }
        this.mcAccountsList.remove(Integer.parseInt(accountSaver.get("selectAccount")));
    }

    public void rmMCAccount(int index) {
        for (int i = index; i < Integer.parseInt(accountSaver.get("maxAccount")); i++) {
            if (accountSaver.get("msRefreshToken" + i) != null && accountSaver.get("msAccessToken" + i) != null) {
                accountSaver.set("msAccessToken" + (i - 1), accountSaver.get("msAccessToken" + i));
                accountSaver.set("msRefreshToken" + (i - 1), accountSaver.get("msRefreshToken" + i));
                accountSaver.remove("msRefreshToken" + i);
                accountSaver.remove("msAccessToken" + i);
            }
            accountSaver.set("offline-username" + (i - 1), accountSaver.get("offline-username" + i));
            accountSaver.remove("offline-username" + i);
        }
        this.mcAccountsList.remove(Integer.parseInt(accountSaver.get("selectAccount")));
    }

    public void addMCAccount(MCAccount mcAccount) {
        this.mcAccountsList.add(mcAccount);
    }

    public void addMCAccount(MCAccount mcAccount, int id) {
        this.mcAccountsList.add(id, mcAccount);
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

    public Saver getAccountSaver() {
        return accountSaver;
    }

    public Path getLauncherDir() {
        return storageFile;
    }

    public Path getClientDir() {
        return ClientDir;
    }

    public Path getConfigDir() {
        return ConfigDir;
    }
    public JavaManager getJavaManager() {
        return javaManager;
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

    public static void moveFiles(Path oldDir, Path newDir) {
        try {
            Files.createDirectories(newDir);

            // 1. déplacer tout
            Files.walk(oldDir)
                    .forEach(source -> {
                        try {
                            if(!source.getFileName().toString().equals("config.properties")) {
                                Path relative = oldDir.relativize(source);
                                Path target = newDir.resolve(relative);

                                Files.createDirectories(target.getParent());

                                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            // 2. supprimer l’ancien dossier (maintenant vide)
            Files.walk(oldDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            if(!path.getFileName().toString().equals("config.properties")) {
                                Files.deleteIfExists(path);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            System.out.println("Déplacement terminé");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }
}