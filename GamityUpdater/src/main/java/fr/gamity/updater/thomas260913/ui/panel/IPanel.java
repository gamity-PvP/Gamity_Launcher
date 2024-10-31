package fr.gamity.updater.thomas260913.ui.panel;

import fr.gamity.updater.thomas260913.ui.PanelManager;
import javafx.scene.layout.GridPane;

public interface IPanel {
    void init(PanelManager panelManager);
    GridPane getLayout();
    void onShow();
    String getName();
    String getStylesheetPath();
}
