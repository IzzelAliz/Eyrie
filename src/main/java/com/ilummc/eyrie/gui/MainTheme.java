package com.ilummc.eyrie.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MainTheme {
    private static Parent parent;
    private static Scene scene;
    private static Stage stage;
    private static double xOffset, yOffset;

    public static void setup(Parent parent, Scene scene, Stage stage) {
        MainTheme.parent = parent;
        stage.setScene(scene);
        parent.setOnMousePressed((MouseEvent event) -> {
            event.consume();
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        parent.setOnMouseDragged((MouseEvent event) -> {
            event.consume();
            stage.setX(event.getScreenX() - xOffset);

            if (event.getScreenY() - yOffset < 0) {
                stage.setY(0);
            } else {
                stage.setY(event.getScreenY() - yOffset);
            }
        });
    }
}
