package de.joshi1999.foy.window;

import java.awt.Color;

public class Theme {
    private final Color backgroundColor;
    private final String backgroundURL;

    public Theme(Color backgroundColor, String backgroundURL) {
        this.backgroundColor = backgroundColor;
        this.backgroundURL = backgroundURL;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public String getBackgroundURL() {
        return backgroundURL;
    }
}
