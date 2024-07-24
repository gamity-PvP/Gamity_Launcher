package fr.gamity.launcher.thomas260913.ui.panels.pages.content;

import fr.gamity.launcher.thomas260913.JsonConfigParser;
import fr.gamity.launcher.thomas260913.Launcher;
import fr.gamity.launcher.thomas260913.ui.PanelManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class jsonClient extends ContentPanel {
    private vanilla createVanillaPanel(){
        Config config = new Config();
        config.name = "vanilla";
        config.mcinfo = new Config.McInfo();
        config.mcinfo.type = "vanilla";
        config.mcinfo.autoconnect = false;
        return new vanilla(config);
    }
    GridPane sidemenu = new GridPane();
    GridPane navContent = new GridPane();

    Node activeLink = null;
    static ContentPanel currentPage = null;

    Button vanillaBtn;

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
        vanillaBtn = new Button("vanilla");
        vanillaBtn.getStyleClass().add("sidemenu-nav-btn");
        MaterialDesignIconView icon1 = new MaterialDesignIconView(MaterialDesignIcon.SERVER);
        icon1.setSize("16px");
        vanillaBtn.setGraphic(icon1);
        setCanTakeAllSize(vanillaBtn);
        setTop(vanillaBtn);
        vanillaBtn.setTranslateY(90d);
        vanillaBtn.setOnMouseClicked(e -> setPage(createVanillaPanel(), vanillaBtn));

        sidemenu.getChildren().addAll(vanillaBtn);

        List<Path> jsonFile = readJsonFilesFromFolder(Launcher.getInstance().getConfigDir());

        AtomicInteger index = new AtomicInteger(0);
        int buttonHeight = 40;
        int spacing = 10;
        jsonFile.forEach(path ->{
            int i = index.getAndIncrement();
            Config config = new JsonConfigParser().parseJsonPath(path);
            Button panelBtn = new Button(config.name);
            panelBtn.getStyleClass().add("sidemenu-nav-btn");
            MaterialDesignIconView icon2 = new MaterialDesignIconView(MaterialDesignIcon.SERVER);
            icon2.setSize("16px");
            panelBtn.setGraphic(icon2);
            setCanTakeAllSize(panelBtn);
            setTop(panelBtn);
            double translateY1 = i * (buttonHeight + spacing);
            double translateY2 = translateY1 + 140d;
            panelBtn.setTranslateY(translateY2);
            panelBtn.setOnMouseClicked(e -> setPage(new createClientPanel(config,true), panelBtn));
            sidemenu.getChildren().add(panelBtn);
        });

        int i = index.getAndIncrement();
        Button ConfigBtn = new Button("Créer une config");
        ConfigBtn.getStyleClass().add("sidemenu-nav-btn");
        FontAwesomeIconView icon3 = new FontAwesomeIconView(FontAwesomeIcon.FILE_CODE_ALT);
        icon3.setSize("16px");
        ConfigBtn.setGraphic(icon3);
        setCanTakeAllSize(ConfigBtn);
        setTop(ConfigBtn);
        double translateY1 = i * (buttonHeight + spacing);
        double translateY2 = translateY1 + 140d;
        ConfigBtn.setTranslateY(translateY2);
        ConfigBtn.setOnMouseClicked(e -> setPage(new createConfig(), ConfigBtn));
        sidemenu.getChildren().add(ConfigBtn);

    }

    @Override
    public void onShow() {
        super.onShow();
        setPage(createVanillaPanel(), vanillaBtn);
    }

    public void setPage(ContentPanel panel, Node navButton) {

        if (currentPage instanceof createClientPanel && ((createClientPanel) currentPage).isDownloading()) {
            return;
        }
        if (currentPage instanceof vanilla && ((vanilla) currentPage).isDownloading()) {
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
        if (currentPage instanceof createClientPanel && ((createClientPanel) currentPage).isDownloading()) {
            return true;
        }
        return currentPage instanceof vanilla && ((vanilla) currentPage).isDownloading();
    }
}