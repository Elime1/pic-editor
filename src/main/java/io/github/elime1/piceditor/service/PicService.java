package io.github.elime1.piceditor.service;

import io.github.elime1.piceditor.models.ImageDisplayInfo;
import io.github.elime1.piceditor.models.Pic;
import io.github.elime1.piceditor.models.PicDisplayInfo;
import io.github.elime1.piceditor.models.PicImage;
import io.github.elime1.piceditor.service.exceptions.UnsupportedPicFormatException;
import io.github.elime1.piceditor.service.exceptions.WrongImageDimensionsException;
import io.github.elime1.piceditor.utils.ImageIndex;
import io.github.elime1.piceditor.utils.PicIO;
import io.github.elime1.piceditor.utils.PicImageConverter;
import javafx.scene.image.Image;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

import static java.util.Objects.nonNull;

@Log4j2
@Service
public class PicService {

    private Pic pic;
    private VersionService versionService;
    private ImageService imageService;
    private PicIO picIO;
    private PicImageConverter picImageConverter;

    private ImageIndex imageIndex;


    @Autowired
    public PicService(VersionService versionService,
                      ImageService imageService,
                      PicIO picIO,
                      PicImageConverter picImageConverter) {
        this.versionService = versionService;
        this.imageService = imageService;
        this.picIO = picIO;
        this.picImageConverter = picImageConverter;
    }

    public void loadPic(File picFile) {
        try {
            pic = readPic(picFile);
            imageIndex = new ImageIndex(pic.getNumberOfImages(), 0);
        } catch (UnsupportedPicFormatException e) {
            log.warn(e.getMessage());
            throw new RuntimeException("Unsupported pic", e);
        }
    }

    public void savePic(File picFile) {
        writePic(picFile, pic);
    }

    public Image currentImage() {
        return getImageFromPicImage(currentPicImage());
    }

    public boolean isPicLoaded() {
        return nonNull(pic);
    }

    public int getCurrentImageNumber() {
        return imageIndex.currentImageNumber();
    }

    public void nextImage() {
        imageIndex.nexImage();
    }

    public void previousImage() {
        imageIndex.previousImage();
    }

    private PicImage currentPicImage() {
        return pic.getPicImages()[imageIndex.currentIndex()];
    }

    public PicDisplayInfo getPicDisplayInfo() {
        if (pic.getNumberOfImages() > 0) {
            String signatureHex = Integer.toHexString(pic.getSignature());
            String version = versionService.getTibiaVersion(signatureHex);
            int numberOfImages = pic.getNumberOfImages();

            return PicDisplayInfo.builder()
                    .version("Version: " +  (version == null? "Unknown" : version))
                    .signature("Signature: " + signatureHex)
                    .numberOfImages("Images: " + numberOfImages)
                    .build();
        }
        return null;
    }

    public ImageDisplayInfo getImageDisplayInfo() {
        PicImage picImage = currentPicImage();
        return ImageDisplayInfo.builder()
                .imageCount(imageIndex.currentImageNumber() + "/" + pic.getNumberOfImages())
                .imageDimensions("Dimensions: " + picImage.getPixelWidth() + "x" + picImage.getPixelHeight())
                .bgHex(picImage.getBgColor().toHexString())
                .bgRgb(picImage.getBgColor().toRGBString())
                .build();
    }

    private Pic readPic(File picFile) throws UnsupportedPicFormatException {
        return picIO.readPic(picFile);
    }

    private void writePic(File picFile, Pic pic) {
        new PicIO().writePic(picFile, pic);
    }

    private Image getImageFromPicImage(PicImage picImage) {
        return new PicImageConverter().toImage(picImage);
    }

    public void replacePicImageSprites(File file) throws WrongImageDimensionsException {
        Image image = imageService.openImage(file);
        picImageConverter.replacePicImageSprites(currentPicImage(), image);
    }
}
