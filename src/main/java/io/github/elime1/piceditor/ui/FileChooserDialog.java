package io.github.elime1.piceditor.ui;

import io.github.elime1.piceditor.application.AppPreferences;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

import static java.util.Objects.nonNull;

@Log4j2
@Service
public class FileChooserDialog {

    private AppPreferences appPreferences;

    @Autowired
    public FileChooserDialog(AppPreferences appPreferences) {
        this.appPreferences = appPreferences;
    }

    public File showReplaceImageDialog(Window parent) {
        log.debug("Opening replace with image dialog");
        File imageFile = showOpenDialog("Replace with image", FileType.IMAGE, parent);
        if(nonNull(imageFile) && imageFile.exists()) {
            this.appPreferences.setImagePath(imageFile.getParent());
            return imageFile;
        }
        log.debug("Replace with image dialog canceled");
        return null;
    }

    public File showSaveImageDialog(Window parent) {
        log.debug("Opening save image dialog");
        File imageFile = showSaveDialog("Save image", FileType.IMAGE, parent);
        if(nonNull(imageFile)) {
            this.appPreferences.setImagePath(imageFile.getParent());
            return imageFile;
        }
        log.debug("Save image dialog canceled");
        return null;
    }

    public File showOpenPicDialog(Window parent) {
        log.debug("Opening open pic file dialog");
        File picFile = showOpenDialog("Open pic file", FileType.PIC, parent);
        if(nonNull(picFile) && picFile.exists()) {
            this.appPreferences.setPicPath(picFile.getParent());
            return picFile;
        }
        log.debug("Open pic file dialog canceled");
        return null;
    }

    public File showSavePicDialog(Window parent) {
        log.debug("Opening save pic dialog");
        File picFile = showSaveDialog("Save pic", FileType.PIC, parent);
        if(nonNull(picFile)) {
            this.appPreferences.setPicPath(picFile.getParent());
            return picFile;
        }
        log.debug("Save pic dialog canceled");
        return null;
    }

    private File showOpenDialog(String header, FileType extType, Window parent) {
        return createFileChooser(header, extType).showOpenDialog(parent);
    }

    private File showSaveDialog(String header, FileType extType, Window parent) {
        return createFileChooser(header, extType).showSaveDialog(parent);
    }

    private FileChooser createFileChooser(String title, FileType fileType) {
        FileChooser fileChooser = new FileChooser();
        String path = null;
        switch (fileType) {
            case IMAGE:
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image", "*.png"));
                fileChooser.setInitialFileName("image.png");
                path = appPreferences.getImagePath();
                break;
            case PIC:
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Pic File", "*.pic"));
                fileChooser.setInitialFileName("Tibia.pic");
                path = appPreferences.getPicPath();
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
}
