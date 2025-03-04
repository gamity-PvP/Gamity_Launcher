package fr.gamity.launcher.thomas260913.ui.panels.pages;

import fr.flowarg.flowio.FileUtils;
import fr.gamity.launcher.thomas260913.Launcher;
import fr.gamity.launcher.thomas260913.UncaughtExceptionHandler;
import fr.gamity.launcher.thomas260913.ui.PanelManager;
import fr.gamity.launcher.thomas260913.ui.panel.Panel;
import fr.gamity.launcher.thomas260913.game.Parser;
import fr.gamity.launcher.thomas260913.utils.Config;
import fr.gamity.launcher.thomas260913.utils.MCAccount;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class Splash extends Panel {
    Path updaterPath = Launcher.getInstance().getLauncherDir().resolve("updater").resolve("updater.jar");
    Saver saver = Launcher.getInstance().getSaver();
    GridPane background = new GridPane();
    GridPane boxPane = new GridPane();
    ImageView alu = new ImageView(new Image("images/blocks/aluminium.png"));
    ImageView copper = new ImageView(new Image("images/blocks/copper.png"));
    ImageView chromium = new ImageView(new Image("images/blocks/chromium.png"));
    ImageView gamity = new ImageView(new Image("images/blocks/gamity.png"));
    ImageView platium = new ImageView(new Image("images/blocks/platium.png"));
    ImageView point1 = new ImageView(new Image("images/point.png"));
    ImageView point2 = new ImageView(new Image("images/point.png"));
    ImageView point3 = new ImageView(new Image("images/point.png"));
    ImageView trou_noir = new ImageView(new Image("images/trou_noir.png"));
    boolean trou_noir_visible = false;
    boolean datafinish = false;
    boolean loginfinish = false;
    ImageView trou_noir2 = new ImageView(new Image("images/trou_noir.png"));
    final double pointY = 98.0;

    ProgressBar progressBar1 = new ProgressBar();
    Label stepLabel1 = new Label();
    ProgressBar progressBar2 = new ProgressBar();
    Label stepLabel2 = new Label();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getStylesheetPath() {
        return "css/splash.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        GridPane bgImage = new GridPane();
        setCanTakeAllSize(bgImage);
        bgImage.getStyleClass().add("bg-image");
        this.background.add(bgImage, 1, 0);
        setCanTakeAllSize(background);
        background.setPadding(new Insets(0));
        setCenterH(background);
        setCenterV(background);
        this.layout.add(background, 0, 0);

        boxPane.getStyleClass().add("box-pane");
        setCanTakeAllSize(boxPane);
        boxPane.setMinHeight(450);
        boxPane.setMaxHeight(450);
        boxPane.setMinWidth(500);
        boxPane.setMaxWidth(500);
        setCenterH(boxPane);
        setCenterV(boxPane);
        this.layout.add(boxPane, 0, 0);

        Label title = new Label("Chargement");
        setCanTakeAllSize(title);
        setCenterH(title);
        setTop(title);
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 35f));
        title.setTranslateX(-35.0);
        title.setTranslateY(70.0);

        setCanTakeAllSize(point1);
        setCenterH(point1);
        setTop(point1);
        point1.setTranslateX(75.0);
        point1.setTranslateY(pointY);
        point1.setFitHeight(5d);
        point1.setPreserveRatio(true);

        setCanTakeAllSize(point2);
        setCenterH(point2);
        setTop(point2);
        point2.setTranslateX(90.0);
        point2.setTranslateY(pointY);
        point2.setFitHeight(5d);
        point2.setPreserveRatio(true);

        setCanTakeAllSize(point3);
        setCenterH(point3);
        setTop(point3);
        point3.setTranslateX(105.0);
        point3.setTranslateY(pointY);
        point3.setFitHeight(5d);
        point3.setPreserveRatio(true);

        setupImageView(trou_noir);
        setupImageView(trou_noir2);
        setupImageView(alu);
        setupImageView(copper);
        setupImageView(chromium);
        setupImageView(gamity);
        setupImageView(platium);
        trou_noir.setTranslateX(-15.076410598336468);
        trou_noir.setTranslateY(-9.4207869197014);

        trou_noir2.setTranslateX(200.0);
        trou_noir2.setTranslateY(0.0);

        progressBar1.getStyleClass().add("download-progress");
        stepLabel1.getStyleClass().add("download-status");
        progressBar2.getStyleClass().add("download-progress");
        stepLabel2.getStyleClass().add("download-status");

        progressBar1.setTranslateY(5);
        setCenterH(progressBar1);
        setTop(progressBar1);
        setCanTakeAllWidth(progressBar1);

        stepLabel1.setTranslateY(15);
        setCenterH(stepLabel1);
        setTop(stepLabel1);
        setCanTakeAllSize(stepLabel1);

        progressBar2.setTranslateY(45);
        setCenterH(progressBar2);
        setTop(progressBar2);
        setCanTakeAllWidth(progressBar2);

        stepLabel2.setTranslateY(55);
        setCenterH(stepLabel2);
        setTop(stepLabel2);
        setCanTakeAllSize(stepLabel2);

        boxPane.getChildren().addAll(trou_noir, trou_noir2, alu, copper, chromium, gamity, platium, title, point1, point2, point3, progressBar1, stepLabel1, progressBar2, stepLabel2);

        loadData();
        loadAccount();
        Thread thread = getThread();
        thread.start();
    }

    private @NotNull Thread getThread() {
        Thread thread = new Thread(() -> {
            boolean finish = false;
            while (!finish) {
                if(loginfinish && datafinish){
                    finish = true;
                    Platform.runLater(() -> {
                        if (Launcher.getInstance().isUserAlreadyLoggedIn()) {
                            logger.info("Hello " + Launcher.getInstance().getMCAccount(Integer.parseInt(saver.get("selectAccount"))).getAuthInfos().getUsername());
                            this.panelManager.showPanel(new App());
                        } else {
                            this.panelManager.showPanel(new Login());
                        }
                    });
                }else{
                    try {
                        getThread().wait(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler());
        return thread;
    }

    private void setupImageView(ImageView imageView) {
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(30d);
        setCenterH(imageView);
        setCenterV(imageView);
        setCanTakeAllSize(imageView);
        imageView.setVisible(false);
    }

    @Override
    public void onShow() {
        Timeline chargement = new Timeline();
        Timeline timeline = new Timeline();
        int chframes = 240;
        int frames = 360 + 180 + 45 + 248;
        double radius = 200.0;
        double centerX = 0;
        double centerY = 0;

        for (int i = 0; i < chframes; i++) {
            KeyFrame keyFrame = getKeyFrame(i, chframes);

            chargement.getKeyFrames().add(keyFrame);
        }

        // Create a KeyFrame for visibility reset at the beginning of each cycle
        KeyFrame resetVisibility = new KeyFrame(Duration.ZERO, event -> {
            trou_noir_visible = false;
            alu.setVisible(false);
            copper.setVisible(false);
            chromium.setVisible(false);
            gamity.setVisible(false);
            platium.setVisible(false);
        });
        timeline.getKeyFrames().add(resetVisibility);

        for (int i = 0; i < frames; i++) {
            final int frame = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis((6000.0 / frames) * i), event -> {
                if (frame < 124) {
                    trou_noir2.setVisible(true);
                    trou_noir2.setFitHeight(frame);
                } else {
                    int frame2 = frame - 124;
                    if (frame2 >= 180 && frame2 < 304) {
                        trou_noir2.setFitHeight(304 - frame2);
                    } else if (frame2 >= 304) {
                        trou_noir2.setVisible(false);
                    }
                    trou_noir.setVisible(trou_noir_visible);
                    if (trou_noir_visible && (frame2 >= 300 && frame2 <= 362)) {
                        trou_noir.setFitHeight((frame2 - 300) * 2);
                    }
                    double angle = Math.toRadians(frame2);
                    double currentRadius = radius - (frame2 / 1.8);
                    double x = centerX + currentRadius * Math.cos(angle);
                    double y = centerY + currentRadius * Math.sin(angle);
                    if (frame2 < 180) {
                        alu.setVisible(true);
                    }
                    updatePosition(alu, x, y, frame2);

                    updatePositionWithDelay(copper, frame2, 45);
                    updatePositionWithDelay(chromium, frame2, 90);
                    updatePositionWithDelay(gamity, frame2, 135);
                    updatePositionWithDelay(platium, frame2, 180);
                }
            });
            if (frame == frames - 1) {
                for (int j = 124; j > 0; j--) {
                    final int frame2 = j;
                    KeyFrame keyFrame2 = new KeyFrame(Duration.millis(((6000.0 / frames) * i) + ((1000.0 / 124.0) * (124 - j))), event -> trou_noir.setFitHeight(frame2));
                    timeline.getKeyFrames().add(keyFrame2);
                }
            }
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        chargement.setCycleCount(Timeline.INDEFINITE);
        chargement.play();
    }

    private @NotNull KeyFrame getKeyFrame(int i, int chframes) {
        final int frame = i;
        return new KeyFrame(Duration.millis((2000.0 / chframes) * i), event -> {
            if (frame < 60) {
                point1.setTranslateY(pointY - ((frame / 60.0) * 15.0));
            } else if (frame < 120) {
                point1.setTranslateY((pointY - 15) + (((frame - 60) / 60.0) * 15.0));
                point2.setTranslateY(pointY - (((frame - 60) / 60.0) * 15.0));
            } else if (frame < 180) {
                point2.setTranslateY((pointY - 15) + (((frame - 120) / 60.0) * 15.0));
                point3.setTranslateY(pointY - (((frame - 120) / 60.0) * 15.0));
            } else {
                point3.setTranslateY((pointY - 15) + (((frame - 180) / 60.0) * 15.0));
            }
        });
    }


    private void updatePosition(ImageView imageView, double x, double y, int frame) {
        imageView.setTranslateX(x);
        imageView.setTranslateY(y);
        if (frame == 300) {
            trou_noir_visible = true;
        }
        if (x == -15.076410598336468 && y == -9.4207869197014) {
            imageView.setVisible(false);
        }
    }

    private void updatePositionWithDelay(ImageView imageView, int frame, int delay) {
        if (frame >= delay) {
            double angle = Math.toRadians(frame - delay);
            double currentRadius = 200.0 - ((frame - delay) / 1.8);
            double x = currentRadius * Math.cos(angle);
            double y = currentRadius * Math.sin(angle);
            updatePosition(imageView, x, y, frame);
            if (frame < 360) {
                imageView.setVisible(true);
            }
        }
    }

    public void loadData() {
        Thread thread = new Thread(() -> {
            try {
                Launcher.getInstance().getLogger().info("fetching version info");
                Platform.runLater(() -> {
                    stepLabel1.setText("fetching version info");
                    setProgress1(0.0, 3.0);
                });
                Launcher.getInstance().setVersionList(new Parser.VersionParser().getVersion());
                Platform.runLater(() -> {
                    stepLabel1.setText("finish parsing version");
                    setProgress1(1.0, 3.0);
                });
                Launcher.getInstance().getLogger().info("finish parsing version");
                Launcher.getInstance().getLogger().info("fetching optifine info");
                Platform.runLater(() -> {
                    stepLabel1.setText("fetching optifine info");
                    setProgress1(1.0, 3.0);
                });
                Launcher.getInstance().setOptifineList(new Parser.OptifineParser().OptifineRequest("all", "all", false));
                Platform.runLater(() -> {
                    stepLabel1.setText("finish optifine version");
                    setProgress1(2.0, 3.0);
                });
                Launcher.getInstance().getLogger().info("finish optifine version");
                Launcher.getInstance().getLogger().info("checking for launcher update");
                Platform.runLater(() -> {
                    stepLabel1.setText("finishing loading");
                    setProgress1(3.0, 3.0);
                });
                try {
                    Config datas = fr.gamity.launcher.thomas260913.utils.Parser.appInfo.parseJsonURL("https://gamity-pvp.fr/apis/launcher/info/updater/json");
                    if (Files.notExists(this.updaterPath) || !FileUtils.getSHA1(this.updaterPath).equalsIgnoreCase(datas.sha1)) {
                        Launcher.getInstance().getLogger().info("update for updater found");
                        if (Files.notExists(this.updaterPath.getParent())) {
                            Launcher.getInstance().getLogger().info("creating directory for updater");
                            Files.createDirectory(this.updaterPath.getParent());
                        }
                        Launcher.getInstance().getLogger().info("downloading new updater");
                        Files.copy(new URL(datas.URL).openStream(), this.updaterPath, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        Launcher.getInstance().getLogger().info("updater is up to date");
                    }
                } catch (Exception ex) {
                    Launcher.getInstance().getLogger().printStackTrace(ex);
                }
                Platform.runLater(() -> {
                    stepLabel1.setText("finish loading");
                    setProgress1(3.0, 3.0);
                });
                datafinish = true;
            } catch (Exception ex) {
                Launcher.getInstance().showErrorDialog(ex, this.panelManager.getStage());
                Launcher.getInstance().getLogger().printStackTrace(ex);
            }
        });
        thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler());
        thread.start();
    }

    public void loadAccount() {
        Thread loadAccount = new Thread(() -> {
            Launcher.getInstance().getLogger().info("Starting load accounts ...");
            for (int i = 0; i < Launcher.getInstance().getMaxAccount(); i++) {
                int finalI = i;
                Platform.runLater(() -> {
                    stepLabel2.setText("load Accounts ...");
                    setProgress2(finalI, Launcher.getInstance().getMaxAccount());
                });
                try {
                    if (saver.get("msAccessToken" + i) != null && saver.get("msRefreshToken" + i) != null) {
                        try {
                            try {
                                MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                                MicrosoftAuthResult response = authenticator.loginWithRefreshToken(saver.get("msRefreshToken" + i));

                                saver.set("msAccessToken" + i, response.getAccessToken());
                                saver.set("msRefreshToken" + i, response.getRefreshToken());
                                saver.save();
                                Launcher.getInstance().addMCAccount(new MCAccount(new AuthInfos(
                                        response.getProfile().getName(),
                                        response.getAccessToken(),
                                        response.getProfile().getId(),
                                        response.getXuid(),
                                        response.getClientId()
                                ), false), i);
                            } catch (MicrosoftAuthenticationException e) {
                                saver.remove("msAccessToken" + i);
                                saver.remove("msRefreshToken" + i);
                                for (int j = i; j < Launcher.getInstance().getMaxAccount(); j++) {
                                    if (saver.get("msRefreshToken" + j) != null && saver.get("msAccessToken" + j) != null) {
                                        saver.set("msAccessToken" + (j - 1), saver.get("msAccessToken" + j));
                                        saver.set("msRefreshToken" + (j - 1), saver.get("msRefreshToken" + j));
                                        saver.remove("msRefreshToken" + j);
                                        saver.remove("msAccessToken" + j);
                                    } else if (saver.get("offline-username" + j) != null) {
                                        saver.set("offline-username" + (j - 1), saver.get("offline-username" + j));
                                        saver.remove("offline-username" + j);
                                    }
                                }
                                saver.save();
                            }
                        } catch (Exception ex) {
                            Launcher.getInstance().showErrorDialog(ex, this.panelManager.getStage());
                            Launcher.getInstance().getLogger().printStackTrace(ex);
                        }
                        Thread.sleep(2000);
                        Launcher.getInstance().getLogger().info("account " + Launcher.getInstance().getMCAccount(i).getAuthInfos().getUsername() + " load");
                    } else if (saver.get("offline-username" + i) != null) {
                        Launcher.getInstance().addMCAccount(new MCAccount(new AuthInfos(saver.get("offline-username" + i), UUID.randomUUID().toString(), UUID.randomUUID().toString()), true), i);
                        Launcher.getInstance().getLogger().info("account " + Launcher.getInstance().getMCAccount(i).getAuthInfos().getUsername() + " load");
                    }
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }
            }
            try {
                Platform.runLater(() -> {
                    stepLabel2.setText("finish load accounts");
                    setProgress2(Launcher.getInstance().getMaxAccount(), Launcher.getInstance().getMaxAccount());
                });
                Launcher.getInstance().getLogger().info("finish load accounts");
            } catch (Exception ex) {
                Launcher.getInstance().showErrorDialog(ex, this.panelManager.getStage());
                Launcher.getInstance().getLogger().printStackTrace(ex);
            }
            loginfinish = true;
        });
        loadAccount.setUncaughtExceptionHandler(new UncaughtExceptionHandler());
        loadAccount.start();
    }

    public void setProgress1(double current, double max) {
        progressBar1.setProgress(current / max);
    }

    public void setProgress2(double current, double max) {
        progressBar2.setProgress(current / max);
    }
}