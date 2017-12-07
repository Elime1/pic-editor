package io.github.elime1.piceditor.application;

import org.springframework.stereotype.Component;
import java.util.prefs.Preferences;

@Component
public class AppPreferences {

    private static final String ROOT_NODE = "elime.piceditor";
    private static final String USER_HOME_PROPRTY = "user.home";
    private static final String PIC_PATH_PROPRTY = "picPath";
    private static final String IMAGE_PATH_PROPRTY = "imagePath";

    private Preferences preferences;

    public AppPreferences() {
        preferences = Preferences.userRoot().node(ROOT_NODE);
    }

    public void setPicPath(String path) {
        preferences.put(PIC_PATH_PROPRTY, path);
    }

    public String getPicPath() {
        return preferences.get(PIC_PATH_PROPRTY, System.getProperty(USER_HOME_PROPRTY));
    }

    public void setImagePath(String path) {
        preferences.put(IMAGE_PATH_PROPRTY, path);
    }

    public String getImagePath() {
        return preferences.get(IMAGE_PATH_PROPRTY, System.getProperty(USER_HOME_PROPRTY));
    }
}
