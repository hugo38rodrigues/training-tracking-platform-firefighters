package ui.components;

import javafx.scene.shape.SVGPath;

public  class IconSvg {
    public static SVGPath createIcon(String content, String styleClass) {
        SVGPath icon = new SVGPath();
        icon.setContent(content);
        icon.getStyleClass().add(styleClass);
        return icon;
    }
}
