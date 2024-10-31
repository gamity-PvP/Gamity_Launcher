package fr.gamity.launcher.thomas260913.ui.panels.pages.content;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Vanilla extends ContentPanel {
    private final Config.CustomServer config;
    public Vanilla(Config.CustomServer config){
        this.config = config;
        this.config.mcinfo.mc = new Config.CustomServer.McInfo.Mc();
    }
    private final Saver saver = Launcher.getInstance().getSaver();
    GridPane boxPane = new GridPane();
    ProgressBar progressBar = new ProgressBar();
    Label stepLabel = new Label();
    Label fileLabel = new Label();
    ComboBox<String> version = new ComboBox<>();
    ComboBox<String> versionType = new ComboBox<>();
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
        rowConstraints.setMinHeight(175);
        rowConstraints.setMaxHeight(175);
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
        VersionList versionList = new VersionList();

        versionType.getStyleClass().add("version-selector");
        versionType.getItems().addAll("snapshot","release","all");
        versionType.setValue("all");
        setCenterH(versionType);
        setCanTakeAllSize(versionType);
        setTop(versionType);
        versionType.setTranslateY(10d);
        versionList.latest = Launcher.getInstance().getVersionList().latest;

        switch(versionType.getValue()){
            case "all":
                versionList.versions = Launcher.getInstance().getVersionList().versions.stream().filter(version1 -> Objects.equals(version1.type, "snapshot") || Objects.equals(version1.type, "release")).collect(Collectors.toList());
                break;
            case "snapshot":
                versionList.versions = Launcher.getInstance().getVersionList().versions.stream().filter(version1 -> Objects.equals(version1.type, "snapshot")).collect(Collectors.toList());
                break;
            case "release":
                versionList.versions = Launcher.getInstance().getVersionList().versions.stream().filter(version1 -> Objects.equals(version1.type, "release")).collect(Collectors.toList());
                break;
        }
        version.getStyleClass().add("version-selector");
        versionList.versions.forEach(McVersion-> version.getItems().add(McVersion.id));
        version.getItems().add("latest");
        version.setValue("latest");
        setCenterH(version);
        setCanTakeAllSize(version);
        setTop(version);
        version.setTranslateY(60d);

        versionType.valueProperty().addListener((e,old,newValue)->{
            version.getItems().clear();
            switch(newValue){
                case "all":
                    versionList.versions = Launcher.getInstance().getVersionList().versions.stream().filter(version1 -> Objects.equals(version1.type, "snapshot") || Objects.equals(version1.type, "release")).collect(Collectors.toList());
                    break;
                case "snapshot":
                    versionList.versions = Launcher.getInstance().getVersionList().versions.stream().filter(version1 -> Objects.equals(version1.type, "snapshot")).collect(Collectors.toList());
                    break;
                case "release":
                    versionList.versions = Launcher.getInstance().getVersionList().versions.stream().filter(version1 -> Objects.equals(version1.type, "release")).collect(Collectors.toList());
                    break;
            }
            versionList.versions.forEach(McVersion-> version.getItems().add(McVersion.id));
            version.getItems().add("latest");
            version.setValue("latest");
        });

        Button playBtn = new Button("Jouer");
        MaterialDesignIconView playIcon = new MaterialDesignIconView(MaterialDesignIcon.PLAY);
        playIcon.getStyleClass().add("play-icon");
        setCanTakeAllSize(playBtn);
        setCenterH(playBtn);
        setTop(playBtn);
        playBtn.getStyleClass().add("play-btn");
        playBtn.setGraphic(playIcon);
        playBtn.setTranslateY(110.0);
        playBtn.setOnMouseClicked(e -> {
            if(Objects.equals(version.getValue(), "latest")){
                switch(versionType.getValue()){
                    case "all":
                    case "release":
                        config.mcinfo.mc.version = versionList.latest.release;
                        break;
                    case "snapshot":
                        config.mcinfo.mc.version = versionList.latest.snapshot;
                        break;
                }
            }else{
                config.mcinfo.mc.version = version.getValue();
            }
            switch(versionList.versions.stream().filter(version-> Objects.equals(version.id, config.mcinfo.mc.version)).collect(Collectors.toList()).get(0).type){
                case "release":
                    config.mcinfo.type = "vanilla";
                    break;
                case "snapshot":
                    config.mcinfo.type = "snapshot";
                    break;
            }
            config.mcinfo.mc.java = versionList.versions.stream().filter(java-> Objects.equals(java.id, config.mcinfo.mc.version)).collect(Collectors.toList()).get(0).getJavaVersion();
            this.play();
        });
        boxPane.getChildren().addAll(playBtn,version,versionType);
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
        Launcher.getInstance().getLogger().info(config.mcinfo.mc.version);
        IProgressCallback callback = new IProgressCallback() {
            private final DecimalFormat decimalFormat = new DecimalFormat("#.#");
            private String stepTxt = "";
            private String percentTxt = "0.0%";

            @Override
            public void step(Step step) {
                Platform.runLater(() -> {
                    stepTxt = StepInfo.valueOf(step.name()).getDetails();
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
            Launcher.presence.largeImageText = "joue à minecraft vanilla";
            Launcher.presence.details = "joue à minecraft vanilla";
            Launcher.presence.state = "en " + config.mcinfo.mc.version;
            Launcher.presence.startTimestamp = System.currentTimeMillis() / 1000;
            Launcher.lib.Discord_UpdatePresence(Launcher.presence);
            client.startGame(config,this.getRamArgsFromSaver(),Launcher.getInstance().getClientDir().resolve(config.name));
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

