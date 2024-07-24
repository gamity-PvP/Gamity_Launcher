package com.gamity.launcher.thomas260913.ui.panels.pages;

import com.gamity.launcher.thomas260913.Launcher;
import com.gamity.launcher.thomas260913.ui.PanelManager;
import com.gamity.launcher.thomas260913.ui.panel.Panel;
import fr.flowarg.azuljavadownloader.AzulJavaBuildInfo;
import fr.flowarg.azuljavadownloader.AzulJavaDownloader;
import fr.flowarg.azuljavadownloader.AzulJavaType;
import fr.flowarg.azuljavadownloader.RequestedJavaInfo;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.animation.Animation;
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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class splash extends Panel {
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
    boolean javafinish = false;
    boolean loginfinish = false;
    ImageView trou_noir2 = new ImageView(new Image("images/trou_noir.png"));
    final double pointY = 58.0;

    ProgressBar progressBar = new ProgressBar();
    Label stepLabel = new Label();

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
        title.setTranslateY(30.0);

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

        progressBar.setTranslateY(-15);
        setCenterH(progressBar);
        setCanTakeAllWidth(progressBar);

        stepLabel.setTranslateY(5);
        setCenterH(stepLabel);
        setCanTakeAllSize(stepLabel);

        boxPane.getChildren().addAll(trou_noir,trou_noir2,alu, copper, chromium, gamity, platium,title,point1,point2,point3,progressBar,stepLabel);

        downloadJava();
        loadAccount();
        new Thread(()->{
            while(!loginfinish && !javafinish){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
            Platform.runLater(()->{
                if (Launcher.getInstance().isUserAlreadyLoggedIn()) {
                    logger.info("Hello " + Launcher.getInstance().getAuthInfos(Integer.parseInt(saver.get("selectAccount"))).getUsername());
                    this.panelManager.showPanel(new App());
                } else {
                    this.panelManager.showPanel(new Login());
                }
            });
        });
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

        for(int i = 0; i < chframes; i++){
            final int frame = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis((2000.0 / chframes) * i), event -> {
                if(frame < 60){
                    point1.setTranslateY(pointY - ((frame/60.0)*15.0));
                }else if(frame < 120){
                    point1.setTranslateY((pointY - 15) + (((frame-60)/60.0)*15.0));
                    point2.setTranslateY(pointY - (((frame-60)/60.0)*15.0));
                }else if(frame < 180){
                    point2.setTranslateY((pointY - 15) + (((frame-120)/60.0)*15.0));
                    point3.setTranslateY(pointY - (((frame-120)/60.0)*15.0));
                }else {
                    point3.setTranslateY((pointY - 15) + (((frame-180)/60.0)*15.0));
                }
            });

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
                if(frame < 124){
                    trou_noir2.setVisible(true);
                    trou_noir2.setFitHeight(frame);
                }else{
                    int frame2 = frame - 124;
                    if(frame2 >= 180 && frame2 < 304) {
                        trou_noir2.setFitHeight(304-frame2);
                    }else if (frame2 >=304){
                        trou_noir2.setVisible(false);
                    }
                    trou_noir.setVisible(trou_noir_visible);
                    if(trou_noir_visible && (frame2 >= 300 && frame2 <= 362)){
                        trou_noir.setFitHeight((frame2 - 300)*2);
                    }
                    double angle = Math.toRadians(frame2);
                    double currentRadius = radius - (frame2 / 1.8);
                    double x = centerX + currentRadius * Math.cos(angle);
                    double y = centerY + currentRadius * Math.sin(angle);
                    if(frame2 < 180){
                        alu.setVisible(true);
                    }
                    updatePosition(alu, x, y,frame2);

                    updatePositionWithDelay(copper, frame2, 45);
                    updatePositionWithDelay(chromium, frame2, 90);
                    updatePositionWithDelay(gamity, frame2, 135);
                    updatePositionWithDelay(platium, frame2, 180);
                }
            });
            if(frame == frames - 1){
                for(int j=124; j > 0; j--){
                    final int frame2 = j;
                    KeyFrame keyFrame2 = new KeyFrame(Duration.millis(((6000.0 / frames) * i)+((1000.0 / 124.0)*(124-j))), event ->{
                        trou_noir.setFitHeight(frame2);
                    });
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


    private void updatePosition(ImageView imageView, double x, double y,int frame) {
        imageView.setTranslateX(x);
        imageView.setTranslateY(y);
        if(frame == 300) {
            trou_noir_visible = true;
        }
        if(x == -15.076410598336468 && y == -9.4207869197014) {
            imageView.setVisible(false);
        }
    }

    private void updatePositionWithDelay(ImageView imageView, int frame, int delay) {
        if (frame >= delay) {
            double angle = Math.toRadians(frame - delay);
            double currentRadius = 200.0 - ((frame - delay) / 1.8);
            double x = currentRadius * Math.cos(angle);
            double y = currentRadius * Math.sin(angle);
            updatePosition(imageView, x, y,frame);
            if(frame < 360){
                imageView.setVisible(true);
            }
        }
    }
    public void downloadJava(){
        new Thread(()->{
            try {
                Launcher.getInstance().getLogger().info("Starting download java 21...");
                final AzulJavaDownloader downloader1 = new AzulJavaDownloader();
                final Path javas1 = Paths.get(Launcher.getInstance().getLauncherDir().toFile().getAbsolutePath() + "/java"); // The directory where the Java versions will be downloaded.
                final AzulJavaBuildInfo buildInfoWindows1 = downloader1.getBuildInfo(new RequestedJavaInfo("21", AzulJavaType.JDK, "windows", "x64", true)); // jdk 21 with javafx for Windows 64 bits
                final Path javaHomeWindows1 = downloader1.downloadAndInstall(buildInfoWindows1, javas1);
                Launcher.getInstance().setJava21(javaHomeWindows1);
                Launcher.getInstance().getLogger().info("Finish download java 21");
                Launcher.getInstance().getLogger().info("Starting download java 17 ...");

                final AzulJavaDownloader downloader2 = new AzulJavaDownloader();
                final Path javas2 = Paths.get(Launcher.getInstance().getLauncherDir().toFile().getAbsolutePath() + "/java"); // The directory where the Java versions will be downloaded.
                final AzulJavaBuildInfo buildInfoWindows2 = downloader2.getBuildInfo(new RequestedJavaInfo("17", AzulJavaType.JDK, "windows", "x64", true)); // jdk 17 with javafx for Windows 64 bits
                final Path javaHomeWindows2 = downloader2.downloadAndInstall(buildInfoWindows2, javas2);
                Launcher.getInstance().setJava17(javaHomeWindows2);
                Launcher.getInstance().getLogger().info("Finish download java 17");
                Launcher.getInstance().getLogger().info("Starting download java 8 ...");

                final AzulJavaDownloader downloader3 = new AzulJavaDownloader();
                final Path javas3 = Paths.get(Launcher.getInstance().getLauncherDir().toFile().getAbsolutePath() + "/java"); // The directory where the Java versions will be downloaded.
                final AzulJavaBuildInfo buildInfoWindows3 = downloader3.getBuildInfo(new RequestedJavaInfo("8", AzulJavaType.JDK, "windows", "x64", true)); // jdk 8 with javafx for Windows 64 bits
                final Path javaHomeWindows3 = downloader3.downloadAndInstall(buildInfoWindows3, javas3);
                Launcher.getInstance().setJava8(javaHomeWindows3);
                Launcher.getInstance().getLogger().info("Finish download java 8");
                javafinish = true;
            }catch(Exception ex){
                Launcher.getInstance().showErrorDialog(ex,this.panelManager.getStage());
            }
        }).start();
    }
    public void loadAccount(){
    Thread loadAccount = new Thread(()->{
        for(int i = 0;i < Launcher.getInstance().getMaxAccount();i++){
            try{
                if (saver.get("msAccessToken" + i) != null && saver.get("msRefreshToken" + i) != null) {
                    try {
                        try {
                            MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                            MicrosoftAuthResult response = authenticator.loginWithRefreshToken(saver.get("msRefreshToken" + i));

                            saver.set("msAccessToken" + i, response.getAccessToken());
                            saver.set("msRefreshToken" + i, response.getRefreshToken());
                            saver.save();
                            Launcher.getInstance().addAuthInfos(new AuthInfos(
                                    response.getProfile().getName(),
                                    response.getAccessToken(),
                                    response.getProfile().getId(),
                                    response.getXuid(),
                                    response.getClientId()
                            ), i);
                        } catch (MicrosoftAuthenticationException e) {
                            saver.remove("msAccessToken" + i);
                            saver.remove("msRefreshToken" + i);
                            saver.save();
                        }
                    }catch(Exception ex){
                        Launcher.getInstance().showErrorDialog(ex,this.panelManager.getStage());
                    }
                    Thread.sleep(2000);
                } else if (saver.get("offline-username" + i) != null) {
                    Launcher.getInstance().addAuthInfos(new AuthInfos(saver.get("offline-username" + i), UUID.randomUUID().toString(), UUID.randomUUID().toString()),i);
                }
            } catch (InterruptedException ignored) {}
        }
        loginfinish = true;
    });
    loadAccount.start();
}
}