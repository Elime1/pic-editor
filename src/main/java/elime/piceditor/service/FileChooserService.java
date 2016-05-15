package elime.piceditor.service;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Created by Elime on 15-08-28.
 */

public class FileChooserService {

    private static Logger log = LogManager.getLogger();
    private PreferencesService preferencesService;

    private enum ExtensionType {
        IMAGE,
        PIC
    }

    public FileChooserService(PreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

    public File showReplaceImageDialog(Window parent) {
        log.debug("Opening replace with image dialog");
        File imageFile = showOpenDialog("Replace with image", ExtensionType.IMAGE, parent);
        if(imageFile != null && imageFile.exists()) {
            this.preferencesService.setImagePath(imageFile.getParent());
            return imageFile;
        }
        log.debug("Replace with image dialog canceled");
        return null;
    }

    public File showSaveImageDialog(Window parent) {
        log.debug("Opening save image dialog");
        File imageFile = showSaveDialog("Save image", ExtensionType.IMAGE, parent);
        if(imageFile != null) {
            this.preferencesService.setImagePath(imageFile.getParent());
            return imageFile;
        }
        log.debug("Save image dialog canceled");
        return null;
    }

    public File showOpenPicDialog(Window parent) {
        log.debug("Opening open pic file dialog");
        File picFile = showOpenDialog("Open pic file", ExtensionType.PIC, parent);
        if(picFile != null && picFile.exists()) {
            this.preferencesService.setPicPath(picFile.getParent());
            return picFile;
        }
        log.debug("Open pic file dialog canceled");
        return null;
    }

    public File showSavePicDialog(Window parent) {
        log.debug("Opening save pic dialog");
        File picFile = showSaveDialog("Save pic", ExtensionType.PIC, parent);
        if(picFile != null) {
            this.preferencesService.setPicPath(picFile.getParent());
            return picFile;
        }
        log.debug("Save pic dialog canceled");
        return null;
    }

    private File showOpenDialog(String header, ExtensionType extType, Window parent) {
        return createFileChooser(header, extType).showOpenDialog(parent);
    }

    private File showSaveDialog(String header, ExtensionType extType, Window parent) {
        return createFileChooser(header, extType).showSaveDialog(parent);
    }

    private FileChooser createFileChooser(String title, ExtensionType type) {
        FileChooser fileChooser = new FileChooser();
        String path = null;
        switch (type) {
            case IMAGE:
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image", "*.png"));
                fileChooser.setInitialFileName("image.png");
                path = preferencesService.getImagePath();
                break;
            case PIC:
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Pic File", "*.pic"));
                fileChooser.setInitialFileName("Tibia.pic");
                path = preferencesService.getPicPath();
        }
        log.debug("Use saved path as initial directory: " + path);
        File initialDirectory = new File(path);
        if (!initialDirectory.exists()) {
            String userHomeDir = System.getProperty("user.home");
            initialDirectory = new File(userHomeDir);
            log.debug("Path is invalid - Using user home directory instead: " + userHomeDir);
        }
        fileChooser.setInitialDirectory(initialDirectory);
        fileChooser.setTitle(title);
        return fileChooser;
    }

    private void setInitialState(FileChooser fileChooser, ExtensionType type) {

    }
}
