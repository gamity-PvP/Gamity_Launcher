package fr.gamity.launcher.thomas260913.ui.panel;

import javafx.scene.Node;

public interface IMovable {
    void setLeft(Node node);
    void setRight(Node node);
    void setTop (Node node);
    void setBottom(Node node);
    void setBaseLine(Node node);
    void setCenterH(Node node);
    void setCenterV(Node node);
}
