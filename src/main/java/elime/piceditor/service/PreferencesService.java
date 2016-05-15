package elime.piceditor.service;

import java.util.prefs.Preferences;

/**
 * Created by Elime on 15-09-16.
 */
public class PreferencesService {

    private Preferences preferences;

    public PreferencesService() {
        preferences = Preferences.userRoot().node("elime.piceditor");
    }

    public void setPicPath(String path) {
        preferences.put("picPath", path);
    }

    public String getPicPath() {
        return preferences.get("picPath", System.getProperty("user.home"));
    }

    public void setImagePath(String path) {
        preferences.put("imagePath", path);
    }

    public String getImagePath() {
        return preferences.get("imagePath", System.getProperty("user.home"));
    }
}
