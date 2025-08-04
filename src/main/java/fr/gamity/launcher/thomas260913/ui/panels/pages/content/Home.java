package fr.gamity.launcher.thomas260913.ui.panels.pages.content;

import fr.gamity.launcher.thomas260913.Launcher;
import fr.gamity.launcher.thomas260913.ui.PanelManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class Home extends ContentPanel {

    private final List<VBox> newsBoxes = new ArrayList<>();

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/home.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        RowConstraints row = new RowConstraints();
        row.setVgrow(Priority.ALWAYS);
        this.layout.getRowConstraints().add(row);

        VBox centerWrapper = new VBox();
        centerWrapper.setAlignment(Pos.CENTER);
        centerWrapper.setPadding(new Insets(30));
        centerWrapper.setSpacing(20);
        centerWrapper.setPrefHeight(Double.MAX_VALUE);
        centerWrapper.setPrefWidth(Double.MAX_VALUE);

        HBox cardsRow = new HBox(30);
        cardsRow.setAlignment(Pos.CENTER);

// Cartes de chargement
        for (int i = 0; i < 3; i++) {
            VBox loadingCard = createNewsCard("Chargement...", "", null, null);
            newsBoxes.add(loadingCard);
            cardsRow.getChildren().add(loadingCard);
        }

        centerWrapper.getChildren().add(cardsRow);
        setCanTakeAllSize(centerWrapper);
        this.layout.add(centerWrapper, 0, 0); // Bien placer dans la grille
        this.layout.setStyle("-fx-background-color: transparent;");


        // Chargement des données depuis l’API Express (en tâche de fond)
        new Thread(() -> {
            try {
                URL url = new URL("https://gamity.thomas260914.dev/news");
                Scanner scanner = new Scanner(url.openStream(), "UTF-8");
                StringBuilder jsonBuilder = new StringBuilder();
                while (scanner.hasNext()) {
                    jsonBuilder.append(scanner.nextLine());
                }
                scanner.close();

                JSONArray json = new JSONArray(jsonBuilder.toString());

                Platform.runLater(() -> {
                    cardsRow.getChildren().clear();
                    for (int i = 0; i < Math.min(3, json.length()); i++) {
                        JSONObject obj = json.getJSONObject(i);
                        String title = obj.optString("title", "Titre inconnu");
                        String desc = obj.optString("description", "");
                        String image = obj.optString("image", null);
                        String link = obj.optString("url", null);

                        VBox card = createNewsCard(title, desc, image, link);
                        cardsRow.getChildren().add(card);
                    }
                });

            } catch (Exception e) {
                Launcher.getInstance().getLogger().printStackTrace(e);
                Platform.runLater(() -> {
                    cardsRow.getChildren().clear();
                    for (int i = 0; i < 3; i++) {
                        VBox errorCard = createNewsCard("Erreur de chargement", "", null, null);
                        cardsRow.getChildren().add(errorCard);
                    }
                });
            }
        }).start();
    }

    private VBox createNewsCard(String title, String description, String imageUrl, final String link) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefSize(250, 320);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.85);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0.3, 0, 4);"
        );
        card.setCursor(Cursor.HAND);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(220);

        card.getChildren().add(titleLabel);

        // Ajouter image entre le titre et la description
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image image = new Image(imageUrl, 200, 120, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(200);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setCache(true);
                imageView.setClip(createRoundedClip(200, 120, 10));
                card.getChildren().add(imageView);
            } catch (Exception e) {
                Launcher.getInstance().getLogger().printStackTrace(e);
            }
        }

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font(12));
        descLabel.setWrapText(true);
        descLabel.setAlignment(Pos.TOP_CENTER);
        descLabel.setMaxWidth(220);

        card.getChildren().add(descLabel);

        // Redirection au clic
        if (link != null && !link.isEmpty()) {
            card.setOnMouseClicked((MouseEvent event) -> openWebPage(link));
        }

        return card;
    }

    private Rectangle createRoundedClip(double width, double height, double arc) {
        Rectangle clip = new Rectangle(width, height);
        clip.setArcWidth(arc);
        clip.setArcHeight(arc);
        return clip;
    }

    private void openWebPage(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            Launcher.getInstance().showErrorDialog(e, this.panelManager.getStage());
            Launcher.getInstance().getLogger().printStackTrace(e);
        }
    }
}
