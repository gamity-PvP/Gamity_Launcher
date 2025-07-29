package fr.gamity.launcher.thomas260913.ui.panels.pages;

import fr.gamity.launcher.thomas260913.Launcher;
import fr.gamity.launcher.thomas260913.ui.PanelManager;
import fr.gamity.launcher.thomas260913.ui.panel.Panel;
import fr.gamity.launcher.thomas260913.utils.MCAccount;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.UUID;


public class Login extends Panel {
    GridPane loginCard = new GridPane();

    Saver saver = Launcher.getInstance().getSaver();

    TextField userField = new TextField();
    Label userErrorLabel = new Label();
    Label infocrack = new Label();
    Label infocrack2 = new Label();
    Button btnLogin = new Button("Connexion");
    Button msLoginBtn = new Button();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getStylesheetPath() {
        return "css/login.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        // Background
        this.layout.getStyleClass().add("login-layout");

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setMinWidth(350);
        columnConstraints.setMaxWidth(350);
        this.layout.getColumnConstraints().addAll(columnConstraints, new ColumnConstraints());
        this.layout.add(loginCard, 0, 0);

        // Background image
        GridPane bgImage = new GridPane();
        setCanTakeAllSize(bgImage);
        bgImage.getStyleClass().add("bg-image");
        this.layout.add(bgImage, 1, 0);

        // Login card
        setCanTakeAllSize(this.layout);
        loginCard.getStyleClass().add("login-card");
        setLeft(loginCard);
        setCenterH(loginCard);
        setCenterV(loginCard);

        /*
         * Login sidebar
         */
        ImageView baniere = new ImageView(new Image("images/baniere.png"));
        baniere.setPreserveRatio(true);
        baniere.setFitHeight(40d);
        baniere.getStyleClass().add("login-title");
        setCenterH(baniere);
        setCanTakeAllSize(baniere);
        setTop(baniere);
        baniere.setTranslateY(30d);
        loginCard.getChildren().add(baniere);


        setCanTakeAllSize(infocrack);
        setCenterV(infocrack);
        setCenterH(infocrack);
        infocrack.getStyleClass().add("info-crack");
        infocrack.setTranslateY(-125d);
        infocrack.setMaxWidth(280);
        infocrack.setTextAlignment(TextAlignment.LEFT);
        infocrack.setText("avec le mode crack vous ne pourrez pas");
        setCanTakeAllSize(infocrack2);
        setCenterV(infocrack2);
        setCenterH(infocrack2);
        infocrack2.getStyleClass().add("info-crack");
        infocrack2.setTranslateY(-110d);
        infocrack2.setMaxWidth(280);
        infocrack2.setTextAlignment(TextAlignment.LEFT);
        infocrack2.setText("accéder à une grande partie des serveurs");

        setCanTakeAllSize(userField);
        setCenterV(userField);
        setCenterH(userField);
        userField.setPromptText("Pseudo");
        userField.setMaxWidth(300);
        userField.setTranslateY(-70d);
        userField.getStyleClass().add("login-input");
        userField.textProperty().addListener((_a, oldValue, newValue) -> this.updateLoginBtnState(userField, userErrorLabel));

        // User error
        setCanTakeAllSize(userErrorLabel);
        setCenterV(userErrorLabel);
        setCenterH(userErrorLabel);
        userErrorLabel.getStyleClass().add("login-error");
        userErrorLabel.setTranslateY(-45d);
        userErrorLabel.setMaxWidth(280);
        userErrorLabel.setTextAlignment(TextAlignment.LEFT);

        // Login button
        setCanTakeAllSize(btnLogin);
        setCenterV(btnLogin);
        setCenterH(btnLogin);
        btnLogin.setDisable(true);
        btnLogin.setMaxWidth(300);
        btnLogin.setTranslateY(10d);
        btnLogin.getStyleClass().add("login-log-btn");
        btnLogin.setOnMouseClicked(e -> {
                this.authenticate();
        });

        Separator separator = new Separator();
        setCanTakeAllSize(separator);
        setCenterH(separator);
        setCenterV(separator);
        separator.getStyleClass().add("login-separator");
        separator.setMaxWidth(300);
        separator.setTranslateY(40d);


        // Login with label
        Label loginWithLabel = new Label("Se connecter avec:".toUpperCase());
        setCanTakeAllSize(loginWithLabel);
        setCenterV(loginWithLabel);
        setCenterH(loginWithLabel);
        loginWithLabel.setFont(Font.font(loginWithLabel.getFont().getFamily(), FontWeight.BOLD, FontPosture.REGULAR, 14d));
        loginWithLabel.getStyleClass().add("login-with-label");
        loginWithLabel.setTranslateY(70d);
        loginWithLabel.setMaxWidth(280d);

        // Microsoft login button
        ImageView view = new ImageView(new Image("images/microsoft.png"));
        view.setPreserveRatio(true);
        view.setFitHeight(30d);
        setCanTakeAllSize(msLoginBtn);
        setCenterH(msLoginBtn);
        setCenterV(msLoginBtn);
        msLoginBtn.getStyleClass().add("ms-login-btn");
        msLoginBtn.setMaxWidth(300);
        msLoginBtn.setTranslateY(110d);
        msLoginBtn.setGraphic(view);
        msLoginBtn.setOnMouseClicked(e -> this.authenticateMS());


        loginCard.getChildren().addAll(infocrack, infocrack2, userField, userErrorLabel, btnLogin, separator, loginWithLabel, msLoginBtn);

    }


    public void updateLoginBtnState(TextField textField, Label errorLabel) {

        if (textField.getText().length() == 0) {
            errorLabel.setText("Le champ ne peut être vide");
        } else {
            errorLabel.setText("");
        }

        btnLogin.setDisable(!(userField.getText().length() > 0));
        btnLogin.setDisable(userField.getText().length() < 3);
    }

    public void authenticate() {
            if(userField.getText().contains(" ")){
                userErrorLabel.setText("Le pseudo ne peut pas contenir d'espace");
            }else{
                userErrorLabel.setText("");
            AuthInfos infos = new AuthInfos(
                    userField.getText(),
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString()
            );
            saver.set("offline-username" + saver.get("selectAccount"), infos.getUsername());
            saver.save();
            Launcher.getInstance().addMCAccount(new MCAccount(infos,true));

            this.logger.info("Hello " + infos.getUsername());

            panelManager.showPanel(new App());
            }
    }

    public void authenticateMS() {
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        authenticator.loginWithAsyncWebview().whenComplete((response, error) -> {
            if (error != null) {
                Launcher.getInstance().getLogger().err(error.toString());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setContentText(error.getMessage());
                alert.show();
                return;
            }

            saver.set("msAccessToken" + saver.get("selectAccount"), response.getAccessToken());
            saver.set("msRefreshToken" + saver.get("selectAccount"), response.getRefreshToken());
            saver.save();
            Launcher.getInstance().addMCAccount(new MCAccount(new AuthInfos(
                    response.getProfile().getName(),
                    response.getAccessToken(),
                    response.getProfile().getId()
            ),false));
            this.logger.info("Hello " + response.getProfile().getName());

            Platform.runLater(() -> panelManager.showPanel(new App()));
        });
    }
}
