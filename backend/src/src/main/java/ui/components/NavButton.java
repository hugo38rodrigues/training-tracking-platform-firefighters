package ui.components;

import javafx.scene.control.Button;

public class NavButton {

    public static Button create(String text, boolean active) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-item");
        if (active) {
            button.getStyleClass().add("nav-item-active");
        }
        button.setMaxWidth(Double.MAX_VALUE); // le bouton prend toute la largeur du menu
        return button;
    }
}
