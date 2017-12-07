package io.github.elime1.piceditor.controllers;

import io.github.elime1.piceditor.models.ImageDisplayInfo;
import io.github.elime1.piceditor.models.PicDisplayInfo;
import io.github.elime1.piceditor.service.exceptions.WrongImageDimensionsException;
import io.github.elime1.piceditor.ui.FileChooserDialog;
import io.github.elime1.piceditor.service.ImageService;
import io.github.elime1.piceditor.service.PicService;
import io.github.elime1.piceditor.service.VersionService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

import static java.util.Objects.nonNull;
import static javafx.scene.control.Alert.AlertType.WARNING;

@Component
@Log4j2
public class MainViewController implements Initializable {

    @FXML private AnchorPane mainAnchorPain;

    //Top
    @FXML private ProgressBar progressBar;
    @FXML private Label imageCountLabel;
    @FXML private Button previousImageButton;
    @FXML private Button nextImageButton;

    //Left
    @FXML private Label versionLabel;
    @FXML private Label signatureLabel;
    @FXML private Label numberOfImagesLabel;
    @FXML private Label imageDimensionsLabel;
    @FXML private Label imageBackgroundDisplayLabel;
    @FXML private Button replaceCurrentImageButton;
    @FXML private Button saveCurrentImageButton;
    @FXML private Button openPicButton;
    @FXML private Button savePicButton;

    //Center
    @FXML private ImageView imageView;

    private FileChooserDialog fileChooserDialog;
    private ImageService imageService;
    private PicService picService;

    private boolean dragFromImageView = false;

    @Autowired
    MainViewController(PicService picService, VersionService versionService, ImageService imageService) {
        this.picService = picService;
        this.imageService = imageService;
    }

    @Autowired
    public void setFileChooserDialog(FileChooserDialog fileChooserDialog) {
        this.fileChooserDialog = fileChooserDialog;
    }

