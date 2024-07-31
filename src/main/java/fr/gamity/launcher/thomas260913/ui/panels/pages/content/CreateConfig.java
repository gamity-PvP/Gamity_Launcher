package fr.gamity.launcher.thomas260913.ui.panels.pages.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gamity.launcher.thomas260913.Launcher;
import fr.gamity.launcher.thomas260913.ui.PanelManager;
import fr.gamity.launcher.thomas260913.ui.panels.pages.App;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CreateConfig extends ContentPanel {
    private final List<Path> jsonFile = JsonClient.readJsonFilesFromFolder(Launcher.getInstance().getConfigDir());
    VersionList versionList = new VersionList();
    private final ComboBox<String> configComboBox = new ComboBox<>();
    private final TextField nameField = new TextField();
    private final ComboBox<String> mcTypeComboBox = new ComboBox<>();
    private final TextField forgeVersionField = new TextField();
    private final TextField forgeModsCustomField = new TextField();
    private final TextField forgeModsCurseField = new TextField();
    private final ComboBox<String> mcVersionCombobox = new ComboBox<>();
    private final ComboBox<String> mcJavaCombobox = new ComboBox<>();
    private final TextField mcExtFilesField = new TextField();
    private final CheckBox autoconnectCheckBox = new CheckBox();
    private final CheckBox mcExtFilesCheckBox = new CheckBox();
    private final TextField serverIpField = new TextField();
    private final TextField serverPortField = new TextField();
    private final CheckBox optifinechkBox = new CheckBox();
    private final ComboBox<String> optifineVersionComboBox = new ComboBox<>();

    List<Parser.OptifineParser.OptifineJson.OptifineList> optifineList = new ArrayList<>();

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

        jsonFile.forEach(path ->{
            try{
                Config.CustomServer config = new Parser.JsonConfigParser().parseJsonPath(path);
                configComboBox.getItems().add(config.name);
            }catch(Exception e) {
                Launcher.getInstance().getLogger().printStackTrace(e);
                Launcher.getInstance().showErrorDialog(e,this.panelManager.getStage());
            }
        });

        configComboBox.getItems().add("créer une nouvelle config");
        configComboBox.setValue("créer une nouvelle config");
        configComboBox.valueProperty().addListener((e,old,newValue)->{
            if(Objects.equals(newValue, "créer une nouvelle config")){
                nameField.setText("");
                optifinechkBox.setSelected(false);
                optifinechkBox.setDisable(true);
                optifineVersionComboBox.setDisable(true);
                optifineVersionComboBox.setValue("aucune");
                forgeVersionField.setDisable(true);
                forgeVersionField.setText("");
                forgeModsCurseField.setDisable(true);
                forgeModsCurseField.setText("");
                forgeModsCustomField.setDisable(true);
                forgeModsCustomField.setText("");
                serverIpField.setDisable(true);
                serverIpField.setText("");
                serverPortField.setDisable(true);
                serverPortField.setText("");
                mcExtFilesField.setDisable(true);
                mcExtFilesField.setText("");
                mcVersionCombobox.setValue("latest");
                mcTypeComboBox.setValue("vanilla");
                mcJavaCombobox.setValue("21");
            }else{
                try {
                    Config.CustomServer config = new Parser.JsonConfigParser().parseJsonPath(Launcher.getInstance().getConfigDir().resolve(configComboBox.getValue() + ".json"));

                    nameField.setText(config.name);
                    if(config.mcinfo.type.contains("forge")){
                        mcTypeComboBox.setValue("forge");
                    }else{
                        mcTypeComboBox.setValue(config.mcinfo.type);
                    }
                    if(Objects.equals(config.mcinfo.type, "vanilla") || Objects.equals(config.mcinfo.type, "snapshot")){
                        forgeVersionField.setDisable(true);
                        forgeVersionField.setText("");
                        forgeModsCurseField.setDisable(true);
                        forgeModsCurseField.setText("");
                        forgeModsCustomField.setDisable(true);
                        forgeModsCustomField.setText("");
                        optifinechkBox.setSelected(false);
                        optifinechkBox.setDisable(true);
                        optifineVersionComboBox.setDisable(true);
                        mcVersionCombobox.setDisable(false);
                    }else{
                        mcVersionCombobox.setDisable(true);
                        forgeVersionField.setDisable(false);
                        forgeVersionField.setText(config.mcinfo.forge.version);
                        forgeModsCurseField.setDisable(false);
                        forgeModsCurseField.setText(config.mcinfo.forge.mods.curseForge.json);
                        forgeModsCustomField.setText(config.mcinfo.forge.mods.custom.json);
                        forgeModsCustomField.setDisable(false);
                        optifinechkBox.setDisable(false);
                        optifinechkBox.setSelected(config.mcinfo.forge.allowOptifine);
                        optifineVersionComboBox.setDisable(!config.mcinfo.forge.allowOptifine);
                        if(config.mcinfo.forge.allowOptifine) {
                            optifineVersionComboBox.setValue(config.mcinfo.forge.optifine.optifineVersion);
                        }
                    }
                    mcVersionCombobox.setValue(config.mcinfo.mc.version);
                    mcJavaCombobox.setValue(config.mcinfo.mc.java);
                    if(config.mcinfo.mc.extfiles != null) {
                        if (!config.mcinfo.mc.extfiles.startsWith("http")) {
                            mcExtFilesCheckBox.setSelected(false);
                            mcExtFilesField.setDisable(true);
                            mcExtFilesField.setText("");
                        }else{
                            mcExtFilesCheckBox.setSelected(true);
                            mcExtFilesField.setDisable(false);
                            mcExtFilesField.setText(config.mcinfo.mc.extfiles);
                        }
                    }
                    if(config.mcinfo.autoconnect){
                        autoconnectCheckBox.setSelected(true);
                        serverIpField.setDisable(false);
                        serverPortField.setDisable(false);
                        serverIpField.setText(config.mcinfo.server.ip);
                        serverPortField.setText(config.mcinfo.server.port);
                    }

                } catch (Exception ex) {
                    optifinechkBox.setSelected(false);
                    optifinechkBox.setDisable(true);
                    optifineVersionComboBox.setDisable(true);
                    optifineVersionComboBox.setValue("aucune");
                    forgeVersionField.setDisable(true);
                    forgeVersionField.setText("");
                    forgeModsCurseField.setDisable(true);
                    forgeModsCurseField.setText("");
                    forgeModsCustomField.setDisable(true);
                    forgeModsCustomField.setText("");
                    serverIpField.setDisable(true);
                    serverIpField.setText("");
                    serverPortField.setDisable(true);
                    serverPortField.setText("");
                    mcExtFilesField.setDisable(true);
                    mcExtFilesField.setText("");
                    mcVersionCombobox.setValue("latest");
                    mcTypeComboBox.setValue("vanilla");
                    mcJavaCombobox.setValue("21");
                    mcVersionCombobox.setDisable(false);
                    Launcher.getInstance().getLogger().printStackTrace(ex);
                    Launcher.getInstance().showErrorDialog(ex,this.panelManager.getStage());
                }
            }
        });
        // Background
        this.layout.getStyleClass().add("config-layout");
        this.layout.setPadding(new Insets(40));
        setCanTakeAllSize(this.layout);

        // Content
        contentPane.getStyleClass().add("content-pane");
        setCanTakeAllSize(contentPane);
        this.layout.getChildren().add(contentPane);

        // Titre
        Label title = new Label("Gestion des config");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
        title.getStyleClass().add("settings-title");
        setLeft(title);
        setCanTakeAllSize(title);
        setTop(title);
        title.setTextAlignment(TextAlignment.LEFT);
        title.setTranslateY(0d);
        title.setTranslateX(25d);

        mcTypeComboBox.getItems().addAll("vanilla", "forge","snapshot");
        mcJavaCombobox.getItems().addAll("21", "17", "8");
        mcVersionCombobox.getItems().add("latest");
        try {
            versionList.versions = Launcher.getInstance().getVersionList().versions.stream().filter(version1 -> Objects.equals(version1.type, "release")).collect(Collectors.toList());
            versionList.versions.forEach(version1->mcVersionCombobox.getItems().add(version1.id));
            optifineList = new Parser.OptifineParser().OptifineRequest("all","all",false);
            if(!Objects.equals(mcVersionCombobox.getValue(), "latest")) {
                List<Parser.OptifineParser.OptifineJson.OptifineList> filter = optifineList.stream().filter(o-> Objects.equals(o.mcversion, mcVersionCombobox.getValue())).collect(Collectors.toList());
                filter.forEach(optifine-> optifineVersionComboBox.getItems().add(optifine.optifine.version));
                if(optifineVersionComboBox.getItems().isEmpty()){
                    optifineVersionComboBox.getItems().add("aucune");
                    optifineVersionComboBox.setValue("aucune");
                }else{
                    optifineVersionComboBox.getItems().add("auto");
                    optifineVersionComboBox.setValue("auto");
                }
            }
        }catch(Exception e){
            Launcher.getInstance().getLogger().printStackTrace(e);
            Launcher.getInstance().showErrorDialog(e,this.panelManager.getStage());
        }

        optifinechkBox.setSelected(false);
        optifinechkBox.setDisable(true);
        optifineVersionComboBox.setDisable(true);
        forgeVersionField.setDisable(true);
        forgeModsCurseField.setDisable(true);
        forgeModsCustomField.setDisable(true);
        serverIpField.setDisable(!autoconnectCheckBox.isSelected());
        serverPortField.setDisable(!autoconnectCheckBox.isSelected());
        mcExtFilesField.setDisable(!mcExtFilesCheckBox.isSelected());
        mcVersionCombobox.setValue("latest");
        mcTypeComboBox.setValue("vanilla");
        mcJavaCombobox.setValue("21");
        forgeVersionField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Code à exécuter lorsque le texte change
            if (newValue.contains(" ")) {
                forgeVersionField.setText(newValue.replace(" ", ""));
            } else {
                if (newValue.contains("-")) {
                    mcVersionCombobox.setValue(getVanillaVersionFromForge(newValue));
                } else {
                    mcVersionCombobox.setValue("forge version error");
                }
            }
        });
        mcTypeComboBox.valueProperty().addListener((e, old, newValue) -> {
            mcVersionCombobox.getItems().clear();
            switch(newValue){
                case "snapshot":
                    versionList.versions = Launcher.getInstance().getVersionList().versions.stream().filter(version1 -> Objects.equals(version1.type, "snapshot")).collect(Collectors.toList());
                    break;
                case "vanilla":
                case "forge":
                    versionList.versions = Launcher.getInstance().getVersionList().versions.stream().filter(version1 -> Objects.equals(version1.type, "release")).collect(Collectors.toList());
                    break;
            }
            versionList.versions.forEach(McVersion-> mcVersionCombobox.getItems().add(McVersion.id));
            mcVersionCombobox.getItems().add("latest");
            mcVersionCombobox.setValue("latest");
            if(Objects.equals(newValue, "vanilla") || Objects.equals(newValue, "snapshot")){
                forgeVersionField.setDisable(true);
                forgeModsCurseField.setDisable(true);
                forgeModsCustomField.setDisable(true);
                optifinechkBox.setSelected(false);
                optifinechkBox.setDisable(true);
                optifineVersionComboBox.setDisable(true);
                mcVersionCombobox.setDisable(false);
            }else{
                forgeVersionField.setDisable(false);
                forgeModsCurseField.setDisable(false);
                forgeModsCustomField.setDisable(false);
                optifinechkBox.setDisable(false);
                mcVersionCombobox.setDisable(true);
                optifineVersionComboBox.setDisable(!optifinechkBox.isSelected());
            }
        });
        optifinechkBox.setOnAction((e)-> optifineVersionComboBox.setDisable(!optifinechkBox.isSelected()));
        mcVersionCombobox.valueProperty().addListener((e, old, newValue) -> {
            optifineVersionComboBox.getItems().clear();
            if(!Objects.equals(newValue, "latest")) {
                List<Parser.OptifineParser.OptifineJson.OptifineList> filter = optifineList.stream().filter(o-> Objects.equals(o.mcversion, newValue)).collect(Collectors.toList());
                filter.forEach(optifine-> optifineVersionComboBox.getItems().add(optifine.optifine.version));
                if(optifineVersionComboBox.getItems().isEmpty()){
                    optifineVersionComboBox.getItems().add("aucune");
                    optifineVersionComboBox.setValue("aucune");
                }else{
                    optifineVersionComboBox.getItems().add("auto");
                    optifineVersionComboBox.setValue("auto");
                }
            }
        });
        mcExtFilesCheckBox.setOnAction((e) -> mcExtFilesField.setDisable(!mcExtFilesCheckBox.isSelected()));
        autoconnectCheckBox.setOnAction((e) -> {
            serverIpField.setDisable(!autoconnectCheckBox.isSelected());
            serverPortField.setDisable(!autoconnectCheckBox.isSelected());
        });
        // Fields and Labels
        addFieldAndLabel("config:", configComboBox, 60);
        addFieldAndLabel("Nom de la config:", nameField, 110);
        addFieldAndLabel("Type de Minecraft:", mcTypeComboBox, 160);
        addFieldAndLabel("Version de Forge:", forgeVersionField, 210);
        addFieldAndLabel("Liste des mods CurseForge:", forgeModsCurseField, 260);
        addFieldAndLabel("Liste des mods Forge Custom:", forgeModsCustomField, 310);
        addFieldAndLabel("Version de Minecraft:", mcVersionCombobox, 360);
        addFieldAndLabel("Version Java:", mcJavaCombobox, 410);
        addFieldAndLabel("Fichiers externes:", mcExtFilesCheckBox, 460);
        addFieldAndLabel("Fichiers externes url:", mcExtFilesField, 510);
        addFieldAndLabel("Autoconnect:", autoconnectCheckBox, 560);
        addFieldAndLabel("IP du serveur:", serverIpField, 610);
        addFieldAndLabel("Port du serveur:", serverPortField, 660);
        addFieldAndLabel("Optifine:", optifinechkBox, 710);
        addFieldAndLabel("Version:", optifineVersionComboBox, 760);

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

        panelManager.getStage().setMinHeight(900.0);
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
        field.setTranslateX(250d);
        field.setTranslateY(translateY - 5d);

        contentPane.getChildren().addAll(label, field);
    }

    private void saveConfig() {
        Config.CustomServer config = new Config.CustomServer();
        if(Objects.equals(configComboBox.getValue(), "créer une nouvelle config")){
            config = new Config.CustomServer();
        }else{
            try {
                config = new Parser.JsonConfigParser().parseJsonPath(Launcher.getInstance().getConfigDir().resolve(configComboBox.getValue() + ".json"));
            } catch (Exception e) {
                Launcher.getInstance().showErrorDialog(e);
                Launcher.getInstance().getLogger().printStackTrace(e);
            }
        }
        config.name = nameField.getText();
        config.mcinfo = new Config.CustomServer.McInfo();
        config.mcinfo.type = getForgeType(forgeVersionField.getText());
        if(!Objects.equals(config.mcinfo.type, "vanilla")){
            config.mcinfo.forge = new Config.CustomServer.McInfo.Forge();
            config.mcinfo.forge.version = forgeVersionField.getText();
            config.mcinfo.forge.mods = new Config.CustomServer.McInfo.Forge.Mods();
            config.mcinfo.forge.mods.curseForge = new Config.CustomServer.McInfo.Forge.Mods.CurseForge();
            config.mcinfo.forge.mods.custom = new Config.CustomServer.McInfo.Forge.Mods.Custom();
            if(forgeModsCustomField.getText() != null) {
                config.mcinfo.forge.mods.custom.json = forgeModsCustomField.getText();
            }
            if(forgeModsCurseField.getText() != null) {
                config.mcinfo.forge.mods.curseForge.json = forgeModsCurseField.getText();
            }
            if(optifinechkBox.isSelected()){
                config.mcinfo.forge.allowOptifine = true;
                config.mcinfo.forge.optifine = new Config.CustomServer.McInfo.Forge.Optifine();
                if(!Objects.equals(optifineVersionComboBox.getValue(), "auto")) {
                    config.mcinfo.forge.optifine.optifineVersion = optifineVersionComboBox.getValue();
                }
            }
        }
        config.mcinfo.mc = new Config.CustomServer.McInfo.Mc();
        if(Objects.equals(mcVersionCombobox.getValue(), "latest")){
            switch (mcTypeComboBox.getValue()){
                case "forge":
                case "vanilla":
                    config.mcinfo.mc.version = versionList.latest.release;
                    break;
                case "snapshot":
                    config.mcinfo.mc.version = versionList.latest.snapshot;
                    break;
            }
        }else{
            config.mcinfo.mc.version = mcVersionCombobox.getValue();
        }
        config.mcinfo.mc.java = mcJavaCombobox.getValue();
        if(mcExtFilesCheckBox.isSelected()) {
            config.mcinfo.mc.extfiles = mcExtFilesField.getText();
        }
        config.mcinfo.autoconnect = autoconnectCheckBox.isSelected();
        if(config.mcinfo.autoconnect) {
            config.mcinfo.server = new Config.CustomServer.McInfo.Server();
            config.mcinfo.server.ip = serverIpField.getText();
            config.mcinfo.server.port = serverPortField.getText();
        }

        ObjectMapper mapper = new ObjectMapper();
        Path destinationPath = Launcher.getInstance().getConfigDir().resolve(config.name + ".json");

        try {
            if(!Files.exists(destinationPath) || !Objects.equals(configComboBox.getValue(), "créer une nouvelle config")) {
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
        Config.CustomServer config = new Config.CustomServer();
        if(Objects.equals(configComboBox.getValue(), "créer une nouvelle config")){
            config = new Config.CustomServer();
        }else{
            try {
                config = new Parser.JsonConfigParser().parseJsonPath(Launcher.getInstance().getConfigDir().resolve(configComboBox.getValue() + ".json"));
            } catch (Exception e) {
                Launcher.getInstance().showErrorDialog(e);
                Launcher.getInstance().getLogger().printStackTrace(e);
            }
        }
        config.name = nameField.getText();
        config.mcinfo = new Config.CustomServer.McInfo();
        config.mcinfo.type = getForgeType(forgeVersionField.getText());
        if(!Objects.equals(config.mcinfo.type, "vanilla")){
            config.mcinfo.forge = new Config.CustomServer.McInfo.Forge();
            config.mcinfo.forge.version = forgeVersionField.getText();
            config.mcinfo.forge.mods = new Config.CustomServer.McInfo.Forge.Mods();
            config.mcinfo.forge.mods.curseForge = new Config.CustomServer.McInfo.Forge.Mods.CurseForge();
            config.mcinfo.forge.mods.custom = new Config.CustomServer.McInfo.Forge.Mods.Custom();
            if(forgeModsCustomField.getText() != null) {
                config.mcinfo.forge.mods.custom.json = forgeModsCustomField.getText();
            }
            if(forgeModsCurseField.getText() != null) {
                config.mcinfo.forge.mods.curseForge.json = forgeModsCurseField.getText();
            }
            if(optifinechkBox.isSelected()){
                config.mcinfo.forge.allowOptifine = true;
                config.mcinfo.forge.optifine = new Config.CustomServer.McInfo.Forge.Optifine();
                if(!Objects.equals(optifineVersionComboBox.getValue(), "auto")) {
                    config.mcinfo.forge.optifine.optifineVersion = optifineVersionComboBox.getValue();
                }
            }
        }
        config.mcinfo.mc = new Config.CustomServer.McInfo.Mc();
        if(Objects.equals(mcVersionCombobox.getValue(), "latest")){
            switch (mcTypeComboBox.getValue()){
                case "forge":
                case "vanilla":
                    config.mcinfo.mc.version = versionList.latest.release;
                    break;
                case "snapshot":
                    config.mcinfo.mc.version = versionList.latest.snapshot;
                    break;
            }
        }else{
            config.mcinfo.mc.version = mcVersionCombobox.getValue();
        }
        config.mcinfo.mc.java = mcJavaCombobox.getValue();
        if(mcExtFilesCheckBox.isSelected()) {
            config.mcinfo.mc.extfiles = mcExtFilesField.getText();
        }
        config.mcinfo.autoconnect = autoconnectCheckBox.isSelected();
        if(config.mcinfo.autoconnect) {
            config.mcinfo.server = new Config.CustomServer.McInfo.Server();
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
    private static String getForgeType(String version){
        String[] data = version.split("-");
        String mcVersion = data[0];
        String forgeVersion = data[1];
        String[] mcVersionSplit = mcVersion.split("\\.");
        if(Integer.parseInt(mcVersionSplit[1]) > 12){
            return "newforge";
        }else if(Integer.parseInt(mcVersionSplit[1]) < 12){
            if(Integer.parseInt(mcVersionSplit[1]) != 7){
                return "oldforge";
            }else{
                return "very_oldforge";
            }
        }else{
            String[] forgeVersionSplit = forgeVersion.split("\\.");
            if(Integer.parseInt(forgeVersionSplit[3]) >= 2851){
                return "newforge";
            }else{
                return "oldforge";
            }
        }
    }
    private static String getVanillaVersionFromForge(String forgeVersion){
        String[] data = forgeVersion.split("-");
        return data[0];
    }
}
