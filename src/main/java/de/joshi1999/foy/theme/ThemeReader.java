package de.joshi1999.foy.theme;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ThemeReader {
    Theme[] themes = new Theme[0];
    Theme defaultTheme = null;

    public ThemeReader() {
        Gson gson = new Gson();
        InputStream inputStream = getClass().getResourceAsStream("/themes/themes.json");
        if (inputStream == null) {
            throw new RuntimeException("themes.json not found in resources");
        }
        JsonReader jasonReader = new JsonReader(new InputStreamReader(inputStream));
        try {
            themes = gson.fromJson(jasonReader, Theme[].class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read themes.json");
        } finally {
            try {
                jasonReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Theme theme : themes) {
            if (theme.getName().equalsIgnoreCase("#default")) {
                defaultTheme = theme;
                break;
            }
        }
    }

    public Theme getTheme(String name) {
        for (Theme theme : themes) {
            if (theme.getName().equalsIgnoreCase(name)) {
                return theme;
            }
        }
        return defaultTheme;
    }
}