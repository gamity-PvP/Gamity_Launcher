package fr.gamity.launcher.thomas260913.ui.panels.pages.content;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gamity.launcher.thomas260913.Launcher;
import fr.gamity.launcher.thomas260913.ui.PanelManager;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.Step;
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
import java.net.URL;
import java.nio.file.Path;
import java.text.DecimalFormat;

public class vanilla extends ContentPanel {
    private final Config config;
    public vanilla(Config config){
        this.config = config;
        this.config.mcinfo.mc = new Config.McInfo.Mc();
    }
    private final Saver saver = Launcher.getInstance().getSaver();
    GridPane boxPane = new GridPane();
    ProgressBar progressBar = new ProgressBar();
    Label stepLabel = new Label();
    Label fileLabel = new Label();
    ComboBox<String> version = new ComboBox<>();
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
        rowConstraints.setMinHeight(125);
        rowConstraints.setMaxHeight(125);
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
        try {
            boxPane.getChildren().clear();

            ObjectMapper objectMapper = new ObjectMapper();
            Version versionList = objectMapper.readValue(new URL("https://gamity-pvp.fr/apis/launcher/mcversion/list.json"), Version.class);

            version.getStyleClass().add("version-selector");
            versionList.getVersionList().forEach(McVersion-> version.getItems().add(McVersion));
            version.getItems().add("latest");
            version.setValue("latest");
            setCenterH(version);
            setCanTakeAllSize(version);
            setTop(version);
            version.setTranslateY(10d);

            Button playBtn = new Button("Jouer");
            MaterialDesignIconView playIcon = new MaterialDesignIconView(MaterialDesignIcon.PLAY);
            playIcon.getStyleClass().add("play-icon");
            setCanTakeAllSize(playBtn);
            setCenterH(playBtn);
            setCenterV(playBtn);
            playBtn.getStyleClass().add("play-btn");
            playBtn.setGraphic(playIcon);
            playBtn.setTranslateY(25.0);
            playBtn.setOnMouseClicked(e -> {
                if(version.getValue() == "latest"){
                    config.mcinfo.mc.version = versionList.getLatest();
                }else{
                    config.mcinfo.mc.version = version.getValue();
                }
                if(Integer.parseInt(config.mcinfo.mc.version.split("\\.")[1]) < 18){
                    config.mcinfo.mc.java = "8";
                }else if(((Integer.parseInt(config.mcinfo.mc.version.split("\\.")[1]) >= 18) && (Integer.parseInt(config.mcinfo.mc.version.split("\\.")[1]) <= 20)) || ((Integer.parseInt(config.mcinfo.mc.version.split("\\.")[1]) == 20) && (Integer.parseInt(config.mcinfo.mc.version.split("\\.")[2]) <= 4))){
                    config.mcinfo.mc.java = "17";
                }else{
                    config.mcinfo.mc.java = "21";
                }
                this.play();
            });
            boxPane.getChildren().addAll(playBtn,version);
        } catch (IOException e) {
            e.printStackTrace();
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
            client = new BuildClient(config,Launcher.getInstance().getClientDir().resolve(config.name),callback);
            isDownloading = false;
            if(Boolean.parseBoolean(saver.get("wait-launch"))) {
                Platform.runLater(this::showLaunchButton);
            }else{
                this.startGame();
            }
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

