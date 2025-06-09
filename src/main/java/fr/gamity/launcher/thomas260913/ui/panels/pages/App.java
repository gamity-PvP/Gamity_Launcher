package fr.gamity.launcher.thomas260913.ui.panels.pages;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gamity.launcher.thomas260913.Launcher;
import fr.gamity.launcher.thomas260913.game.Config;
import fr.gamity.launcher.thomas260913.ui.PanelManager;
import fr.gamity.launcher.thomas260913.ui.panel.Panel;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import fr.gamity.launcher.thomas260913.ui.panels.pages.content.*;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class App extends Panel {
    GridPane sidemenu = new GridPane();
    GridPane navContent = new GridPane();
    Node activeLink = null;
    ContentPanel currentPage = null;
    Button homeBtn, settingsBtn, clientBtn;
    Saver saver = Launcher.getInstance().getSaver();
    public static String iconSize = "16px";
    private Config.ServerList serverList;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getStylesheetPath() {
        return "css/app.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            serverList = objectMapper.readValue(new URL(saver.get("weblink") + "/apis/launcher/servers/list.json"), Config.ServerList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Background
        this.layout.getStyleClass().add("app-layout");
        setCanTakeAllSize(this.layout);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setMinWidth(300);
        columnConstraints.setMaxWidth(300);
        this.layout.getColumnConstraints().addAll(columnConstraints, new ColumnConstraints());

        // Side menu
        this.layout.add(sidemenu, 0, 0);
        sidemenu.getStyleClass().add("sidemenu");
        setLeft(sidemenu);
        setCenterH(sidemenu);
        setCenterV(sidemenu);

        // Background Image
        GridPane bgImage = new GridPane();
        setCanTakeAllSize(bgImage);
        bgImage.getStyleClass().add("bg-image");
        this.layout.add(bgImage, 1, 0);

        // Nav content
        this.layout.add(navContent, 1, 0);
        navContent.getStyleClass().add("nav-content");
        setLeft(navContent);
        setCenterH(navContent);
        setCenterV(navContent);

        /*
         * Side menu
         */
        // Titre
        ImageView baniere = new ImageView(new Image("images/baniere.png"));
        baniere.setPreserveRatio(true);
        baniere.setFitHeight(40d);
        baniere.getStyleClass().add("home-title");
        setCenterH(baniere);
        setCanTakeAllSize(baniere);
        setTop(baniere);
        baniere.setTranslateY(30d);
        sidemenu.getChildren().add(baniere);

        // Navigation
        homeBtn = new Button("Accueil");
        homeBtn.getStyleClass().add("sidemenu-nav-btn");
        MaterialDesignIconView icon2 = new MaterialDesignIconView(MaterialDesignIcon.HOME);
        icon2.setSize(iconSize);
        homeBtn.setGraphic(icon2);
        setCanTakeAllSize(homeBtn);
        setTop(homeBtn);
        homeBtn.setTranslateY(90d);
        homeBtn.setOnMouseClicked(e -> {
            setPage(new Home(), homeBtn);
            Launcher.presence.largeImageText = "Dans le launcher";
            Launcher.presence.state = "Dans le launcher";
            Launcher.presence.details = "";
            Launcher.lib.Discord_UpdatePresence(Launcher.presence);
        });

        settingsBtn = new Button("Paramètres");
        settingsBtn.getStyleClass().add("sidemenu-nav-btn");
        MaterialDesignIconView icon3 = new MaterialDesignIconView(MaterialDesignIcon.SETTINGS);
        icon3.setSize(iconSize);
        settingsBtn.setGraphic(icon3);
        setCanTakeAllSize(settingsBtn);
        setTop(settingsBtn);
        settingsBtn.setTranslateY(140d);
        settingsBtn.setOnMouseClicked(e -> {
            setPage(new Settings(), settingsBtn);
            Launcher.presence.state = "Dans le launcher";
            Launcher.presence.largeImageText = "Dans le launcher";
            Launcher.presence.details = "";
            Launcher.lib.Discord_UpdatePresence(Launcher.presence);
        });
        AtomicInteger index = new AtomicInteger(0);
        int buttonHeight = 40;
        int spacing = 10;
        serverList.getServers().forEach(server -> {
            int i = index.getAndIncrement();
            Button panelBtn = new Button(server.getName());
            panelBtn.getStyleClass().add("sidemenu-nav-btn");
            MaterialDesignIconView icon1 = new MaterialDesignIconView(MaterialDesignIcon.SERVER);
            icon1.setSize(iconSize);
            panelBtn.setGraphic(icon1);
            setCanTakeAllSize(panelBtn);
            setTop(panelBtn);
            double translateY1 = i * (buttonHeight + spacing);
            double translateY2 = translateY1 + 190d;
            panelBtn.setTranslateY(translateY2);
            panelBtn.setOnMouseClicked(e -> {
                try {
                    setPage(new CreateClientPanel(server.getConfig()), panelBtn);
                } catch (Exception ex) {
                    Launcher.getInstance().getLogger().printStackTrace(ex);
                    Launcher.getInstance().showErrorDialog(ex,this.panelManager.getStage());
                }
                Launcher.presence.largeImageText = "Dans le launcher";
                Launcher.presence.state = "Dans le launcher";
                Launcher.presence.details = "";
                Launcher.lib.Discord_UpdatePresence(Launcher.presence);
            });
            sidemenu.getChildren().add(panelBtn);
        });
        MaterialDesignIconView icon1 = new MaterialDesignIconView(MaterialDesignIcon.SERVER);
        icon1.setSize(iconSize);
        clientBtn = new Button("Custom");
        clientBtn.getStyleClass().add("sidemenu-nav-btn");
        clientBtn.setGraphic(icon1);
        setCanTakeAllSize(clientBtn);
        setTop(clientBtn);
        int i = index.getAndIncrement();
        double translateY1 = i * (buttonHeight + spacing);
        double translateY2 = translateY1 + 190d;
        clientBtn.setTranslateY(translateY2);
        clientBtn.setOnMouseClicked(e -> {
            setPage(new JsonClient(), clientBtn);
            Launcher.presence.largeImageText = "Dans le launcher";
            Launcher.presence.state = "Dans le launcher";
            Launcher.presence.details = "";
            Launcher.lib.Discord_UpdatePresence(Launcher.presence);
        });

        sidemenu.getChildren().addAll(homeBtn, settingsBtn, clientBtn);


        if (Launcher.getInstance().getMCAccount() != null) {
            // Pseudo + avatar
            GridPane userPane = new GridPane();
            setCanTakeAllWidth(userPane);
            userPane.setMaxHeight(80);
            userPane.setMinWidth(80);
            userPane.getStyleClass().add("user-pane");
            setBottom(userPane);

            String avatarUrl = "https://minotar.net/avatar/" + Launcher.getInstance().getMCAccount().getAuthInfos().getUuid() + ".png";
            ImageView avatarView = new ImageView();
            Image avatarImg = new Image(avatarUrl);
            avatarView.setImage(avatarImg);
            avatarView.setPreserveRatio(true);
            avatarView.setFitHeight(50d);
            setCenterV(avatarView);
            setCanTakeAllSize(avatarView);
            setLeft(avatarView);
            avatarView.setTranslateX(15d);
            userPane.getChildren().add(avatarView);

            Label usernameLabel = new Label(Launcher.getInstance().getMCAccount().getAuthInfos().getUsername());
            usernameLabel.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
            setCanTakeAllSize(usernameLabel);
            setCenterV(usernameLabel);
            setLeft(usernameLabel);
            usernameLabel.getStyleClass().add("username-label");
            usernameLabel.setTranslateX(75d);
            setCanTakeAllWidth(usernameLabel);
            userPane.getChildren().add(usernameLabel);

            Button logoutBtn = new Button();
            MaterialDesignIconView logoutIcon = new MaterialDesignIconView(MaterialDesignIcon.LOGOUT);
            logoutIcon.setSize("20px");
            logoutIcon.getStyleClass().add("logout-icon");
            setCanTakeAllSize(logoutBtn);
            setCenterV(logoutBtn);
            setRight(logoutBtn);
            logoutBtn.getStyleClass().add("logout-btn");
            logoutBtn.setGraphic(logoutIcon);
            logoutBtn.setOnMouseClicked(e -> {
                if (currentPage instanceof CreateClientPanel && ((CreateClientPanel) currentPage).isDownloading()) {
                    return;
                }
                if(JsonClient.getLaunching()){
                    return;
                }
                saver.remove("offline-username" + saver.get("selectAccount"));
                saver.remove("msAccessToken" + saver.get("selectAccount"));
                saver.remove("msRefreshToken" + saver.get("selectAccount"));
                Launcher.getInstance().rmMCAccount();
                if (Launcher.getInstance().getMCAccountSize() > 0) {
                    this.panelManager.showPanel(new SelectAccount());
                } else {
                    this.panelManager.showPanel(new Login());
                }
                saver.save();

            });
            userPane.getChildren().add(logoutBtn);

            sidemenu.getChildren().add(userPane);
        } else {
            this.panelManager.showPanel(new SelectAccount());
        }
    }

    @Override
    public void onShow() {
        super.onShow();
        setPage(new Home(), homeBtn);
    }

    public void setPage(ContentPanel panel, Node navButton) {
        if (currentPage instanceof CreateClientPanel && ((CreateClientPanel) currentPage).isDownloading()) {
            return;
        }
        if(JsonClient.getLaunching()){
            return;
        }

        if (activeLink != null)
            activeLink.getStyleClass().remove("active");
        activeLink = navButton;
        activeLink.getStyleClass().add("active");

        this.navContent.getChildren().clear();
        if (panel != null) {
            this.navContent.getChildren().add(panel.getLayout());
            currentPage = panel;
            if (panel.getStylesheetPath() != null) {
                this.panelManager.getStage().getScene().getStylesheets().clear();
                this.panelManager.getStage().getScene().getStylesheets().addAll(
                        this.getStylesheetPath(),
                        panel.getStylesheetPath()
                );
            }
            panel.init(this.panelManager);
            panel.onShow();
        }
    }
}