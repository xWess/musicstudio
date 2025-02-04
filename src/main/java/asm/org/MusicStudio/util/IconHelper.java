package asm.org.MusicStudio.util;

import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class IconHelper {
    public static FontIcon createIcon(FontAwesomeSolid icon, String color, int size) {
        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.setIconSize(size);
        fontIcon.setIconColor(javafx.scene.paint.Color.valueOf(color));
        return fontIcon;
    }
} 