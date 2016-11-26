package elime.piceditor.controllers;

import elime.piceditor.entities.Pic;
import elime.piceditor.entities.Thing;
import elime.piceditor.service.*;
import elime.piceditor.service.exceptions.UnsupportedPicFormatException;
import elime.piceditor.service.exceptions.WrongImageDimensionsException;
import elime.piceditor.service.util.Services;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Elime on 15-08-04.
 */
public class MainViewController {

    private static Logger log = LogManager.getLogger();

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
    //@FXML private ScrollPane scrollPane;

    //Services
    private FileChooserService fileChooserService;
    private ImageService imageService;
    private PicService picService;
    private VersionService versionService;

    private Window window;

    private Pic pic;
    private int thingIndex;

    private boolean dragFromImageView = false;

    ////////////////////////////////////////////////////////////////////////
    //Initialize

    public void init(Stage window, Services services) {
        this.window = window;
        progressBar.setProgress(0.0);
        imageCountLabel.setAlignment(Pos.CENTER);
        initButtonEvents();
        initDragEvents();
        initServices(services);
        initKeyListener(window.getScene());
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
            dragFromImageView = true;
            Dragboard db = imageView.startDragAndDrop(TransferMode.ANY);
            ClipboardContent clipboardContent = new ClipboardContent();
            ArrayList<File> files = new ArrayList<>(1);
            File imageFile = new File(System.getProperty("user.home") + "/PicEditor/image.png");
            files.add(imageFile);
            clipboardContent.putFiles(files);
            db.setContent(clipboardContent);
            imageService.saveImage(imageFile, imageView.getImage());
            event.consume();
        });

        imageView.setOnDragOver(event -> {
            if (!dragFromImageView) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    File file = db.getFiles().get(0);
                    if (file.getName().toLowerCase().endsWith(".png"))
                    {
                        event.acceptTransferModes(TransferMode.COPY);
                        event.consume();
                    }
                }
            }
        });

        imageView.setOnDragDropped(event -> {
            log.debug("Mouse drag dropped on image view");
            if (pic != null) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    replaceCurrentImage(db.getFiles().get(0));
                    event.setDropCompleted(true);
                    event.consume();
                }
            }
        });

        imageView.setOnDragDone(event -> {
            log.debug("Mouse drag done");
            dragFromImageView = false;
        });

    }

    private void initServices(Services services) {
        this.fileChooserService = services.getFileChooserService();
        this.imageService = services.getImageService();
        this.picService = services.getPicService();
        this.versionService = services.getVersionService();
    }

    private void initKeyListener(Scene scene) {
        scene.setOnKeyPressed(event -> {
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
        openPic(fileChooserService.showOpenPicDialog(window));
    }

    private void openPic(File picFile) {
        if (picFile != null) {
            if (picFile.exists()) {
                try {
                    loadPic(picFile);
                } catch (Exception e) {
                    log.error("Failed to open pic", e);
                    showAlert("Failed to open pic", "The file '" + picFile.getPath() + "' could not be opened: " + e.toString());
                }
            } else {
                log.warn("The file '" + picFile.getPath() + " does not exist!");
            }
        }
    }

    private void replaceCurrentImage(File file) {
        Thing thing = pic.getThings()[thingIndex];
        Image image = imageService.openImage(file);
        try {
            picService.replaceThingSprites(thing, image);
        } catch (WrongImageDimensionsException e) {
            showAlert(
                "Wrong dimensions!",
                "The image must have the same dimensions as the image it is replacing. (" +
                thing.getPixelWidth() + "x" + thing.getPixelHeight() + " pixels)"
            );
            return;
        }
        log.info("Replacing image " + (thingIndex + 1) + " with: " + file.getPath());
        imageView.setImage(picService.getImageFromThing(thing));
    }

    private void nextImage() {
        if (imageView.getImage() == null) return;
        if (thingIndex == pic.getNumberOfImages() - 1) {
            thingIndex = 0;
        } else {
            thingIndex++;
        }
        displayThing(pic.getThings()[thingIndex]);
    }

    private void previousImage() {
        if (imageView.getImage() == null) return;
        if (thingIndex == 0) {
            thingIndex = pic.getNumberOfImages() - 1;
        } else {
            thingIndex--;
        }
        displayThing(pic.getThings()[thingIndex]);
    }

    private void savePic() {
        if (pic != null) {
            File file = fileChooserService.showSavePicDialog(window);
            if (file != null) {
                log.debug("Saving pic: " + file.getPath());
                picService.writePic(file, pic);
            }
        }
    }

    private void replaceImage() {
        if (pic != null) {
            File file = fileChooserService.showReplaceImageDialog(window);
            if (file != null) {
                replaceCurrentImage(file);
            }
        }
    }

    private void saveImage() {
        Image image = imageView.getImage();
        if (image != null) {
            File file = fileChooserService.showSaveImageDialog(window);
            if (file != null) {
                log.debug("Saving image: " + file.getPath());
                imageService.saveImage(file, image);
            }
        }
    }

    private void displayThing(Thing thing) {
        imageView.setImage(picService.getImageFromThing(thing));
        imageCountLabel.setText((thingIndex + 1) + "/" + pic.getNumberOfImages());
        imageDimensionsLabel.setText("Dimensions: " + thing.getPixelWidth() + "x" + thing.getPixelHeight());
        imageBackgroundDisplayLabel.setStyle("-fx-background-color:" + thing.getBgColor().toString() + ";");
        Tooltip tooltip = new Tooltip(thing.getBgColor().toRGBString() + " " + thing.getBgColor().toHexString());
        imageBackgroundDisplayLabel.setTooltip(tooltip);
    }

    private void showNavigationArrows(boolean visible) {
        previousImageButton.setVisible(visible);
        nextImageButton.setVisible(visible);
    }

    private void loadPic(File picFile) {
        try {
            pic = picService.readPic(picFile);
        } catch (UnsupportedPicFormatException e) {
            log.warn(e.getMessage());
            showAlert("Unsupported pic", e.getMessage());
            return;
        }

        if (pic.getNumberOfImages() > 0) {
            String signatureHex = Integer.toHexString(pic.getSignature());
            String version = versionService.getTibiaVersion(signatureHex);

            versionLabel.setText("Version: " +  (version == null? "Unknown" : version));
            signatureLabel.setText("Signature: " + signatureHex);
            numberOfImagesLabel.setText("Images: " + pic.getNumberOfImages());
            thingIndex = 0;
            displayThing(pic.getThings()[0]);
            showNavigationArrows(true);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(window);
        alert.show();
    }

}


