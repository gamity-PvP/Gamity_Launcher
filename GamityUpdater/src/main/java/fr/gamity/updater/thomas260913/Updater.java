package fr.gamity.updater.thomas260913;

import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowlogger.Logger;
import fr.gamity.updater.thomas260913.ui.PanelManager;
import fr.gamity.updater.thomas260913.ui.panels.pages.Splash;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;

public class Updater extends Application {
    private static final String version = "1.5";
    private static Updater instance;
    private ILogger logger;
    private final Path launcherDir = GameDirGenerator.createGameDir("gamity", true);
    private Path storageFile;
    private final Saver saver;
    private Stage stage;
    private PanelManager panelManager;
    private String installerVersion;

    public Updater() {
        instance = this;

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
                return;
            }
        }

        if(!Paths.get(filesPath).toAbsolutePath().toString().equals(this.launcherDir.toAbsolutePath().toString())){
            storageFile = Paths.get(filesPath).resolve("gamity");
        }else{
            storageFile = this.launcherDir.toAbsolutePath();
        }
        this.logger = new Logger("[gamity]", storageFile.resolve("updater").resolve("updater.log"));
        if (Files.notExists(this.storageFile)) {
            try {
                Files.createDirectory(this.storageFile);
            } catch (IOException e) {
                this.logger.err("Unable to create launcher folder");
                this.logger.printStackTrace(e);
            }
        }
        if (Files.notExists(this.storageFile.resolve("updater"))) {
            try {
                Files.createDirectory(this.storageFile.resolve("updater"));
            } catch (IOException e) {
                this.logger.err("Unable to create updater folder");
                this.logger.printStackTrace(e);
            }
        }
    }

    public static Updater getInstance() {
        return instance;
    }

    public static String getUpdaterVersion() {
        return version;
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


    @Override
    public void start(Stage stage) {
        try {
            List<String> parameters = getParameters().getRaw();
            if(parameters.contains("--installerVersion")){
                this.installerVersion = parameters.get(parameters.indexOf("--installerVersion")+1);
            }else{
                this.installerVersion = null;
            }
            this.stage = stage;
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
            this.logger.info("Starting updater");
            this.logger.info("Updater version : " + getUpdaterVersion());
            this.logger.info("Installer version : " + this.installerVersion);
            this.panelManager = new PanelManager(this, stage);
            this.panelManager.init();
            panelManager.showPanel(new Splash(this.installerVersion));
        }catch(Exception ex){
            logger.printStackTrace(ex);
            showErrorDialog(ex);
        }
    }

    public ILogger getLogger() {
        return logger;
    }

    public Saver getSaver() {
        return saver;
    }

    public Path getLauncherDir() {
        return storageFile;
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