package fr.gamity.launcher.thomas260913.ui.panels.pages.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gamity.launcher.thomas260913.Launcher;
import fr.gamity.launcher.thomas260913.ui.PanelManager;
import fr.gamity.launcher.thomas260913.ui.panels.pages.App;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class createConfig extends ContentPanel {
    private Version versionList;
    private final TextField nameField = new TextField();
    private final ComboBox<String> mcTypeComboBox = new ComboBox<>();
    private final TextField forgeVersionField = new TextField();
    private final TextField forgeModsField = new TextField();
    private final ComboBox<String> mcVersionCombobox = new ComboBox<>();
    private final ComboBox<String> mcJavaCombobox = new ComboBox<>();
    private final TextField mcExtFilesField = new TextField();
    private final CheckBox autoconnectCheckBox = new CheckBox();
    private final CheckBox mcExtFilesCheckBox = new CheckBox();
    private final TextField serverIpField = new TextField();
    private final TextField serverPortField = new TextField();

    GridPane contentPane = new GridPane();

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/config.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        // Background
        this.layout.getStyleClass().add("config-layout");
        this.layout.setPadding(new Insets(40));
        setCanTakeAllSize(this.layout);

        // Content
        contentPane.getStyleClass().add("content-pane");
        setCanTakeAllSize(contentPane);
        this.layout.getChildren().add(contentPane);

        // Titre
        Label title = new Label("Créer votre config");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
        title.getStyleClass().add("settings-title");
        setLeft(title);
        setCanTakeAllSize(title);
        setTop(title);
        title.setTextAlignment(TextAlignment.LEFT);
        title.setTranslateY(0d);
        title.setTranslateX(25d);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            versionList = objectMapper.readValue(new URL("https://gamity-pvp.fr/apis/launcher/mcversion/list.json"), Version.class);
            versionList.getVersionList().forEach(McVersion-> {
                mcVersionCombobox.getItems().add(McVersion);
            });
        }catch(Exception e){
            Launcher.getInstance().getLogger().printStackTrace(e);
            Launcher.getInstance().showErrorDialog(e,this.panelManager.getStage());
        }

        forgeVersionField.setDisable(true);
        forgeModsField.setDisable(true);
        serverIpField.setDisable(!autoconnectCheckBox.isSelected());
        serverPortField.setDisable(!autoconnectCheckBox.isSelected());
        mcExtFilesField.setDisable(!mcExtFilesCheckBox.isSelected());
        mcTypeComboBox.getItems().addAll("vanilla", "newforge", "oldforge", "very_oldforge");
        mcJavaCombobox.getItems().addAll("21", "17", "8");
        mcVersionCombobox.getItems().add("latest");
        mcVersionCombobox.setValue("latest");
        mcTypeComboBox.setValue("vanilla");
        mcJavaCombobox.setValue("21");

        mcTypeComboBox.valueProperty().addListener((e, old, newValue) -> {
            if(Objects.equals(newValue, "vanilla")){
                forgeVersionField.setDisable(true);
                forgeModsField.setDisable(true);
            }else{
                forgeVersionField.setDisable(false);
                forgeModsField.setDisable(false);
            }
        });
        mcExtFilesCheckBox.setOnAction((e) -> {
            mcExtFilesField.setDisable(!mcExtFilesCheckBox.isSelected());
        });
        autoconnectCheckBox.setOnAction((e) -> {
            serverIpField.setDisable(!autoconnectCheckBox.isSelected());
            serverPortField.setDisable(!autoconnectCheckBox.isSelected());
        });
        // Fields and Labels
        addFieldAndLabel("Nom de la config:", nameField, 60);
        addFieldAndLabel("Type de Minecraft:", mcTypeComboBox, 110);
        addFieldAndLabel("Version de Forge:", forgeVersionField, 160);
        addFieldAndLabel("Liste des mods Forge url:", forgeModsField, 210);
        addFieldAndLabel("Version de Minecraft:", mcVersionCombobox, 260);
        addFieldAndLabel("Version Java:", mcJavaCombobox, 310);
        addFieldAndLabel("Fichiers externes:", mcExtFilesCheckBox, 360);
        addFieldAndLabel("Fichiers externes url:", mcExtFilesField, 410);
        addFieldAndLabel("Autoconnect:", autoconnectCheckBox, 460);
        addFieldAndLabel("IP du serveur:", serverIpField, 510);
        addFieldAndLabel("Port du serveur:", serverPortField, 560);

        // Save Button
        Button saveBtn = new Button("Enregistrer");
        saveBtn.getStyleClass().add("save-btn");
        FontAwesomeIconView saveIconView = new FontAwesomeIconView(FontAwesomeIcon.SAVE);
        saveIconView.setSize(App.iconSize);
        saveIconView.getStyleClass().add("save-icon");
        saveBtn.setGraphic(saveIconView);
        setCanTakeAllSize(saveBtn);
        setBottom(saveBtn);
        setCenterH(saveBtn);
        saveBtn.setTranslateY(-50d);
        saveBtn.setOnMouseClicked(e -> saveConfig());

        // Export Button
        Button exportBtn = new Button("Exporter");
        exportBtn.getStyleClass().add("export-btn");
        MaterialDesignIconView exportIconView = new MaterialDesignIconView(MaterialDesignIcon.FILE_EXPORT);
        exportIconView.setSize(App.iconSize);
        exportIconView.getStyleClass().add("export-icon");
        exportBtn.setGraphic(exportIconView);
        setCanTakeAllSize(exportBtn);
        setBottom(exportBtn);
        setCenterH(exportBtn);
        exportBtn.setOnMouseClicked(e -> exportConfig());

        contentPane.getChildren().addAll(title, saveBtn, exportBtn);

        panelManager.getStage().setMinHeight(780.0);
    }

    private void addFieldAndLabel(String labelText, Control field, double translateY) {
        Label label = new Label(labelText);
        label.getStyleClass().add("config-label");
        setLeft(label);
        setCanTakeAllSize(label);
        setTop(label);
        label.setTextAlignment(TextAlignment.LEFT);
        label.setTranslateX(25d);
        label.setTranslateY(translateY);

        if (field instanceof CheckBox) {
            field.getStyleClass().add("config-chk");
        } else if(field instanceof TextField) {
            field.getStyleClass().add("config-field");
        }else{
            field.getStyleClass().add("combobox");
        }
        if(field instanceof TextField){
            field.setMaxWidth(300);
        }
        setLeft(field);
        setCanTakeAllSize(field);
        setTop(field);
        field.setTranslateX(200d);
        field.setTranslateY(translateY - 5d);

        contentPane.getChildren().addAll(label, field);
    }

    private void saveConfig() {
        Config config = new Config();
        config.name = nameField.getText();
        config.mcinfo = new Config.McInfo();
        config.mcinfo.type = mcTypeComboBox.getValue();
        if(!Objects.equals(config.mcinfo.type, "vanilla")){
            config.mcinfo.forge = new Config.McInfo.Forge();
            config.mcinfo.forge.version = forgeVersionField.getText();
            config.mcinfo.forge.mods = forgeModsField.getText();
        }
        config.mcinfo.mc = new Config.McInfo.Mc();
        if(Objects.equals(mcVersionCombobox.getValue(), "latest")){
            config.mcinfo.mc.version = versionList.getLatest();
        }else{
            config.mcinfo.mc.version = mcVersionCombobox.getValue();
        }
        config.mcinfo.mc.java = mcJavaCombobox.getValue();
        if(mcExtFilesCheckBox.isSelected()) {
            config.mcinfo.mc.extfiles = mcExtFilesField.getText();
        }
        config.mcinfo.autoconnect = autoconnectCheckBox.isSelected();
        if(config.mcinfo.autoconnect) {
            config.mcinfo.server = new Config.McInfo.Server();
            config.mcinfo.server.ip = serverIpField.getText();
            config.mcinfo.server.port = serverPortField.getText();
        }

        ObjectMapper mapper = new ObjectMapper();
        Path destinationPath = Launcher.getInstance().getConfigDir().resolve(config.name + ".json");

        try {
            if(!Files.exists(destinationPath)) {
                mapper.writeValue(destinationPath.toFile(), config);
                Launcher.getInstance().showAlert(Alert.AlertType.INFORMATION, "Sauvegarde", "Configuration sauvegardée avec succès !");
            }else{
                Launcher.getInstance().showAlert(Alert.AlertType.WARNING, "Sauvegarde", "La configuration " + config.name + " exist déjà\nveuiller changer le nom ou supprimer l'ancienne puis réessayer");
            }
        } catch (IOException e) {
            Launcher.getInstance().showErrorDialog(e, this.panelManager.getStage());
        }
    }

    private void exportConfig() {
        Config config = new Config();
        config.name = nameField.getText();
        config.mcinfo = new Config.McInfo();
        config.mcinfo.type = mcTypeComboBox.getValue();
        if(!Objects.equals(config.mcinfo.type, "vanilla")){
            config.mcinfo.forge = new Config.McInfo.Forge();
            config.mcinfo.forge.version = forgeVersionField.getText();
            config.mcinfo.forge.mods = forgeModsField.getText();
        }
        config.mcinfo.mc = new Config.McInfo.Mc();
        if(Objects.equals(mcVersionCombobox.getValue(), "latest")){
            config.mcinfo.mc.version = versionList.getLatest();
        }else{
            config.mcinfo.mc.version = mcVersionCombobox.getValue();
        }
        config.mcinfo.mc.java = mcJavaCombobox.getValue();
        if(mcExtFilesCheckBox.isSelected()) {
            config.mcinfo.mc.extfiles = mcExtFilesField.getText();
        }
        config.mcinfo.autoconnect = autoconnectCheckBox.isSelected();
        if(config.mcinfo.autoconnect) {
            config.mcinfo.server = new Config.McInfo.Server();
            config.mcinfo.server.ip = serverIpField.getText();
            config.mcinfo.server.port = serverPortField.getText();
        }

        ObjectMapper mapper = new ObjectMapper();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter la configuration");
        fileChooser.setInitialFileName(config.name);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers JSON", "*.json"));
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
}
