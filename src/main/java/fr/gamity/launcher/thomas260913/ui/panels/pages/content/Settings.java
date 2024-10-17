package fr.gamity.launcher.thomas260913.ui.panels.pages.content;

import fr.gamity.launcher.thomas260913.Launcher;
import fr.gamity.launcher.thomas260913.MultiOutputStream;
import fr.gamity.launcher.thomas260913.TextAreaOutputStream;
import fr.gamity.launcher.thomas260913.ui.PanelManager;
import fr.gamity.launcher.thomas260913.ui.panels.pages.App;
import fr.gamity.launcher.thomas260913.ui.panels.pages.Login;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;


public class Settings extends ContentPanel {
    public static TextArea consoleArea;
    private final Saver saver = Launcher.getInstance().getSaver();

    AtomicBoolean _close = new AtomicBoolean(Boolean.parseBoolean(saver.get("autoclose")));
    AtomicBoolean _wait = new AtomicBoolean(Boolean.parseBoolean(saver.get("wait-launch")));
    AtomicBoolean _optifine = new AtomicBoolean(Boolean.parseBoolean(saver.get("optifine")));

    GridPane contentPane = new GridPane();

    @Override
    public String getName() {
        return "settings";
    }

    @Override
    public String getStylesheetPath() {
        //return null;
        return "css/content/settings.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);
        Label saveLabel = new Label();

        // Background
        this.layout.getStyleClass().add("settings-layout");
        this.layout.setPadding(new Insets(40));
        setCanTakeAllSize(this.layout);

        // Content
        contentPane.getStyleClass().add("content-pane");
        setCanTakeAllSize(contentPane);
        this.layout.getChildren().add(contentPane);

        // Titre
        Label title = new Label("Paramètres");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
        title.getStyleClass().add("settings-title");
        setLeft(title);
        setCanTakeAllSize(title);
        setTop(title);
        title.setTextAlignment(TextAlignment.LEFT);
        title.setTranslateY(20d);
        title.setTranslateX(25d);

        // RAM
        Label ramLabel = new Label("Mémoire max");
        ramLabel.getStyleClass().add("settings-labels");
        setLeft(ramLabel);
        setCanTakeAllSize(ramLabel);
        setTop(ramLabel);
        ramLabel.setTextAlignment(TextAlignment.LEFT);
        ramLabel.setTranslateX(25d);
        ramLabel.setTranslateY(80d);

        // RAM Slider
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getStyleClass().add("ram-selector");
        for(int i = 512; i <= Math.ceil(memory.getTotal() / Math.pow(1024, 2)); i+=512) {
            comboBox.getItems().add(i/1024.0+" Go");
        }

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

        if (comboBox.getItems().contains(val/1024.0+" Go")) {
            comboBox.setValue(val / 1024.0 + " Go");
        } else {
            comboBox.setValue("1.0 Go");
        }

        setLeft(comboBox);
        setCanTakeAllSize(comboBox);
        setTop(comboBox);
        comboBox.setTranslateX(35d);
        comboBox.setTranslateY(110d);
        comboBox.valueProperty().addListener((e, old, newValue) -> {
            saveLabel.setText("");
        });

        // autoclose label
        Label autocloseLabel = new Label("Fermeture du launcher après lancement");
        autocloseLabel.getStyleClass().add("autoclose-label");
        setLeft(autocloseLabel);
        setCanTakeAllSize(autocloseLabel);
        setTop(autocloseLabel);
        autocloseLabel.setTextAlignment(TextAlignment.LEFT);
        autocloseLabel.setTranslateX(25d);
        autocloseLabel.setTranslateY(150d);

        // autoclose

        CheckBox autoclose = new CheckBox("autoclose");
        setCanTakeAllSize(autoclose);
        setLeft(autoclose);
        setTop(autoclose);
        autoclose.getStyleClass().add("autoclose-chk");
        autoclose.setMaxWidth(300);
        autoclose.setTranslateX(35d);
        autoclose.setTranslateY(180d);
        autoclose.setSelected(_close.get());
        autoclose.selectedProperty().addListener((e, old, newValue) -> {
            _close.set(newValue);
            saveLabel.setText("");
        });
        // wait label
        Label waitLabel = new Label("Appuyer sur un bouton pour start le jeu après l'autoupdate");
        waitLabel.getStyleClass().add("wait-label");
        setLeft(waitLabel);
        setCanTakeAllSize(waitLabel);
        setTop(waitLabel);
        waitLabel.setTextAlignment(TextAlignment.LEFT);
        waitLabel.setTranslateX(25d);
        waitLabel.setTranslateY(210d);

        // wait

        CheckBox wait = new CheckBox("wait-launch");
        setCanTakeAllSize(wait);
        setLeft(wait);
        setTop(wait);
        wait.getStyleClass().add("wait-chk");
        wait.setMaxWidth(300);
        wait.setTranslateX(35d);
        wait.setTranslateY(240d);
        wait.setSelected(_wait.get());
        wait.selectedProperty().addListener((e, old, newValue) -> {
            _wait.set(newValue);
            saveLabel.setText("");
        });

        Label optifineLabel = new Label("Optifine");
        optifineLabel.getStyleClass().add("wait-label");
        setLeft(optifineLabel);
        setCanTakeAllSize(optifineLabel);
        setTop(optifineLabel);
        optifineLabel.setTextAlignment(TextAlignment.LEFT);
        optifineLabel.setTranslateX(25d);
        optifineLabel.setTranslateY(270d);

        CheckBox optifine = new CheckBox("optifine");
        setCanTakeAllSize(optifine);
        setLeft(optifine);
        setTop(optifine);
        optifine.getStyleClass().add("wait-chk");
        optifine.setMaxWidth(300);
        optifine.setTranslateX(35d);
        optifine.setTranslateY(300d);
        optifine.setSelected(_optifine.get());
        optifine.selectedProperty().addListener((e, old, newValue) -> {
            _optifine.set(newValue);
            saveLabel.setText("");
        });

        // Account
        Label accountLabel = new Label("comptes");
        accountLabel.getStyleClass().add("settings-labels");
        setLeft(accountLabel);
        setCanTakeAllSize(accountLabel);
        setTop(accountLabel);
        accountLabel.setTextAlignment(TextAlignment.LEFT);
        accountLabel.setTranslateX(25d);
        accountLabel.setTranslateY(330d);

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
        comboBoxAccount.setTranslateY(360d);
        comboBoxAccount.valueProperty().addListener((e, old, newValue) -> {
            saveLabel.setText("");
        });


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("choisir une configuration gamity launcher");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Json Files", "*.json")
        );

