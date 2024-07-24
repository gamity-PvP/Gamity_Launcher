package fr.gamity.launcher.thomas260913.ui.panels.pages;

import fr.gamity.launcher.thomas260913.Launcher;
import fr.gamity.launcher.thomas260913.ui.PanelManager;
import fr.gamity.launcher.thomas260913.ui.panel.Panel;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;

public class selectAccount extends Panel {
    Saver saver = Launcher.getInstance().getSaver();
    GridPane background = new GridPane();
    GridPane boxPane = new GridPane();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getStylesheetPath() {
        return "css/selectAccount.css";
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
        boxPane.setMinHeight(400);
        boxPane.setMaxHeight(400);
        boxPane.setMinWidth(400);
        boxPane.setMaxWidth(400);
        setCenterH(boxPane);
        setCenterV(boxPane);
        this.layout.add(boxPane,0,0);

        // Account
        Label accountLabel = new Label("comptes");
        accountLabel.getStyleClass().add("settings-labels");
        setLeft(accountLabel);
        setCanTakeAllSize(accountLabel);
        setTop(accountLabel);
        accountLabel.setTextAlignment(TextAlignment.LEFT);
        accountLabel.setTranslateX(25d);
        accountLabel.setTranslateY(185d);

        // Account Slider

        ComboBox<String> comboBoxAccount = new ComboBox<>();
        comboBoxAccount.getStyleClass().add("ram-selector");
        for(int j = 0; j < Launcher.getInstance().getAuthInfosSize(); j+=1) {
            comboBoxAccount.getItems().add(j + ". " + Launcher.getInstance().getAuthInfos(j).getUsername());
        }
        if(comboBoxAccount.getItems().size() < Launcher.getInstance().getMaxAccount()){
            comboBoxAccount.getItems().add(comboBoxAccount.getItems().size() + ". Se connecter à votre compte");
        }

        comboBoxAccount.setValue("Séléctionner votre compte");
        setLeft(comboBoxAccount);
        setCanTakeAllSize(comboBoxAccount);
        setTop(comboBoxAccount);
        comboBoxAccount.setTranslateX(35d);
        comboBoxAccount.setTranslateY(215d);
        comboBoxAccount.valueProperty().addListener((e, old, newValue) -> {
            String[] Account = newValue.split("\\.");
            int _selectAccount = Integer.parseInt(Account[0]);
            saver.set("selectAccount", String.valueOf(_selectAccount));
            saver.save();
            if(Account[1].equals(" Se connecter à votre compte")){
                panelManager.showPanel(new Login());
            }else{
                panelManager.showPanel(new App());
            }
        });
        boxPane.getChildren().addAll(accountLabel,comboBoxAccount);
    }
}