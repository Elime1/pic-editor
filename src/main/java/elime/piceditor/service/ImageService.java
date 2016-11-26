package elime.piceditor.service;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Elime on 15-08-28.
 */
public class ImageService {
    private static Logger log = LogManager.getLogger();

    public void saveImage(File file, Image image) {
        log.info("Saving image to file: " + file.getPath());
        try {
            String name = file.getName();
            String extension = name.substring(name.lastIndexOf('.') + 1, name.length()).toLowerCase();
            if (extension.equals("png")) { //With alpha
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), extension, file);
            } else { //Without alpha (jpg, bmp)
                ImageIO.write(convertToBufferedImageWithoutAlpha(image), extension, file);
            }
        } catch (IOException e) {
            log.warn("Failed to write image to file");
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            log.warn("Could not save image - null argument");
            throw new RuntimeException(e);
        }
    }

    public Image openImage(File file) {
        log.info("Opening image from file: " + file.getPath());

        FileInputStream fileInputStream = null;
        Image image = null;

        try {
            fileInputStream = new FileInputStream(file);
            image = new Image(fileInputStream);
        } catch (FileNotFoundException e) {
            log.warn("Could not open image file: " + file.getPath());
            throw new RuntimeException(e);
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
            } catch (IOException e) {
                log.warn("Failed to close FileInputStream", e);
            }
        }

        return image;
    }

    private BufferedImage convertToBufferedImageWithoutAlpha(Image image) {
        BufferedImage bufferedImage = new BufferedImage((int)image.getWidth(), (int)image.getHeight(), BufferedImage.TYPE_INT_RGB);
        PixelReader pixelReader = image.getPixelReader();
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                bufferedImage.setRGB(x, y, pixelReader.getArgb(x, y));
            }
        }
        return bufferedImage;
    }
}
