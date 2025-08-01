package fr.gamity.launcher.thomas260913.ui.panels.pages.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import fr.gamity.launcher.thomas260913.JsonCreator.JsonCreator;
import fr.gamity.launcher.thomas260913.Launcher;
import fr.gamity.launcher.thomas260913.game.Config;
import fr.gamity.launcher.thomas260913.game.Parser;
import fr.gamity.launcher.thomas260913.game.VersionList;
import fr.gamity.launcher.thomas260913.ui.PanelManager;
import fr.gamity.launcher.thomas260913.ui.panel.Panel;
import fr.gamity.launcher.thomas260913.ui.panels.pages.App;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;

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
    private final TextField forgeModsModrinthField = new TextField();
    private final TextField forgeModPackModrinthField = new TextField();
    private final TextField forgeModPackCurseField = new TextField();
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

        versionList.latest = Launcher.getInstance().getVersionList().latest;
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
                forgeModsModrinthField.setDisable(true);
                forgeModPackModrinthField.setDisable(true);
                forgeModPackCurseField.setDisable(true);
                forgeModsModrinthField.setText("");
                forgeModPackModrinthField.setText("");
                forgeModPackCurseField.setText("");
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
                        forgeModsModrinthField.setDisable(true);
                        forgeModPackModrinthField.setDisable(true);
                        forgeModPackCurseField.setDisable(true);
                        forgeModsModrinthField.setText("");
                        forgeModPackModrinthField.setText("");
                        forgeModPackCurseField.setText("");
                        optifinechkBox.setSelected(false);
                        optifinechkBox.setDisable(true);
                        optifineVersionComboBox.setDisable(true);
                        mcVersionCombobox.setDisable(false);
                    }else{
                        mcVersionCombobox.setDisable(true);
                        mcVersionCombobox.setValue(config.mcinfo.mc.version);
                        forgeVersionField.setDisable(false);
                        if(!config.mcinfo.noBuild){
                            forgeVersionField.setText(config.mcinfo.modLoader.version);
                            forgeModsCurseField.setDisable(false);
                            if(config.mcinfo.modLoader.mods.curseForge != null) {
                                if (config.mcinfo.modLoader.mods.curseForge.json != null) {
                                    forgeModsCurseField.setText(config.mcinfo.modLoader.mods.curseForge.json);
                                } else {
                                    forgeModsCurseField.setText("");
                                }
                            } else {
                                forgeModsCurseField.setText("");
                            }
                            if(config.mcinfo.modLoader.mods.custom != null) {
                                if (config.mcinfo.modLoader.mods.custom.json != null) {
                                    forgeModsCustomField.setText(config.mcinfo.modLoader.mods.custom.json);
                                } else {
                                    forgeModsCustomField.setText("");
                                }
                            } else {
                                forgeModsCustomField.setText("");
                            }
                            forgeModsCustomField.setDisable(false);
                            forgeModsModrinthField.setDisable(false);
                            forgeModPackModrinthField.setDisable(false);
                            forgeModPackCurseField.setDisable(false);
                            if(config.mcinfo.modLoader.mods.modrinth != null) {
                                if (config.mcinfo.modLoader.mods.modrinth.json != null) {
                                    forgeModsModrinthField.setText(config.mcinfo.modLoader.mods.modrinth.json);
                                } else {
                                    forgeModsModrinthField.setText("");
                                }
                            } else {
                                forgeModsModrinthField.setText("");
                            }
                            if(config.mcinfo.modLoader.mods.modrinthModpack != null) {
                                if (config.mcinfo.modLoader.mods.modrinthModpack.json != null) {
                                    forgeModPackModrinthField.setText(config.mcinfo.modLoader.mods.modrinthModpack.json);
                                } else {
                                    forgeModPackModrinthField.setText("");
                                }
                            } else {
                                forgeModPackModrinthField.setText("");
                            }
                            if(config.mcinfo.modLoader.mods.curseForgeModpack != null) {
                                if (config.mcinfo.modLoader.mods.curseForgeModpack.json != null) {
                                    forgeModPackCurseField.setText(config.mcinfo.modLoader.mods.curseForgeModpack.json);
                                } else {
                                    forgeModPackCurseField.setText("");
                                }
                            } else {
                                forgeModPackCurseField.setText("");
                            }
                            optifinechkBox.setDisable(false);
                            optifinechkBox.setSelected(config.mcinfo.modLoader.allowOptifine);
                            optifineVersionComboBox.setDisable(!config.mcinfo.modLoader.allowOptifine);
                            if(config.mcinfo.modLoader.allowOptifine) {
                                if(config.mcinfo.modLoader.optifine.optifineVersion != null) {
                                    optifineVersionComboBox.setValue(config.mcinfo.modLoader.optifine.optifineVersion);
                                }else{
                                    optifineVersionComboBox.setValue("auto");
                                }
                            }else{
                                optifineVersionComboBox.setValue("");
                            }
                        }else{
                            forgeVersionField.setDisable(true);
                            forgeVersionField.setText("");
                            forgeModsCurseField.setDisable(true);
                            forgeModsCurseField.setText("");
                            forgeModsCustomField.setDisable(true);
                            forgeModsCustomField.setText("");
                            forgeModsModrinthField.setDisable(true);
                            forgeModPackModrinthField.setDisable(true);
                            forgeModPackCurseField.setDisable(true);
                            forgeModsModrinthField.setText("");
                            forgeModPackModrinthField.setText("");
                            forgeModPackCurseField.setText("");
                            optifinechkBox.setSelected(false);
                            optifinechkBox.setDisable(true);
                            optifineVersionComboBox.setDisable(true);
                            mcVersionCombobox.setDisable(false);
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
                    }else{
                        autoconnectCheckBox.setSelected(false);
                        serverIpField.setDisable(true);
                        serverPortField.setDisable(true);
                        serverIpField.setText("");
                        serverPortField.setText("");
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
                    forgeModsModrinthField.setDisable(true);
                    forgeModPackModrinthField.setDisable(true);
                    forgeModPackCurseField.setDisable(true);
                    forgeModsModrinthField.setText("");
                    forgeModPackModrinthField.setText("");
                    forgeModPackCurseField.setText("");
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
        title.getStyleClass().add("config-title");
        setLeft(title);
        setTop(title);
        title.setPadding(new Insets(10));
        title.setTextAlignment(TextAlignment.LEFT);
        title.setTranslateY(0d);
        title.setTranslateX(25d);

        mcTypeComboBox.getItems().addAll("vanilla", "forge","snapshot","fabric","neoforge","nobuild");
        mcJavaCombobox.getItems().addAll("21", "17", "8");
        mcVersionCombobox.getItems().add("latest");
        try {
            versionList.versions = Launcher.getInstance().getVersionList().versions.stream().filter(version1 -> Objects.equals(version1.type, "release")).collect(Collectors.toList());
            versionList.versions.forEach(version1->mcVersionCombobox.getItems().add(version1.id));
            optifineList = Launcher.getInstance().getOptifineList();
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
        forgeModsModrinthField.setDisable(true);
        forgeModPackModrinthField.setDisable(true);
        forgeModPackCurseField.setDisable(true);
        serverIpField.setDisable(!autoconnectCheckBox.isSelected());
        serverPortField.setDisable(!autoconnectCheckBox.isSelected());
        mcExtFilesField.setDisable(!mcExtFilesCheckBox.isSelected());
        mcVersionCombobox.setValue("latest");
        mcTypeComboBox.setValue("vanilla");
        mcJavaCombobox.setValue("21");
        forgeVersionField.textProperty().addListener((observable, oldValue, newValue) -> {
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
                case "neoforge":
                case "fabric":
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
                forgeModsModrinthField.setDisable(true);
                forgeModPackModrinthField.setDisable(true);
                forgeModPackCurseField.setDisable(true);
                optifinechkBox.setSelected(false);
                optifinechkBox.setDisable(true);
                optifineVersionComboBox.setDisable(true);
                mcVersionCombobox.setDisable(false);
            }else{
                forgeVersionField.setDisable(false);
                forgeModsCurseField.setDisable(false);
                forgeModsCustomField.setDisable(false);
                forgeModsModrinthField.setDisable(false);
                forgeModPackModrinthField.setDisable(false);
                forgeModPackCurseField.setDisable(false);
                if(Objects.equals(newValue, "fabric") || Objects.equals(newValue, "neoforge")) {
                    optifinechkBox.setSelected(false);
                    optifinechkBox.setDisable(true);
                    mcVersionCombobox.setDisable(false);
                    optifineVersionComboBox.setDisable(true);
                }else{
                    if(Objects.equals(newValue, "nobuild")){
                        forgeVersionField.setDisable(true);
                        forgeModsCurseField.setDisable(true);
                        forgeModsCustomField.setDisable(true);
                        forgeModsModrinthField.setDisable(true);
                        forgeModPackModrinthField.setDisable(true);
                        forgeModPackCurseField.setDisable(true);
                        optifinechkBox.setSelected(false);
                        optifinechkBox.setDisable(true);
                        optifineVersionComboBox.setDisable(true);
                        mcVersionCombobox.setDisable(true);
                    }else {
                        optifinechkBox.setDisable(false);
                        mcVersionCombobox.setDisable(true);
                        optifineVersionComboBox.setDisable(!optifinechkBox.isSelected());
                    }
                }
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
        VBox inputVBox = new VBox();
        inputVBox.setSpacing(10);
        inputVBox.getChildren().addAll(
            addInput("config:", configComboBox),
            addInput("Nom de la config:", nameField),
            addInput("Type de Minecraft:", mcTypeComboBox),
            addInput("Version du mods loader:", forgeVersionField),
            addInput("Liste des mods CurseForge:", forgeModsCurseField, "curseforgeMods"),
            addInput("Liste des mods Custom:", forgeModsCustomField, "customMods"),
            addInput("Liste des mods Modrinth:", forgeModsModrinthField, "ModrinthMods"),
            addInput("Liste des modpack Modrinth:", forgeModPackModrinthField, "ModrinthModpack"),
            addInput("Liste des modpack CurseForge:", forgeModPackCurseField, "curseforgeModpack"),
            addInput("Version de Minecraft:", mcVersionCombobox),
            addInput("Version Java:", mcJavaCombobox),
            addInput("Fichiers externes:", mcExtFilesCheckBox),
            addInput("Fichiers externes url:", mcExtFilesField),
            addInput("Autoconnect:", autoconnectCheckBox),
            addInput("IP du serveur:", serverIpField),
            addInput("Port du serveur:", serverPortField),
            addInput("Optifine:", optifinechkBox),
            addInput("Version:", optifineVersionComboBox)
        );

        ScrollPane scrollPane = new ScrollPane(inputVBox);
        scrollPane.setFitToWidth(true);
        setCanTakeAllSize(scrollPane);
        scrollPane.getStyleClass().add("scrollPane-config");

        scrollPane.setPrefSize(400, 600);
        // Save Button
        Button saveBtn = new Button("Enregistrer");
        saveBtn.getStyleClass().add("save-btn");
        FontAwesomeIconView saveIconView = new FontAwesomeIconView(FontAwesomeIcon.SAVE);
        saveIconView.setSize(App.iconSize);
        saveIconView.getStyleClass().add("save-icon");
        saveBtn.setGraphic(saveIconView);
        setBottom(saveBtn);
        setCenterH(saveBtn);
        saveBtn.setOnMouseClicked(e -> saveConfig());

        // Export Button
        Button exportBtn = new Button("Exporter");
        exportBtn.getStyleClass().add("export-btn");
        MaterialDesignIconView exportIconView = new MaterialDesignIconView(MaterialDesignIcon.FILE_EXPORT);
        exportIconView.setSize(App.iconSize);
        exportIconView.getStyleClass().add("export-icon");
        exportBtn.setGraphic(exportIconView);
        setBottom(exportBtn);
        setCenterH(exportBtn);
        exportBtn.setOnMouseClicked(e -> exportConfig());

        contentPane.add(title,0,0);
        contentPane.add(scrollPane,0,1);
        contentPane.add(saveBtn,0,2);
        contentPane.add(exportBtn,0,3);
        this.panelManager.getStage().setMinHeight(600);
    }
    private HBox addInput(String name, Control field){
        Label label = new Label(name);
        label.getStyleClass().add("config-label");
        label.setMinWidth(300);
        setLeft(label);
        setCanTakeAllSize(label);
        setTop(label);
        label.setTextAlignment(TextAlignment.LEFT);

        if (field instanceof CheckBox) {
            field.getStyleClass().add("config-chk");
        } else if(field instanceof TextField) {
            field.setMaxWidth(300);
            field.getStyleClass().add("config-field");
        }else{
            field.getStyleClass().add("combobox");
        }
        setLeft(field);
        field.setMinWidth(200);
        field.setMaxWidth(200);
        setCanTakeAllSize(field);
        setTop(field);
        field.setTranslateY(-5d);
        HBox hbox = new HBox(label,field);
        hbox.setSpacing(10);
        hbox.setMinHeight(40);
        return hbox;
    }
    private HBox addInput(String name, TextField field, String jsonCreatorType){
        JsonCreator jsonCreator = new JsonCreator(this.panelManager.getStage(),field,field.getText());
        Label label = new Label(name);
        label.getStyleClass().add("config-label");
        label.setMinWidth(300);
        setLeft(label);
        setCanTakeAllSize(label);
        setTop(label);
        label.setTextAlignment(TextAlignment.LEFT);
        Button btn = new Button("json creator");
        btn.getStyleClass().add("save-btn");
        setCanTakeAllSize(btn);
        btn.setTranslateY(-5);
        btn.setOnMouseClicked((e)->{
            switch(jsonCreatorType.toLowerCase()){
                case "curseforgemods":
                    jsonCreator.showCurseModsJsonCreator();
                    break;
                case "curseforgemodpack":
                    jsonCreator.showCurseModPackJsonCreator();
                    break;
                case "custommods":
                    jsonCreator.showCustomModsJsonCreator();
                    break;
                case "modrinthmods":
                    jsonCreator.showModrinthModsJsonCreator();
                    break;
                case "modrinthmodpack":
                    jsonCreator.showModrinthModPackJsonCreator();
                    break;
            }
        });
        btn.setDisable(field.isDisable() && !btn.isDisable());
        mcTypeComboBox.valueProperty().addListener((e,old,newValue)-> btn.setDisable(!Objects.equals(newValue, "forge")));
        field.setMaxWidth(200);
        field.getStyleClass().add("config-field");
        field.setMinWidth(200);
        setLeft(field);
        setCanTakeAllSize(field);
        setTop(field);
        field.setTranslateY(-5d);
        HBox hbox = new HBox(label,field,btn);
        hbox.setSpacing(10);
        hbox.setMinHeight(40);
        return hbox;
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
        config.mcinfo.mc = new Config.CustomServer.McInfo.Mc();
        config.mcinfo.type = mcTypeComboBox.getValue();
        if(!Objects.equals(config.mcinfo.type, "nobuild")) {
        if(!Objects.equals(config.mcinfo.type, "vanilla") && !Objects.equals(config.mcinfo.type, "snapshot")){
            config.mcinfo.modLoader = new Config.CustomServer.McInfo.ModLoader();
            config.mcinfo.modLoader.version = forgeVersionField.getText();
            config.mcinfo.modLoader.mods = new Config.CustomServer.McInfo.ModLoader.Mods();
            config.mcinfo.modLoader.mods.curseForge = new Config.CustomServer.McInfo.ModLoader.Mods.CurseForge();
            config.mcinfo.modLoader.mods.custom = new Config.CustomServer.McInfo.ModLoader.Mods.Custom();
            config.mcinfo.modLoader.mods.modrinth = new Config.CustomServer.McInfo.ModLoader.Mods.Modrinth();
            config.mcinfo.modLoader.mods.modrinthModpack = new Config.CustomServer.McInfo.ModLoader.Mods.ModrinthModpack();
            config.mcinfo.modLoader.mods.curseForgeModpack = new Config.CustomServer.McInfo.ModLoader.Mods.CurseForgeModpack();
            if(!Objects.equals(forgeModsCustomField.getText(), "")) {
                config.mcinfo.modLoader.mods.custom.json = forgeModsCustomField.getText();
            }
            if(!Objects.equals(forgeModsCurseField.getText(), "")) {
                config.mcinfo.modLoader.mods.curseForge.json = forgeModsCurseField.getText();
            }
            if(!Objects.equals(forgeModsModrinthField.getText(), "")) {
                config.mcinfo.modLoader.mods.modrinth.json = forgeModsModrinthField.getText();
            }
            if(!Objects.equals(forgeModPackModrinthField.getText(), "")) {
                config.mcinfo.modLoader.mods.modrinthModpack.json = forgeModPackModrinthField.getText();
            }
            if(!Objects.equals(forgeModPackCurseField.getText(), "")) {
                config.mcinfo.modLoader.mods.curseForgeModpack.json = forgeModPackCurseField.getText();
            }
            if(optifinechkBox.isSelected()){
                config.mcinfo.modLoader.allowOptifine = true;
                config.mcinfo.modLoader.optifine = new Config.CustomServer.McInfo.ModLoader.Optifine();
                if(!Objects.equals(optifineVersionComboBox.getValue(), "auto")) {
                    config.mcinfo.modLoader.optifine.optifineVersion = optifineVersionComboBox.getValue();
                }
            }
        }
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
        }else{
            config.mcinfo.noBuild = true;
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
                ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                writer.writeValue(destinationPath.toFile(), config);
                Launcher.getInstance().showAlert(Alert.AlertType.INFORMATION, "Sauvegarde", "Configuration sauvegardée avec succès !");
                Platform.runLater(() -> {
                    App app = new App();
                    panelManager.showPanel(app);
                    JsonClient jsonClient = new JsonClient();
                    app.setPage(jsonClient, app.clientBtn);
                    jsonClient.setPage(new CreateConfig(), jsonClient.ConfigBtn);
                });
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
        config.mcinfo.mc = new Config.CustomServer.McInfo.Mc();
        config.mcinfo.type = mcTypeComboBox.getValue();
        if(!Objects.equals(config.mcinfo.type, "nobuild")) {
            if (!Objects.equals(config.mcinfo.type, "vanilla") || !Objects.equals(config.mcinfo.type, "snapshot")) {
                config.mcinfo.modLoader = new Config.CustomServer.McInfo.ModLoader();
                config.mcinfo.modLoader.version = forgeVersionField.getText();
                config.mcinfo.modLoader.mods = new Config.CustomServer.McInfo.ModLoader.Mods();
                config.mcinfo.modLoader.mods.curseForge = new Config.CustomServer.McInfo.ModLoader.Mods.CurseForge();
                config.mcinfo.modLoader.mods.custom = new Config.CustomServer.McInfo.ModLoader.Mods.Custom();
                if (forgeModsCustomField.getText() != null) {
                    config.mcinfo.modLoader.mods.custom.json = forgeModsCustomField.getText();
                }
                if (forgeModsCurseField.getText() != null) {
                    config.mcinfo.modLoader.mods.curseForge.json = forgeModsCurseField.getText();
                }
                if (optifinechkBox.isSelected()) {
                    config.mcinfo.modLoader.allowOptifine = true;
                    config.mcinfo.modLoader.optifine = new Config.CustomServer.McInfo.ModLoader.Optifine();
                    if (!Objects.equals(optifineVersionComboBox.getValue(), "auto")) {
                        config.mcinfo.modLoader.optifine.optifineVersion = optifineVersionComboBox.getValue();
                    }
                }
            }
        }else{
            config.mcinfo.type = "vanilla";
            config.mcinfo.noBuild = true;
        }
        if (Objects.equals(mcVersionCombobox.getValue(), "latest")) {
            switch (mcTypeComboBox.getValue()) {
                case "forge":
                case "vanilla":
                    config.mcinfo.mc.version = versionList.latest.release;
                    break;
                case "snapshot":
                    config.mcinfo.mc.version = versionList.latest.snapshot;
                    break;
            }
        } else {
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
                ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                writer.writeValue(file, config);
                Launcher.getInstance().showAlert(Alert.AlertType.INFORMATION, "Exportation", "Configuration exportée avec succès !");
            } catch (IOException e) {
                Launcher.getInstance().showErrorDialog(e, this.panelManager.getStage());
            }
        }
    }
    private static String getVanillaVersionFromForge(@NotNull String forgeVersion){
        String[] data = forgeVersion.split("-");
        return data[0];
    }
}
