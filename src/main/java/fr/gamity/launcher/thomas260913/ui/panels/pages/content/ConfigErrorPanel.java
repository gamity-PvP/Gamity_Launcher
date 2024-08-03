package fr.gamity.launcher.thomas260913.ui.panels.pages.content;

import fr.gamity.launcher.thomas260913.Launcher;
import fr.gamity.launcher.thomas260913.ui.PanelManager;
import fr.gamity.launcher.thomas260913.ui.panels.pages.App;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class ConfigErrorPanel extends ContentPanel {
    private final Path path;
    private final String name;
    private final Exception error;
    private final String fileContent;
    private final Path additionalString;

    public ConfigErrorPanel(Path path,String fileContent,Exception e) {
        this.path = path;
        this.name = path.getFileName().toString();
        this.error = e;
        this.fileContent = fileContent;
        this.additionalString = Launcher.getInstance().getLauncherDir().resolve("configExemple.json");
    }

    GridPane contentPane = new GridPane();

    @Override
    public String getName() {
        return "ConfigErrorPanel";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/ConfigErrorPanel.css";
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

        // Title
        Label title = new Label("Corrupted Config \"" + name + "\"");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
        title.getStyleClass().add("settings-title");
        setCanTakeAllSize(title);
        setLeft(title);
        title.setTextAlignment(TextAlignment.LEFT);

        // Label for exception TextArea
        Label exceptionLabel = new Label("Erreur lors du chargement:");
        exceptionLabel.getStyleClass().add("label");
        exceptionLabel.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 14f));
        setLeft(exceptionLabel);
        setCanTakeAllSize(exceptionLabel);

        // TextArea for exception
        TextArea exceptionArea = new TextArea();
        exceptionArea.setText(getStackTrace(error));
        exceptionArea.setEditable(false);
        exceptionArea.setWrapText(true);
        exceptionArea.getStyleClass().add("text-area-readonly");
        setCanTakeAllSize(exceptionArea);

        // Label for additional string TextArea
        Label additionalLabel = new Label("Exemple de config:");
        additionalLabel.getStyleClass().add("label");
        additionalLabel.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 14f));
        setLeft(additionalLabel);
        setCanTakeAllSize(additionalLabel);

        // TextArea for additional string
        TextArea additionalArea = new TextArea();
        additionalArea.getStyleClass().add("text-area");
        additionalArea.setText(readFileToString(additionalString.toAbsolutePath().toString()));
        additionalArea.setEditable(false);
        additionalArea.setWrapText(true);
        setCanTakeAllSize(additionalArea);

        // Label for file content TextArea
        Label fileContentLabel = new Label("Contenue de la config:");
        fileContentLabel.getStyleClass().add("label");
        fileContentLabel.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 14f));
        setLeft(fileContentLabel);
        setCanTakeAllSize(fileContentLabel);

        // TextArea for file content
        TextArea fileContentArea = new TextArea();
        fileContentArea.setText(fileContent);
        fileContentArea.setWrapText(true);
        fileContentArea.getStyleClass().add("text-area");
        setCanTakeAllSize(fileContentArea);

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
            try {
                Files.write(path, fileContentArea.getText().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                saveLabel.setText("Paramètre(s) enregistré(s)");
                saveLabel.setTextFill(Color.GREEN);
            } catch (IOException ioException) {
                saveLabel.setText("Erreur lors de la sauvegarde");
                saveLabel.setTextFill(Color.RED);
                ioException.printStackTrace();
            }
        });
        Button deleteBtn = new Button("Supprimer");
        deleteBtn.getStyleClass().add("deleteBtn");
        FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
        deleteIcon.setSize(App.iconSize);
        deleteIcon.getStyleClass().add("delete-icon");
        deleteBtn.setGraphic(deleteIcon);
        setCanTakeAllSize(deleteBtn);
        setBottom(deleteBtn);
        setCenterH(deleteBtn);
        deleteBtn.setOnMouseClicked(e -> {
            try {
                Files.delete(path);
                saveLabel.setText("Fichier supprimé");
                saveLabel.setTextFill(Color.GREEN);
            } catch (IOException ioException) {
                saveLabel.setText("Erreur lors de la suppression");
                saveLabel.setTextFill(Color.RED);
                ioException.printStackTrace();
            }
        });

        // Add components to contentPane
        contentPane.add(title, 0, 0);
        contentPane.add(exceptionLabel, 0, 1);
        contentPane.add(exceptionArea, 0, 2);
        contentPane.add(additionalLabel, 0, 3);
        contentPane.add(additionalArea, 0, 4);
        contentPane.add(fileContentLabel, 0, 5);
        contentPane.add(fileContentArea, 0, 6);
        contentPane.add(saveLabel, 0, 7);
        contentPane.add(saveBtn, 0, 8);
        contentPane.add(deleteBtn, 0, 9);

        panelManager.getStage().setMinHeight(680.0);
    }

    private String getStackTrace(Exception e) {
        StringBuilder result = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            result.append(element.toString()).append("\n");
        }
        return result.toString();
    }

    private static String readFileToString(String path) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            Launcher.getInstance().showErrorDialog(e);
            return null;
        }
    }
}