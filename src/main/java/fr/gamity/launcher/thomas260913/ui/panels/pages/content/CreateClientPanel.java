package fr.gamity.launcher.thomas260913.ui.panels.pages.content;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gamity.launcher.thomas260913.Launcher;
import fr.gamity.launcher.thomas260913.UncaughtExceptionHandler;
import fr.gamity.launcher.thomas260913.ui.PanelManager;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.Step;
import fr.gamity.launcher.thomas260913.utils.JavaInstaller;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;

public class CreateClientPanel extends ContentPanel {
    private final Config.CustomServer config;
    private final boolean export;
    public CreateClientPanel(Config.CustomServer config, boolean export){
        this.config = config;
        this.export = export;
    }
    public CreateClientPanel(Config.CustomServer config){
        this.config = config;
        this.export = false;
    }
    private final Saver saver = Launcher.getInstance().getSaver();
    GridPane boxPane = new GridPane();
    ProgressBar progressBar = new ProgressBar();
    Label stepLabel = new Label();
    Label fileLabel = new Label();
    boolean isDownloading = false;

    @Override
    public String getName() {
        return config.name;
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/home.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        if(export){
            rowConstraints.setMinHeight(145);
            rowConstraints.setMaxHeight(145);
        }else {
            rowConstraints.setMinHeight(75);
            rowConstraints.setMaxHeight(75);
        }
        this.layout.getRowConstraints().addAll(rowConstraints, new RowConstraints());
        boxPane.getStyleClass().add("box-pane");
        setCanTakeAllSize(boxPane);
        boxPane.setPadding(new Insets(20));
        this.layout.add(boxPane, 0, 0);
        this.layout.getStyleClass().add("home-layout");

        progressBar.getStyleClass().add("download-progress");
        stepLabel.getStyleClass().add("download-status");
        fileLabel.getStyleClass().add("download-status");

        progressBar.setTranslateY(-15);
        setCenterH(progressBar);
        setCanTakeAllWidth(progressBar);

        stepLabel.setTranslateY(5);
        setCenterH(stepLabel);
        setCanTakeAllSize(stepLabel);

        fileLabel.setTranslateY(20);
        setCenterH(fileLabel);
        setCanTakeAllSize(fileLabel);

        this.showPlayButton();
    }

    private void showPlayButton() {
        boxPane.getChildren().clear();

        Button playBtn = new Button("Jouer");
        MaterialDesignIconView playIcon = new MaterialDesignIconView(MaterialDesignIcon.PLAY);
        playIcon.getStyleClass().add("play-icon");
        setCanTakeAllSize(playBtn);
        setCenterH(playBtn);
        setCenterV(playBtn);
        playBtn.getStyleClass().add("play-btn");
        playBtn.setGraphic(playIcon);
        playBtn.setOnMouseClicked(e -> this.play());

        Button exportBtn = new Button("Exporter la config");
        MaterialDesignIconView exportIcon = new MaterialDesignIconView(MaterialDesignIcon.FILE_EXPORT);
        exportIcon.getStyleClass().add("play-icon");
        setCanTakeAllSize(exportBtn);
        setCenterH(exportBtn);
        setCenterV(exportBtn);
        exportBtn.getStyleClass().add("play-btn");
        exportBtn.setGraphic(exportIcon);
        exportBtn.setTranslateY(50.0);
        exportBtn.setOnMouseClicked(e -> this.export());

        boxPane.getChildren().add(playBtn);
        if(export){
            boxPane.getChildren().add(exportBtn);
        }
    }

