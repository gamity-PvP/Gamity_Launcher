package fr.gamity.launcher.thomas260913.JsonCreator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import fr.gamity.launcher.thomas260913.Launcher;
import fr.gamity.launcher.thomas260913.ui.panels.pages.App;
import fr.gamity.launcher.thomas260913.ui.panels.pages.content.Parser;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class JsonCreator {
    Stage ownerStage;
    TextField field;
    String json;

    public JsonCreator(Stage ownerStage, TextField field, String json) {
        this.ownerStage = ownerStage;
        this.field = field;
        if (!Objects.equals(json, "")) {
            this.json = json;
        } else {
            this.json = "{}";
        }
    }

    public void showModrinthModsJsonCreator() {
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vbox, 300, 200);
        Stage newStage = new Stage();
        newStage.setMinWidth(854);
        newStage.setMinHeight(480);
        newStage.setWidth(1280);
        newStage.setHeight(720);
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(ownerStage);
        newStage.getIcons().add(new Image("images/icon.png"));
        newStage.setScene(scene);
        newStage.setTitle("Json Creator");

        TextField name = new TextField();
        TextField versionId = new TextField();
        TextField versionNumber = new TextField();
        TextField projectReference = new TextField();
        Button addModsBtn = new Button();
        Button saveBtn = new Button("Enregistrer");
        versionId.setMaxWidth(300);
        versionNumber.setMaxWidth(300);
        projectReference.setMaxWidth(300);
        name.setMaxWidth(300);
        Parser.ModsJsonParser.ModrinthParser.ModrinthList tempModrinthList = new Parser.ModsJsonParser.ModrinthParser.ModrinthList();
        try {
            ObjectMapper mapper = new ObjectMapper();
            tempModrinthList = mapper.readValue(json, Parser.ModsJsonParser.ModrinthParser.ModrinthList.class);
        } catch (IOException e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            Launcher.getInstance().getLogger().debug(json);
            Launcher.getInstance().showErrorDialog(e, ownerStage);
        }
        final Parser.ModsJsonParser.ModrinthParser.ModrinthList modrinthList = tempModrinthList;

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getStyleClass().add("combobox");
        comboBox.getItems().add("créer un nouveau mods");
        comboBox.setValue("créer un nouveau mods");
        if (modrinthList.modrinthMods != null) {
            modrinthList.modrinthMods.forEach(mods -> comboBox.getItems().add(mods.name));
        } else {
            modrinthList.modrinthMods = new ArrayList<>();
        }
        comboBox.valueProperty().addListener((e, old, newValue) -> {
            if (!Objects.equals(newValue, "créer un nouveau mods")) {
                addModsBtn.setText("enregistrer le mods");
                Parser.ModsJsonParser.ModrinthParser.ModrinthList.Mods mods = findModrinthModsByName(modrinthList.modrinthMods, newValue);
                if (mods != null) {
                    name.setText(newValue);
                    versionId.setText(mods.versionId);
                    versionNumber.setText(mods.versionNumber);
                    projectReference.setText(mods.projectReference);
                } else {
                    comboBox.setValue("créer un nouveau mods");
                }
            } else {
                addModsBtn.setText("ajouter le mods");
                name.setText("");
                versionId.setText("");
                versionNumber.setText("");
                projectReference.setText("");
            }
        });

        
        TextArea info = new TextArea();
        info.setWrapText(true);
        textAreaSising(info,scene,newStage);
        info.setEditable(false);
        info.setText("Info :\npour ajouter un mods modrinth il vous faudra soit le versionId soit versionNumber et projectReference\nevidemment vous ne pouvez mettre soit uniquement le versionId soit uniquement le versionNumber et projectReference sinon des erreurs pourrait survenir");

        name.setPromptText("Entrez le nom du mod ici");
        versionId.setPromptText("Entrez le versionId ici");
        versionNumber.setPromptText("Entrez le versionNumber ici");
        projectReference.setPromptText("Entrez le projectReference ici");

        TextArea textArea = new TextArea();
        textAreaSising(textArea,scene,newStage);
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            String formattedJson = writer.writeValueAsString(modrinthList);
            textArea.setText(formattedJson);
        } catch (IOException e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            Launcher.getInstance().showErrorDialog(e, ownerStage);
        }

        addModsBtn.setText("ajouter le mods");
        addModsBtn.getStyleClass().add("save-btn");
        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.SAVE);
        iconView.setSize(App.iconSize);
        iconView.getStyleClass().add("save-icon");
        addModsBtn.setGraphic(iconView);
        addModsBtn.setOnMouseClicked(e -> {
            if (Objects.equals(comboBox.getValue(), "créer un nouveau mods")) {
                Parser.ModsJsonParser.ModrinthParser.ModrinthList.Mods mods = new Parser.ModsJsonParser.ModrinthParser.ModrinthList.Mods();
                if (versionId.getText() != null) {
                    mods.versionId = versionId.getText();
                    mods.name = name.getText();
                    modrinthList.modrinthMods.add(mods);
                    comboBox.setValue("créer un nouveau mods");
                    comboBox.getItems().add(name.getText());
                    addModsBtn.setText("ajouter le mods");
                    name.setText("");
                    versionId.setText("");
                    versionNumber.setText("");
                    projectReference.setText("");
                } else if (projectReference.getText() != null && versionNumber.getText() != null) {
                    mods.projectReference = projectReference.getText();
                    mods.versionNumber = versionNumber.getText();
                    mods.name = name.getText();
                    modrinthList.modrinthMods.add(mods);
                    comboBox.getItems().add(name.getText());
                    comboBox.setValue("créer un nouveau mods");
                    addModsBtn.setText("ajouter le mods");
                    name.setText("");
                    versionId.setText("");
                    versionNumber.setText("");
                    projectReference.setText("");
                } else {
                    throw new NullPointerException("le mods doit contenir soit un versionId soit le projectReference et versionNumber");
                }
            } else {
                Parser.ModsJsonParser.ModrinthParser.ModrinthList.Mods mods = findModrinthModsByName(modrinthList.modrinthMods, comboBox.getValue());
                if (mods != null) {
                    if (!Objects.equals(versionId.getText(), "")) {
                        modrinthList.modrinthMods.remove(mods);
                        mods.versionId = versionId.getText();
                        mods.name = name.getText();
                        modrinthList.modrinthMods.add(mods);
                        comboBox.setValue("créer un nouveau mods");
                        comboBox.getItems().add(name.getText());
                        addModsBtn.setText("ajouter le mods");
                        name.setText("");
                        versionId.setText("");
                        versionNumber.setText("");
                        projectReference.setText("");
                    } else if (!Objects.equals(projectReference.getText(), "") && !Objects.equals(versionNumber.getText(), "")) {
                        modrinthList.modrinthMods.remove(mods);
                        mods.projectReference = projectReference.getText();
                        mods.versionNumber = versionNumber.getText();
                        mods.name = name.getText();
                        modrinthList.modrinthMods.add(mods);
                        comboBox.getItems().add(name.getText());
                        comboBox.setValue("créer un nouveau mods");
                        addModsBtn.setText("ajouter le mods");
                        name.setText("");
                        versionId.setText("");
                        versionNumber.setText("");
                        projectReference.setText("");
                    } else {
                        throw new NullPointerException("le mods doit contenir soit un versionId soit le projectReference et versionNumber");
                    }
                } else {
                    throw new NullPointerException("le mods selectionner n'est pas dans la liste");
                }
            }
            try {
                ObjectMapper mapper = new ObjectMapper();
                ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                String formattedJson = writer.writeValueAsString(modrinthList);
                textArea.setText(formattedJson);
            } catch (IOException ex) {
                Launcher.getInstance().getLogger().printStackTrace(ex);
                Launcher.getInstance().showErrorDialog(ex, ownerStage);
            }
        });

        saveBtn.getStyleClass().add("save-btn");
        FontAwesomeIconView iconView2 = new FontAwesomeIconView(FontAwesomeIcon.SAVE);
        iconView2.setSize(App.iconSize);
        iconView2.getStyleClass().add("save-icon");
        saveBtn.setGraphic(iconView2);
        saveBtn.setOnMouseClicked(e -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(modrinthList);
                field.setText(json);
            } catch (JsonProcessingException ex) {
                Launcher.getInstance().getLogger().printStackTrace(ex);
                Launcher.getInstance().showErrorDialog(ex, ownerStage);
            }
            newStage.close();
        });

        vbox.getChildren().addAll(info, comboBox, name, versionId, versionNumber, projectReference, addModsBtn, textArea, saveBtn);

        newStage.getScene().getStylesheets().add("css/JsonCreator.css");
        newStage.show();
    }

    public void showModrinthModPackJsonCreator() {
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vbox, 300, 200);
        Stage newStage = new Stage();
        newStage.setMinWidth(854);
        newStage.setMinHeight(480);
        newStage.setWidth(1280);
        newStage.setHeight(720);
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(ownerStage);
        newStage.getIcons().add(new Image("images/icon.png"));
        newStage.setScene(scene);
        newStage.setTitle("Json Creator");

        TextField versionId = new TextField();
        TextField versionNumber = new TextField();
        TextField projectReference = new TextField();
        CheckBox extFiles = new CheckBox("fichier externe");
        TextField excludedMods = new TextField();
        Button saveBtn = new Button("Enregistrer");
        versionId.setMaxWidth(300);
        versionNumber.setMaxWidth(300);
        projectReference.setMaxWidth(300);
        excludedMods.setMaxWidth(300);
        Parser.ModsJsonParser.ModrinthModpackParser.ModrinthPackInfoJson tempModPack = new Parser.ModsJsonParser.ModrinthModpackParser.ModrinthPackInfoJson();

        try {
            ObjectMapper mapper = new ObjectMapper();
            tempModPack = mapper.readValue(json, Parser.ModsJsonParser.ModrinthModpackParser.ModrinthPackInfoJson.class);
        } catch (IOException e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            Launcher.getInstance().showErrorDialog(e, ownerStage);
        }
        final Parser.ModsJsonParser.ModrinthModpackParser.ModrinthPackInfoJson modPack = tempModPack;

        
        TextArea info = new TextArea();
        info.setWrapText(true);
        textAreaSising(info,scene,newStage);
        info.setEditable(false);
        info.setText("Info :\npour ajouter un modpack modrinth il vous faudra soit le versionId soit versionNumber et projectReference\nevidemment vous ne pouvez mettre soit uniquement le versionId soit uniquement le versionNumber et projectReference sinon des erreurs pourrait survenir\nles fichier externe sont evidemment les config et mods exclu sont les mods qui ne seront pas télécharger\npour les mods exclus la syntaxe est \"mods1, mods2, mods3\"");

        versionId.setPromptText("Entrez le versionId ici");
        versionNumber.setPromptText("Entrez le versionNumber ici");
        projectReference.setPromptText("Entrez le projectReference ici");
        excludedMods.setPromptText("Entrez les mods exlus ici");

        TextArea textArea = new TextArea();
        textAreaSising(textArea,scene,newStage);
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            String formattedJson = writer.writeValueAsString(modPack);
            textArea.setText(formattedJson);
        } catch (IOException e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            Launcher.getInstance().showErrorDialog(e, ownerStage);
        }

        saveBtn.getStyleClass().add("save-btn");
        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.SAVE);
        iconView.setSize(App.iconSize);
        iconView.getStyleClass().add("save-icon");
        saveBtn.setGraphic(iconView);
        saveBtn.setOnMouseClicked(e -> {
            if (!Objects.equals(versionId.getText(), "")) {
                modPack.versionId = versionId.getText();
                modPack.excluded = excludedMods.getText().split(", ");
                modPack.extFile = extFiles.isSelected();
            } else if (!Objects.equals(projectReference.getText(), "") && !Objects.equals(versionNumber.getText(), "")) {
                modPack.projectReference = projectReference.getText();
                modPack.versionNumber = versionNumber.getText();
                modPack.excluded = excludedMods.getText().split(", ");
                modPack.extFile = extFiles.isSelected();
            } else {
                throw new NullPointerException("le mods doit contenir soit un versionId soit le projectReference et versionNumber");
            }
            try {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(modPack);
                field.setText(json);
            } catch (JsonProcessingException ex) {
                Launcher.getInstance().getLogger().printStackTrace(ex);
                Launcher.getInstance().showErrorDialog(ex, ownerStage);
            }
            newStage.close();
        });

        vbox.getChildren().addAll(info, versionId, versionNumber, projectReference, extFiles, excludedMods, textArea, saveBtn);

        newStage.getScene().getStylesheets().add("css/JsonCreator.css");
        newStage.show();

    }

    public void showCurseModsJsonCreator() {
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vbox, 300, 200);
        Stage newStage = new Stage();
        newStage.setMinWidth(854);
        newStage.setMinHeight(480);
        newStage.setWidth(1280);
        newStage.setHeight(720);
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(ownerStage);
        newStage.getIcons().add(new Image("images/icon.png"));
        newStage.setScene(scene);
        newStage.setTitle("Json Creator");

        TextField name = new TextField();
        TextField fileId = new TextField();
        TextField projectId = new TextField();
        Button addModsBtn = new Button();
        Button saveBtn = new Button("Enregistrer");
        name.setMaxWidth(300);
        fileId.setMaxWidth(300);
        projectId.setMaxWidth(300);
        Parser.ModsJsonParser.CurseParser.CurseList tempCurseList = new Parser.ModsJsonParser.CurseParser.CurseList();
        try {
            ObjectMapper mapper = new ObjectMapper();
            tempCurseList = mapper.readValue(json, Parser.ModsJsonParser.CurseParser.CurseList.class);
        } catch (IOException e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            Launcher.getInstance().getLogger().debug(json);
            Launcher.getInstance().showErrorDialog(e, ownerStage);
        }
        final Parser.ModsJsonParser.CurseParser.CurseList curseList = tempCurseList;

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getStyleClass().add("combobox");
        comboBox.getItems().add("créer un nouveau mods");
        comboBox.setValue("créer un nouveau mods");
        if (curseList.curseFiles != null) {
            curseList.curseFiles.forEach(mods -> comboBox.getItems().add(mods.name));
        } else {
            curseList.curseFiles = new ArrayList<>();
        }
        comboBox.valueProperty().addListener((e, old, newValue) -> {
            if (!Objects.equals(newValue, "créer un nouveau mods")) {
                addModsBtn.setText("enregistrer le mods");
                Parser.ModsJsonParser.CurseParser.CurseList.Curse mods = findCurseModsByName(curseList.curseFiles, newValue);
                if (mods != null) {
                    name.setText(newValue);
                    fileId.setText(String.valueOf(mods.fileID));
                    projectId.setText(String.valueOf(mods.projectID));
                } else {
                    comboBox.setValue("créer un nouveau mods");
                }
            } else {
                addModsBtn.setText("ajouter le mods");
                name.setText("");
                fileId.setText("");
                projectId.setText("");
            }
        });

        
        TextArea info = new TextArea();
        info.setWrapText(true);
        textAreaSising(info,scene,newStage);
        info.setEditable(false);
        info.setText("Info :\npour ajouter des mods CurseForge il vous faudra fileId et projectId trouvable sur curseforge");

        name.setPromptText("Entrez le nom du mod ici");
        fileId.setPromptText("Entrez le fileId ici");
        projectId.setPromptText("Entrez le projectId ici");

        TextArea textArea = new TextArea();
        textAreaSising(textArea,scene,newStage);
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            String formattedJson = writer.writeValueAsString(curseList);
            textArea.setText(formattedJson);
        } catch (IOException e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            Launcher.getInstance().getLogger().debug(json);
            Launcher.getInstance().showErrorDialog(e, ownerStage);
        }

        addModsBtn.setText("ajouter le mods");
        addModsBtn.getStyleClass().add("save-btn");
        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.SAVE);
        iconView.setSize(App.iconSize);
        iconView.getStyleClass().add("save-icon");
        addModsBtn.setGraphic(iconView);
        addModsBtn.setOnMouseClicked(e -> {
            if (Objects.equals(comboBox.getValue(), "créer un nouveau mods")) {
                Parser.ModsJsonParser.CurseParser.CurseList.Curse mods = new Parser.ModsJsonParser.CurseParser.CurseList.Curse();
                if (fileId.getText() != null && projectId.getText() != null) {
                    mods.fileID = Integer.parseInt(fileId.getText());
                    mods.projectID = Integer.parseInt(projectId.getText());
                    mods.name = name.getText();
                    curseList.curseFiles.add(mods);
                    comboBox.setValue("créer un nouveau mods");
                    comboBox.getItems().add(name.getText());
                    addModsBtn.setText("ajouter le mods");
                    name.setText("");
                    fileId.setText("");
                    projectId.setText("");
                } else {
                    throw new NullPointerException("le mods doit contenir le projectId et fileId");
                }
            } else {
                Parser.ModsJsonParser.CurseParser.CurseList.Curse mods = findCurseModsByName(curseList.curseFiles, comboBox.getValue());
                if (mods != null) {
                    if (!Objects.equals(fileId.getText(), "") && !Objects.equals(projectId.getText(), "")) {
                        curseList.curseFiles.remove(mods);
                        mods.fileID = Integer.parseInt(fileId.getText());
                        mods.projectID = Integer.parseInt(projectId.getText());
                        mods.name = name.getText();
                        curseList.curseFiles.add(mods);
                        comboBox.setValue("créer un nouveau mods");
                        comboBox.getItems().add(name.getText());
                        addModsBtn.setText("ajouter le mods");
                        name.setText("");
                        fileId.setText("");
                        projectId.setText("");
                    } else {
                        throw new NullPointerException("le mods doit contenir le projectId et fileId");
                    }
                } else {
                    throw new NullPointerException("le mods selectionner n'est pas dans la liste");
                }
            }
            try {
                ObjectMapper mapper = new ObjectMapper();
                ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                String formattedJson = writer.writeValueAsString(curseList);
                textArea.setText(formattedJson);
            } catch (IOException ex) {
                Launcher.getInstance().getLogger().printStackTrace(ex);
                Launcher.getInstance().showErrorDialog(ex, ownerStage);
            }
        });

        saveBtn.getStyleClass().add("save-btn");
        FontAwesomeIconView iconView2 = new FontAwesomeIconView(FontAwesomeIcon.SAVE);
        iconView2.setSize(App.iconSize);
        iconView2.getStyleClass().add("save-icon");
        saveBtn.setGraphic(iconView2);
        saveBtn.setOnMouseClicked(e -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(curseList);
                field.setText(json);
            } catch (JsonProcessingException ex) {
                Launcher.getInstance().getLogger().printStackTrace(ex);
                Launcher.getInstance().showErrorDialog(ex, ownerStage);
            }
            newStage.close();
        });

        vbox.getChildren().addAll(info, comboBox, name, fileId, projectId, addModsBtn, textArea, saveBtn);

        newStage.getScene().getStylesheets().add("css/JsonCreator.css");
        newStage.show();
    }

    public void showCurseModPackJsonCreator() {
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vbox, 300, 200);
        Stage newStage = new Stage();
        newStage.setMinWidth(854);
        newStage.setMinHeight(480);
        newStage.setWidth(1280);
        newStage.setHeight(720);
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(ownerStage);
        newStage.getIcons().add(new Image("images/icon.png"));
        newStage.setScene(scene);
        newStage.setTitle("Json Creator");

        TextField fileId = new TextField();
        TextField projectId = new TextField();
        CheckBox extFiles = new CheckBox("fichier externe");
        TextField excludedMods = new TextField();
        Button saveBtn = new Button("Enregistrer");
        fileId.setMaxWidth(300);
        projectId.setMaxWidth(300);
        excludedMods.setMaxWidth(300);
        Parser.ModsJsonParser.CurseModPackParser.CurseModPackInfoJson tempmodPack = new Parser.ModsJsonParser.CurseModPackParser.CurseModPackInfoJson();

        try {
            ObjectMapper mapper = new ObjectMapper();
            tempmodPack = mapper.readValue(json, Parser.ModsJsonParser.CurseModPackParser.CurseModPackInfoJson.class);
        } catch (IOException e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            Launcher.getInstance().getLogger().debug(json);
            Launcher.getInstance().showErrorDialog(e, ownerStage);
        }
        final Parser.ModsJsonParser.CurseModPackParser.CurseModPackInfoJson modPack = tempmodPack;

        
        TextArea info = new TextArea();
        info.setWrapText(true);
        textAreaSising(info,scene,newStage);
        info.setEditable(false);
        info.setText("Info :\npour ajouter un modpack CurseForge il vous faudra fileId et projectId\nles fichier externe sont evidemment les config et mods exclu sont les mods qui ne seront pas télécharger\npour les mods exclus la syntaxe est \"mods1, mods2, mods3\"");

        fileId.setPromptText("Entrez le fileId ici");
        projectId.setPromptText("Entrez le projectId ici");
        excludedMods.setPromptText("Entrez les mods exlus ici");

        TextArea textArea = new TextArea();
        textAreaSising(textArea,scene,newStage);
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            String formattedJson = writer.writeValueAsString(modPack);
            textArea.setText(formattedJson);
        } catch (IOException e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            Launcher.getInstance().showErrorDialog(e, ownerStage);
        }

        saveBtn.getStyleClass().add("save-btn");
        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.SAVE);
        iconView.setSize(App.iconSize);
        iconView.getStyleClass().add("save-icon");
        saveBtn.setGraphic(iconView);
        saveBtn.setOnMouseClicked(e -> {
            if (!Objects.equals(projectId.getText(), "") && !Objects.equals(fileId.getText(), "")) {
                modPack.projectID = Integer.parseInt(projectId.getText());
                modPack.fileID = Integer.parseInt(fileId.getText());
                modPack.excluded = excludedMods.getText().split(", ");
                modPack.extFile = extFiles.isSelected();
            } else {
                throw new NullPointerException("le mods doit contenir le projectId et fileId");
            }
            try {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(modPack);
                field.setText(json);
            } catch (JsonProcessingException ex) {
                Launcher.getInstance().getLogger().printStackTrace(ex);
                Launcher.getInstance().showErrorDialog(ex, ownerStage);
            }
            newStage.close();
        });

        vbox.getChildren().addAll(info, fileId, projectId, extFiles, excludedMods, textArea, saveBtn);

        newStage.getScene().getStylesheets().add("css/JsonCreator.css");
        newStage.show();

    }

    public void showCustomModsJsonCreator() {
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vbox);
        Stage newStage = new Stage();
        newStage.setMinWidth(854);
        newStage.setMinHeight(480);
        newStage.setWidth(1280);
        newStage.setHeight(720);
        newStage.setMinWidth(854);
        newStage.setMinHeight(480);
        newStage.setWidth(1280);
        newStage.setHeight(720);
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(ownerStage);
        newStage.getIcons().add(new Image("images/icon.png"));
        newStage.setScene(scene);
        newStage.setTitle("Json Creator");

        TextField name = new TextField();
        TextField downloadURL = new TextField();
        TextField size = new TextField();
        TextField sha1 = new TextField();
        name.setMaxWidth(300);
        downloadURL.setMaxWidth(300);
        size.setMaxWidth(300);
        sha1.setMaxWidth(300);

        Button addModsBtn = new Button();
        Button saveBtn = new Button("Enregistrer");
        Parser.ModsJsonParser.ModsParser.ModsList modsListTemp = new Parser.ModsJsonParser.ModsParser.ModsList();

        try {
            ObjectMapper mapper = new ObjectMapper();
            modsListTemp = mapper.readValue(json, Parser.ModsJsonParser.ModsParser.ModsList.class);
        } catch (IOException e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            Launcher.getInstance().getLogger().debug(json);
            Launcher.getInstance().showErrorDialog(e, ownerStage);
        }
        final Parser.ModsJsonParser.ModsParser.ModsList modsList = modsListTemp;
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getStyleClass().add("combobox");
        comboBox.getItems().add("créer un nouveau mods");
        comboBox.setValue("créer un nouveau mods");
        if (modsList.mods != null) {
            modsList.mods.forEach(mods -> comboBox.getItems().add(mods.name));
        } else {
            modsList.mods = new ArrayList<>();
        }
        comboBox.valueProperty().addListener((e, old, newValue) -> {
            if (!Objects.equals(newValue, "créer un nouveau mods")) {
                addModsBtn.setText("enregistrer le mods");
                Parser.ModsJsonParser.ModsParser.ModsList.Mods mods = findCustomModsByName(modsList.mods, newValue);
                if (mods != null) {
                    name.setText(newValue);
                    downloadURL.setText(mods.downloadURL);
                    size.setText(String.valueOf(mods.size));
                    sha1.setText(mods.sha1);
                } else {
                    name.setText("");
                    downloadURL.setText("");
                    size.setText("");
                    sha1.setText("");
                    comboBox.setValue("créer un nouveau mods");
                }
            } else {
                addModsBtn.setText("ajouter le mods");
                name.setText("");
                downloadURL.setText("");
                size.setText("");
                sha1.setText("");
            }
        });

        
        TextArea info = new TextArea();
        info.setWrapText(true);
        textAreaSising(info,scene,newStage);
        info.setEditable(false);
        info.setText("Info :\npour ajouter un mods custom il vous faudra un nom de fichier (nom), un lien direct de téléchargement, la taille en octet (precit) et le sha1 du fichier");
        name.setPromptText("Entrez le nom du mod ici");
        downloadURL.setPromptText("Entrez le downloadURL (lien direct) ici");
        size.setPromptText("Entrez la taille (en octet) ici");
        sha1.setPromptText("Entrez le sha1 du mods ici");
        TextArea textArea = new TextArea();
        textAreaSising(textArea,scene,newStage);

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            String formattedJson = writer.writeValueAsString(modsList);
            textArea.setText(formattedJson);
        } catch (IOException e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            Launcher.getInstance().showErrorDialog(e, ownerStage);
        }

        addModsBtn.setText("ajouter le mods");
        addModsBtn.getStyleClass().add("save-btn");
        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.SAVE);
        iconView.setSize(App.iconSize);
        iconView.getStyleClass().add("save-icon");
        addModsBtn.setGraphic(iconView);
        addModsBtn.setOnMouseClicked(e -> {
            if (Objects.equals(comboBox.getValue(), "créer un nouveau mods")) {
                Parser.ModsJsonParser.ModsParser.ModsList.Mods mods = new Parser.ModsJsonParser.ModsParser.ModsList.Mods();
                if (!Objects.equals(downloadURL.getText(), "") && !Objects.equals(size.getText(), "") && !Objects.equals(sha1.getText(), "") && !Objects.equals(name.getText(), "")) {
                    mods.downloadURL = downloadURL.getText();
                    if (name.getText().endsWith(".jar")) {
                        mods.name = name.getText();
                    } else {
                        mods.name = name.getText() + ".jar";
                    }
                    mods.sha1 = sha1.getText();
                    mods.size = Integer.parseInt(size.getText());
                    modsList.mods.add(mods);
                    comboBox.setValue("créer un nouveau mods");
                    comboBox.getItems().add(name.getText());
                    addModsBtn.setText("ajouter le mods");
                    name.setText("");
                    downloadURL.setText("");
                    size.setText("");
                    sha1.setText("");
                } else {
                    throw new NullPointerException("le mods doit contenir le projectId et fileId");
                }
            } else {
                Parser.ModsJsonParser.ModsParser.ModsList.Mods mods = findCustomModsByName(modsList.mods, comboBox.getValue());
                if (mods != null) {
                    if (downloadURL.getText() != null && size.getText() != null && sha1.getText() != null && name.getText() != null) {
                        modsList.mods.remove(mods);
                        mods.downloadURL = downloadURL.getText();
                        mods.name = name.getText();
                        mods.sha1 = sha1.getText();
                        mods.size = Integer.parseInt(size.getText());
                        modsList.mods.add(mods);
                        comboBox.setValue("créer un nouveau mods");
                        addModsBtn.setText("ajouter le mods");
                        name.setText("");
                        downloadURL.setText("");
                        size.setText("");
                        sha1.setText("");
                    } else {
                        throw new NullPointerException("le mods doit contenir le projectId et fileId");
                    }
                } else {
                    throw new NullPointerException("le mods selectionner n'est pas dans la liste");
                }
            }

            try {
                ObjectMapper mapper = new ObjectMapper();
                ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                String formattedJson = writer.writeValueAsString(modsList);
                textArea.setText(formattedJson);
            } catch (IOException ex) {
                Launcher.getInstance().getLogger().printStackTrace(ex);
                Launcher.getInstance().showErrorDialog(ex, ownerStage);
            }
        });

        saveBtn.getStyleClass().add("save-btn");
        FontAwesomeIconView iconView2 = new FontAwesomeIconView(FontAwesomeIcon.SAVE);
        iconView2.setSize(App.iconSize);
        iconView2.getStyleClass().add("save-icon");
        saveBtn.setGraphic(iconView2);
        saveBtn.setOnMouseClicked(e -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(modsList);
                field.setText(json);
            } catch (JsonProcessingException ex) {
                Launcher.getInstance().getLogger().printStackTrace(ex);
                Launcher.getInstance().showErrorDialog(ex, ownerStage);
            }
            newStage.close();
        });

        vbox.getChildren().addAll(info, comboBox, name, downloadURL, sha1, size, addModsBtn, textArea, saveBtn);
        newStage.getScene().getStylesheets().add("css/JsonCreator.css");
        newStage.show();
    }

    private Parser.ModsJsonParser.ModrinthParser.ModrinthList.Mods findModrinthModsByName(List<Parser.ModsJsonParser.ModrinthParser.ModrinthList.Mods> modsList, String name) {
        for (Parser.ModsJsonParser.ModrinthParser.ModrinthList.Mods mods : modsList) {
            if (mods.name.equalsIgnoreCase(name)) {
                return mods;
            }
        }
        return null;
    }

    private Parser.ModsJsonParser.CurseParser.CurseList.Curse findCurseModsByName(List<Parser.ModsJsonParser.CurseParser.CurseList.Curse> modsList, String name) {
        for (Parser.ModsJsonParser.CurseParser.CurseList.Curse mods : modsList) {
            if (mods.name.equalsIgnoreCase(name)) {
                return mods;
            }
        }
        return null;
    }

    private Parser.ModsJsonParser.ModsParser.ModsList.Mods findCustomModsByName(List<Parser.ModsJsonParser.ModsParser.ModsList.Mods> modsList, String name) {
        for (Parser.ModsJsonParser.ModsParser.ModsList.Mods mods : modsList) {
            if (mods.name.equalsIgnoreCase(name)) {
                return mods;
            }
        }
        return null;
    }

    private double computeTextWidth(Text text, String textString) {
        text.setText(textString);
        return text.getLayoutBounds().getWidth();
    }

    private void textAreaSising(TextArea textArea,Scene scene,Stage stage) {
        Text textNode = new Text();
        textNode.setFont(textArea.getFont());
        textArea.setPrefRowCount(textArea.getText().split("\n").length+1);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            double width = computeTextWidth(textNode, newValue) + 20; // Ajustement pour les marges
            textArea.setMaxWidth(Math.min(width, scene.getWidth()/2));
            textArea.setMinWidth(scene.getWidth()/4);
            textArea.setPrefRowCount(newValue.split("\n").length+1);
        });
        stage.widthProperty().addListener((o,oldValue,newValue)->{
            double width = computeTextWidth(textNode, textArea.getText()) + 20; // Ajustement pour les marges
            textArea.setMaxWidth(Math.min(width, Double.parseDouble(newValue.toString())/2));
            textArea.setMinWidth(scene.getWidth()/4);
            textArea.setPrefRowCount(textArea.getText().split("\n").length+1);
        });
    }
}