        Button configBtn = new Button("Importer une config launcher");
        configBtn.getStyleClass().add("save-btn");
        FontAwesomeIconView iconView2 = new FontAwesomeIconView(FontAwesomeIcon.FILE_CODE_ALT);
        iconView2.getStyleClass().add("save-icon");
        iconView2.setSize(App.iconSize);
        configBtn.setGraphic(iconView2);
        setLeft(configBtn);
        setCanTakeAllSize(configBtn);
        setTop(configBtn);
        configBtn.setTranslateX(35d);
        configBtn.setTranslateY(405d);
        configBtn.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(this.panelManager.getStage());
            if (selectedFile != null) {
                boolean success = copyFileToConfig(selectedFile);
                if(success){
                    Launcher.getInstance().showAlert(Alert.AlertType.INFORMATION,"importation de la config","copie de la config vers le dossier des configs réussi avec succès");
                }else{
                    Launcher.getInstance().showAlert(Alert.AlertType.ERROR,"importation de la config","le fichier existe déjà");
                }
            }
        });
        Button openBtn = new Button("ouvrir le dossier des configs");
        openBtn.getStyleClass().add("save-btn");
        MaterialDesignIconView iconView5 = new MaterialDesignIconView(MaterialDesignIcon.FOLDER);
        iconView5.setSize(App.iconSize);
        iconView5.getStyleClass().add("save-icon");
        openBtn.setGraphic(iconView5);
        setLeft(openBtn);
        setCanTakeAllSize(openBtn);
        setTop(openBtn);
        openBtn.setTranslateX(35d);
        openBtn.setTranslateY(450d);
        openBtn.setOnMouseClicked(e -> openFolder(Launcher.getInstance().getConfigDir()));

        Button deleteBtn = new Button("supprimer le dossier de jeux");
        deleteBtn.getStyleClass().add("save-btn");
        MaterialDesignIconView iconView4 = new MaterialDesignIconView(MaterialDesignIcon.CLOSE);
        iconView4.setSize(App.iconSize);
        iconView4.getStyleClass().add("save-icon");
        deleteBtn.setGraphic(iconView4);
        setLeft(deleteBtn);
        setCanTakeAllSize(deleteBtn);
        setTop(deleteBtn);
        deleteBtn.setTranslateX(35d);
        deleteBtn.setTranslateY(495d);
        deleteBtn.setOnMouseClicked(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    null,
                    "Êtes vous sùr de vouloir supprimer le dossier des jeux ?\nceci contienne toutes vos configs, sauvegarde, screenshot",
                    "Info",
                    JOptionPane.YES_NO_CANCEL_OPTION
            );
            if (choice == 0) {
                new Thread(()-> {
                    try {
                        deleteDirectory(new File(Launcher.getInstance().getClientDir().toUri()));
                    }catch (Exception ex){
                        Launcher.getInstance().getLogger().printStackTrace(ex);
                        Launcher.getInstance().showErrorDialog(ex,this.panelManager.getStage());
                    }
                },"deleteFile").start();
            }
            deleteBtn.setDisable(false);
        });

        Button consoleBtn = new Button("ouvrir la console");
        consoleBtn.getStyleClass().add("save-btn");
        MaterialDesignIconView iconView3 = new MaterialDesignIconView(MaterialDesignIcon.CONSOLE);
        iconView3.setSize(App.iconSize);
        iconView3.getStyleClass().add("save-icon");
        consoleBtn.setGraphic(iconView3);
        setLeft(consoleBtn);
        setCanTakeAllSize(consoleBtn);
        setTop(consoleBtn);
        consoleBtn.setTranslateX(35d);
        consoleBtn.setTranslateY(540d);
        consoleBtn.setOnMouseClicked(e -> openConsole());


        // Save Button Title


        saveLabel.getStyleClass().add("save-label");
        setCanTakeAllSize(saveLabel);
        setBottom(saveLabel);
        setCenterH(saveLabel);
        saveLabel.setTextAlignment(TextAlignment.CENTER);
        saveLabel.setTranslateY(-50d);

        //version

        Label version = new Label(Launcher.getLauncherVersion());
        version.getStyleClass().add("version-label");
        setCanTakeAllSize(version);
        setBottom(version);
        setRight(version);
        version.setTextAlignment(TextAlignment.RIGHT);
        version.setTranslateY(30d);

        // Save Button

        Button saveBtn = new Button("Enregistrer");
        saveBtn.getStyleClass().add("save-btn");
        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.SAVE);
        iconView.setSize(App.iconSize);
        iconView.getStyleClass().add("save-icon");
        saveBtn.setGraphic(iconView);
        setCanTakeAllSize(saveBtn);
        setBottom(saveBtn);
        setCenterH(saveBtn);
        saveBtn.setOnMouseClicked(e -> {
            double _val = Double.parseDouble(comboBox.getValue().replace(" Go", ""));
            _val *= 1024;
            if(!Objects.equals(comboBoxAccount.getValue(), "Séléctionner votre compte")) {
                String[] Account = comboBoxAccount.getValue().split("\\. ");
                int _selectAccount = Integer.parseInt(Account[0]);
                saver.set("selectAccount", String.valueOf(_selectAccount));
                if (Account[1].equals("Se connecter à votre compte")) {
                    panelManager.showPanel(new Login());
                } else {
                    panelManager.showPanel(new App());
                }
            }
            saver.set("autoclose", String.valueOf(_close.get()));
            saver.set("wait-launch", String.valueOf(_wait.get()));
            saver.set("optifine", String.valueOf(_optifine.get()));
            saver.set("maxRam", String.valueOf((int) _val));
            saveLabel.setText("Paramètre(s) enregistré(s)");
            saveLabel.setTextFill(Color.GREEN);
        });
        contentPane.getChildren().addAll(title,ramLabel,comboBox,autocloseLabel,autoclose,waitLabel,wait,optifineLabel,optifine,accountLabel,comboBoxAccount,configBtn,openBtn,deleteBtn,consoleBtn,saveLabel,saveBtn,version);
        panelManager.getStage().setMinHeight(680.0);
    }
    private void openFolder(Path path) {
        File configFolder = new File(path.toUri());
            try {
                Desktop.getDesktop().open(configFolder);
            } catch (IOException ex) {
                Launcher.getInstance().getLogger().printStackTrace(ex);
                Launcher.getInstance().showErrorDialog(ex,this.panelManager.getStage());
            }
    }
    private boolean copyFileToConfig(File fileToCopy) {
        Path destinationPath = null;
        try {
            Path sourcePath = fileToCopy.toPath();
            Parser.JsonConfigParser parser = new Parser.JsonConfigParser();
            Config.CustomServer config = parser.parseJsonPath(sourcePath);
            destinationPath = Launcher.getInstance().getConfigDir().resolve(config.name + ".json");
            if(Files.exists(destinationPath)){
                return false;
            }
            Files.copy(sourcePath, destinationPath);
            Launcher.getInstance().getLogger().info("File copied to: " + destinationPath);
        } catch (Exception e) {
            assert destinationPath != null;
            Launcher.getInstance().getLogger().err("Failed to copy file to: " + destinationPath);
            Launcher.getInstance().getLogger().printStackTrace(e);
            Launcher.getInstance().showErrorDialog(e,this.panelManager.getStage());
        }
        return true;
    }

    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }
    private void openConsole() {
        if (consoleArea == null) {
            consoleArea = new TextArea();
            consoleArea.setEditable(false);

            // Transfer logs from the buffer to the TextArea
            consoleArea.setText(Launcher.getInstance().getLogBuffer().toString());

            // Redirect system output to the TextArea now
            MultiOutputStream multiOutStream = new MultiOutputStream();
            multiOutStream.addOutputStream(System.out);
            multiOutStream.addOutputStream(new TextAreaOutputStream(consoleArea));

            PrintStream printStream = new PrintStream(multiOutStream);
            System.setOut(printStream);
            System.setErr(printStream);
        }

        Stage consoleStage = new Stage();
        VBox root = new VBox(consoleArea);
        VBox.setVgrow(consoleArea, Priority.ALWAYS); // Make the TextArea take all available space
        Scene scene = new Scene(root, 600, 400);
        consoleStage.setScene(scene);
        consoleStage.setTitle("Console");
        consoleStage.getIcons().add(new Image("images/icon.png"));
        consoleStage.show();
    }
}