    private void export() {
        ObjectMapper mapper = new ObjectMapper();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter la configuration");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers JSON", "*.json"));
        fileChooser.setInitialFileName(config.name);
        File file = fileChooser.showSaveDialog(this.panelManager.getStage());

        if (file != null) {
            try {
                mapper.writeValue(file, config);
                Launcher.getInstance().showAlert(Alert.AlertType.INFORMATION, "Exportation", "Configuration exportée avec succès !");
            } catch (IOException e) {
                Launcher.getInstance().showErrorDialog(e, this.panelManager.getStage());
            }
        }
    }

    private void showLaunchButton() {
        boxPane.getChildren().clear();
        Button playBtn = new Button("lancer le jeu");
        MaterialDesignIconView playIcon = new MaterialDesignIconView(MaterialDesignIcon.PLAY);
        playIcon.getStyleClass().add("play-icon");
        setCanTakeAllSize(playBtn);
        setCenterH(playBtn);
        setCenterV(playBtn);
        playBtn.getStyleClass().add("play-btn");
        playBtn.setGraphic(playIcon);
        playBtn.setOnMouseClicked(e -> {
            playBtn.setDisable(true);
            this.startGame();
        });
        boxPane.getChildren().add(playBtn);
    }

    private void play() {
        isDownloading = true;
        boxPane.getChildren().clear();
        setProgress(0, 0);
        boxPane.getChildren().addAll(progressBar, stepLabel, fileLabel);

        Platform.runLater(() -> new Thread(this::update).start());
    }

    private BuildClient client;

    public void update() {
        IProgressCallback callback = new IProgressCallback() {
            private final DecimalFormat decimalFormat = new DecimalFormat("#.#");
            private String stepTxt = "";
            private String percentTxt = "0.0%";

            @Override
            public void step(Step step) {
                Platform.runLater(() -> {
                    stepTxt = CreateClientPanel.StepInfo.valueOf(step.name()).getDetails();
                    setStatus(String.format("%s (%s)", stepTxt, percentTxt));
                });
            }

            @Override
            public void update(DownloadList.DownloadInfo info) {
                Platform.runLater(() -> {
                    percentTxt = decimalFormat.format(info.getDownloadedBytes() * 100.d / info.getTotalToDownloadBytes()) + "%";
                    setStatus(String.format("%s (%s)", stepTxt, percentTxt));
                    setProgress(info.getDownloadedBytes(), info.getTotalToDownloadBytes());
                });
            }

            @Override
            public void onFileDownloaded(Path path) {
                Platform.runLater(() -> {
                    String p = path.toString();
                    fileLabel.setText("..." + p.replace(Launcher.getInstance().getClientDir().resolve(config.name).toFile().getAbsolutePath(), ""));
                });
            }
        };

        try {
            AtomicBoolean buildDownload = new AtomicBoolean(false);
            AtomicBoolean javaDownload = new AtomicBoolean(false);
            Thread build = new Thread(()->{
                client = new BuildClient(config,Launcher.getInstance().getClientDir().resolve(config.name),callback,Boolean.parseBoolean(saver.get("optifine")));
                buildDownload.set(true);
            });
            build.setUncaughtExceptionHandler(new UncaughtExceptionHandler());
            build.start();
            Thread javas = new Thread(()->{
                JavaInstaller javaInstaller = new JavaInstaller(Launcher.getInstance().getLauncherDir().resolve("java"));
                try {
                    javaInstaller.installJava(config.mcinfo.mc.java);
                } catch (IOException e) {
                    Launcher.getInstance().getLogger().printStackTrace(e);
                    Launcher.getInstance().showErrorDialog(e);
                }
                javaDownload.set(true);
            });
            javas.setUncaughtExceptionHandler(new UncaughtExceptionHandler());
            javas.start();
            Thread downloadCheck = new Thread(()->{
                while(!buildDownload.get() || !javaDownload.get()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {}
                }
                isDownloading = false;
                if(Boolean.parseBoolean(saver.get("wait-launch"))) {
                    Platform.runLater(this::showLaunchButton);
                }else{
                    this.startGame();
                }
            });
            downloadCheck.setUncaughtExceptionHandler(new UncaughtExceptionHandler());
            downloadCheck.start();
        } catch (Exception exception) {
            Launcher.getInstance().getLogger().printStackTrace(exception);
            Launcher.getInstance().showErrorDialog(exception,this.panelManager.getStage());
        }
    }

    public void startGame() {
        try {
            client.startGame(config,this.getRamArgsFromSaver(),Launcher.getInstance().getClientDir().resolve(config.name));
            Launcher.presence.largeImageText = "joue à " + config.name;
            Launcher.presence.state = "joue à "  + config.name;
            Launcher.presence.startTimestamp = System.currentTimeMillis() / 1000;
            if(Boolean.parseBoolean(saver.get("autoclose"))) {
                Platform.runLater(Platform::exit);
            }
        } catch (Exception exception) {
            Launcher.getInstance().getLogger().printStackTrace(exception);
            Launcher.getInstance().showErrorDialog(exception,this.panelManager.getStage());
        }
    }

    public String getRamArgsFromSaver() {
        int val = 1024;
        try {
            if (saver.get("maxRam") != null) {
                val = Integer.parseInt(saver.get("maxRam"));
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException error) {
            saver.set("maxRam", String.valueOf(val));
            saver.save();
        }

        return "-Xmx" + val + "M";
    }

    public void setStatus(String status) {
        this.stepLabel.setText(status);
    }

    public void setProgress(double current, double max) {
        this.progressBar.setProgress(current / max);
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public enum StepInfo {
        READ("Lecture du fichier json..."),
        DL_LIBS("Téléchargement des libraries..."),
        DL_ASSETS("Téléchargement des ressources..."),
        EXTRACT_NATIVES("Extraction des natives..."),
        MODS("Téléchargement des mods..."),
        EXTERNAL_FILES("Téléchargement des fichier externes..."),
        POST_EXECUTIONS("Exécution post-installation..."),
        MOD_LOADER("Installation du mod loader..."),
        INTEGRATION("Intégration des mods..."),
        END("Fini !");
        String details;

        StepInfo(String details) {
            this.details = details;
        }

        public String getDetails() {
            return details;
        }
    }

}
