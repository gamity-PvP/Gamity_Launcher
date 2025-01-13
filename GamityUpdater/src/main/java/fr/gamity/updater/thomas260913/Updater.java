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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class Updater extends Application {
    private static final String version = "1.0";
    private static Updater instance;
    private final ILogger logger;
    private final Path launcherDir = GameDirGenerator.createGameDir("gamity", true);
    private final Saver saver;
    private Stage stage;
    private PanelManager panelManager;

    public Updater() {
        instance = this;
        this.logger = new Logger("[gamity]", this.launcherDir.resolve("updater").resolve("updater.log"));
        if (Files.notExists(this.launcherDir)) {
            try {
                Files.createDirectory(this.launcherDir);
            } catch (IOException e) {
                this.logger.err("Unable to create launcher folder");
                this.logger.printStackTrace(e);
            }
        }
        if (Files.notExists(this.launcherDir.resolve("updater"))) {
            try {
                Files.createDirectory(this.launcherDir.resolve("updater"));
            } catch (IOException e) {
                this.logger.err("Unable to create updater folder");
                this.logger.printStackTrace(e);
            }
        }
        saver = new Saver(this.launcherDir.resolve("updater").resolve("config.properties"));
        saver.load();
    }

    public static Updater getInstance() {
        return instance;
    }

    public static String getUpdaterVersion() {
        return version;
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


    @Override
    public void start(Stage stage) {
        try {
            this.stage = stage;
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
            this.logger.info("Starting updater");
            this.logger.info("Updater verion : " + getUpdaterVersion());
            this.panelManager = new PanelManager(this, stage);
            this.panelManager.init();
            panelManager.showPanel(new Splash());
        }catch(Exception ex){
            logger.printStackTrace(ex);
            showErrorDialog(ex);
        }
    }

    public ILogger getLogger() {
        return logger;
    }

    public Path getLauncherDir() {
        return launcherDir;
    }

    public Path getUpdaterDir() {
        return launcherDir.resolve("updater");
    }

    public Saver getSaver() {
        return saver;
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }
}