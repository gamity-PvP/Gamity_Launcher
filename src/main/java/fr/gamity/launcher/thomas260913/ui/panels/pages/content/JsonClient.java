package fr.gamity.launcher.thomas260913.ui.panels.pages.content;

import fr.gamity.launcher.thomas260913.Launcher;
import fr.gamity.launcher.thomas260913.game.Config;
import fr.gamity.launcher.thomas260913.game.Parser;
import fr.gamity.launcher.thomas260913.ui.PanelManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonClient extends ContentPanel {
    private Vanilla createVanillaPanel(){
        Config.CustomServer config = new Config.CustomServer();
        config.name = "vanilla";
        config.mcinfo = new Config.CustomServer.McInfo();
        config.mcinfo.autoconnect = false;
        return new Vanilla(config);
    }
    GridPane sidemenu = new GridPane();
    GridPane navContent = new GridPane();

    Node activeLink = null;
    static ContentPanel currentPage = null;

    public Button vanillaBtn,ConfigBtn;

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


        // Background
        this.layout.getStyleClass().add("app-layout");
        setCanTakeAllSize(this.layout);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setMinWidth(200);
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


        // Navigation

        VBox inputVBox = new VBox();

        vanillaBtn = new Button("vanilla");
        vanillaBtn.getStyleClass().add("sidemenu-nav-btn");
        MaterialDesignIconView icon1 = new MaterialDesignIconView(MaterialDesignIcon.SERVER);
        icon1.setSize("16px");
        vanillaBtn.setGraphic(icon1);
        setCanTakeAllSize(vanillaBtn);
        setTop(vanillaBtn);
        vanillaBtn.setOnMouseClicked(e -> setPage(createVanillaPanel(), vanillaBtn));

        inputVBox.getChildren().addAll(vanillaBtn);

        List<Path> jsonFile = readJsonFilesFromFolder(Launcher.getInstance().getConfigDir());

        jsonFile.forEach(path ->{
            try{
                Config.CustomServer config = new Parser.JsonConfigParser().parseJsonPath(path);
                Button panelBtn = new Button(config.name);
                panelBtn.getStyleClass().add("sidemenu-nav-btn");
                MaterialDesignIconView icon2 = new MaterialDesignIconView(MaterialDesignIcon.SERVER);
                icon2.setSize("16px");
                panelBtn.setGraphic(icon2);
                setCanTakeAllSize(panelBtn);
                setTop(panelBtn);
                panelBtn.setOnMouseClicked(e -> setPage(new CreateClientPanel(config, true), panelBtn));
                inputVBox.getChildren().add(panelBtn);
            }catch(Exception ex) {
                Button panelBtn = new Button("corrupted config");
                panelBtn.getStyleClass().add("sidemenu-nav-btn");
                MaterialDesignIconView icon2 = new MaterialDesignIconView(MaterialDesignIcon.SERVER);
                icon2.setSize("16px");
                panelBtn.setGraphic(icon2);
                setCanTakeAllSize(panelBtn);
                setTop(panelBtn);
                panelBtn.setOnMouseClicked(e -> setPage(new ConfigErrorPanel(path,readFileToString(path.toAbsolutePath().toString()),ex), panelBtn));
                inputVBox.getChildren().add(panelBtn);
                Launcher.getInstance().getLogger().printStackTrace(ex);
                Launcher.getInstance().showErrorDialog(ex,this.panelManager.getStage());
            }
        });

        ConfigBtn = new Button("Créer une config");
        ConfigBtn.getStyleClass().add("sidemenu-nav-btn");
        FontAwesomeIconView icon3 = new FontAwesomeIconView(FontAwesomeIcon.FILE_CODE_ALT);
        icon3.setSize("16px");
        ConfigBtn.setGraphic(icon3);
        setCanTakeAllSize(ConfigBtn);
        setTop(ConfigBtn);
        ConfigBtn.setOnMouseClicked(e -> setPage(new CreateConfig(), ConfigBtn));
        inputVBox.getChildren().add(ConfigBtn);

        ScrollPane scrollPane = new ScrollPane(inputVBox);
        scrollPane.setFitToWidth(true);
        setCanTakeAllSize(scrollPane);
        scrollPane.getStyleClass().add("scrollPane");

        sidemenu.getChildren().addAll(scrollPane);
    }

    @Override
    public void onShow() {
        super.onShow();
        setPage(createVanillaPanel(), vanillaBtn);
    }

    public void setPage(ContentPanel panel, Node navButton) {

        if (currentPage instanceof CreateClientPanel && ((CreateClientPanel) currentPage).isDownloading()) {
            return;
        }
        if (currentPage instanceof Vanilla && ((Vanilla) currentPage).isDownloading()) {
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
    public static List<Path> readJsonFilesFromFolder(Path folderPath) {
        List<Path> paths = new ArrayList<>();
        File folder = new File(folderPath.toUri());

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                paths.add(file.toPath());
            }
        }
        return paths;
    }
    public static boolean getLaunching(){
        if (currentPage instanceof CreateClientPanel && ((CreateClientPanel) currentPage).isDownloading()) {
            return true;
        }
        return currentPage instanceof Vanilla && ((Vanilla) currentPage).isDownloading();
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