    ////////////////////////////////////////////////////////////////////////
    //Initialize

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressBar.setProgress(0.0);
        imageCountLabel.setAlignment(Pos.CENTER);
        initButtonEvents();
        initDragEvents();
        initKeyListener();
    }

    private void initButtonEvents() {
        replaceCurrentImageButton.setOnAction(event -> replaceImage());
        saveCurrentImageButton.setOnAction(event -> saveImage());
        openPicButton.setOnAction(event -> openPic());
        savePicButton.setOnAction(event -> savePic());
        nextImageButton.setOnAction(event -> nextImage());
        previousImageButton.setOnAction(event -> previousImage());
    }

    private void initDragEvents() {

        imageView.setOnDragDetected(event -> {
            if (picService.isPicLoaded()) {
                dragFromImageView = true;
                String tmpDir = System.getProperty("user.home") + "/image.png";
                Dragboard db = imageView.startDragAndDrop(TransferMode.ANY);
                ClipboardContent clipboardContent = new ClipboardContent();
                File imageFile = new File(tmpDir);
                clipboardContent.putFiles(Collections.singletonList(imageFile));
                db.setContent(clipboardContent);
                imageService.saveImage(imageFile, imageView.getImage());
                event.consume();
            }
        });

        imageView.setOnDragOver(event -> {
            if (!dragFromImageView) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    File file = db.getFiles().get(0);
                    if (file.getName().toLowerCase().endsWith(".png")) {
                        event.acceptTransferModes(TransferMode.COPY);
                        event.consume();
                    }
                }
            }
        });

        imageView.setOnDragDropped(event -> {
            log.debug("Mouse drag dropped on image view");
            if (picService.isPicLoaded()) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    replaceCurrentImage(db.getFiles().get(0));
                }
            }
            event.setDropCompleted(true);
            event.consume();
        });

        imageView.setOnDragDone(event -> {
            log.debug("Mouse drag done");
            dragFromImageView = false;
        });

    }

    private void initKeyListener() {
        mainAnchorPain.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            if (keyCode.equals(KeyCode.RIGHT)) {nextImage();}
            else if (keyCode.equals(KeyCode.LEFT)) {previousImage();}
            else if (event.isControlDown()) {
                if (event.isAltDown()) {
                    if (keyCode.equals(KeyCode.O)) {replaceImage();}
                    else if (keyCode.equals(KeyCode.S)) {saveImage();}
                } else {
                    if (keyCode.equals(KeyCode.O)) {openPic();}
                    else if (keyCode.equals(KeyCode.S)) {savePic();}
                    else if (keyCode.equals(KeyCode.R)) {replaceImage();}
                }
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////
    //Public

    public void openPic(String fileName) {
        log.debug("Opening file: " + fileName);
        String extension = fileName.substring(fileName.length() - 4);
        extension = extension.toLowerCase();
        if (extension.equals(".pic")) {
            openPic(new File(fileName));
        } else {
            log.debug("The file is not a pic file: " + fileName);
            showAlert("Failed to open pic", "The file '" + fileName + "' does not seem to be a pic file.");
        }
    }

    ////////////////////////////////////////////////////////////////////////
    //Private

    private void openPic() {
        openPic(fileChooserDialog.showOpenPicDialog(getWindow()));
    }

    private void openPic(File file) {
        if (nonNull(file)) {
            if (file.exists()) {
                try {
                    loadPic(file);
                } catch (Exception e) {
                    log.error("Failed to open pic", e);
                    showAlert("Failed to open pic",
                            "The file '" + file.getPath() + "' could not be opened: " + e.toString());
                }
            } else {
                log.warn("The file '" + file.getPath() + " does not exist!");
            }
        }
    }

    private void replaceCurrentImage(File file) {
        try {
            picService.replacePicImageSprites(file);
        } catch (WrongImageDimensionsException e) {
            showAlert("Wrong dimensions!", e.getMessage());
            return;
        }
        int imageNumber = picService.getCurrentImageNumber();
        log.info("Replacing image " + imageNumber + " with: " + file.getPath());
        imageView.setImage(picService.currentImage());
    }

    private void nextImage() {
        if (picService.isPicLoaded()) {
            picService.nextImage();
            displayImage();
        }
    }

    private void previousImage() {
        if (picService.isPicLoaded()) {
            picService.previousImage();
            displayImage();
        }
    }

    private void savePic() {
        if (picService.isPicLoaded()) {
            File file = fileChooserDialog.showSavePicDialog(getWindow());
            if (nonNull(file)) {
                log.debug("Saving pic: " + file.getPath());
                picService.savePic(file);
            }
        }
    }

    private void replaceImage() {
        if (picService.isPicLoaded()) {
            File file = fileChooserDialog.showReplaceImageDialog(getWindow());
            if (nonNull(file)) {
                replaceCurrentImage(file);
            }
        }
    }

    private void saveImage() {
        Image image = imageView.getImage();
        if (nonNull(image)) {
            File file = fileChooserDialog.showSaveImageDialog(getWindow());
            if (nonNull(file)) {
                log.debug("Saving image: " + file.getPath());
                imageService.saveImage(file, image);
            }
        }
    }

    private void displayImage() {
        ImageDisplayInfo info = picService.getImageDisplayInfo();
        Tooltip tooltip = new Tooltip(info.getBgRgb() + " " + info.getBgHex());
        imageCountLabel.setText(info.getImageCount());
        imageDimensionsLabel.setText(info.getImageDimensions());
        imageBackgroundDisplayLabel.setStyle("-fx-background-color:" + info.getBgRgb() + ";");
        imageBackgroundDisplayLabel.setTooltip(tooltip);
        imageView.setImage(picService.currentImage());
    }

    private void showNavigationArrows(boolean visible) {
        previousImageButton.setVisible(visible);
        nextImageButton.setVisible(visible);
    }

    private void loadPic(File picFile) {
        picService.loadPic(picFile);
        updatePicInfo();
        showNavigationArrows(true);
        displayImage();
    }

    private void updatePicInfo() {
        PicDisplayInfo info = picService.getPicDisplayInfo();
        versionLabel.setText(info.getVersion());
        signatureLabel.setText(info.getSignature());
        numberOfImagesLabel.setText(info.getNumberOfImages());
    }

    private Window getWindow() {
        if (nonNull(mainAnchorPain.getScene())) {
            return mainAnchorPain.getScene().getWindow();
        }
        return null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(getWindow());
        alert.show();
    }

}
