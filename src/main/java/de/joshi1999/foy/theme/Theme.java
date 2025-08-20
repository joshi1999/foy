package de.joshi1999.foy.theme;

import java.awt.Color;

public class Theme {
    private String name;
    private String backgroundColor;
    private String backgroundImage;
    private int percentage;

    public Color getBackgroundColor() {
        return Color.decode(backgroundColor);
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public String getName() {
        return name;
    }

    public int getPercentage() {
        return percentage;
    }
}
