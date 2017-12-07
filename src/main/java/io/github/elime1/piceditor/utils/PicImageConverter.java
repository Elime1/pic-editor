package io.github.elime1.piceditor.utils;

import io.github.elime1.piceditor.models.PicColor;
import io.github.elime1.piceditor.models.PicImage;
import io.github.elime1.piceditor.models.Sprite;
import io.github.elime1.piceditor.service.exceptions.WrongImageDimensionsException;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class PicImageConverter {

    public Image toImage(PicImage picImage) {
        Sprite[] sprites = picImage.getSprites();

        int width = picImage.getWidth();
        int height = picImage.getHeight();
        int size = Sprite.DEFAULT_W_H;

        WritableImage writableImage = new WritableImage(size * width, size * height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        PicColor bgColor = picImage.getBgColor();

        int spriteIndex = 0;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                byte[] pixelData = sprites[spriteIndex].getPixelData();
                int currentPixel = 0;
                int i = 0;
                while (i < pixelData.length) {
                    int backgroundPixels = ((pixelData[i++] & 0xff) | (pixelData[i++] & 0xff) << 8);
                    int coloredPixels = ((pixelData[i++] & 0xff) | (pixelData[i++] & 0xff) << 8);
                    for (int j = 0; j < backgroundPixels; j++) {
                        pixelWriter.setArgb(
                                size * w + currentPixel % size,
                                size * h + currentPixel / size,
                                rgbToArgbInt(bgColor.getR(), bgColor.getG(), bgColor.getB())
                        );
                        currentPixel++;
                    }
                    for (int j = 0; j < coloredPixels; j++) {
                        pixelWriter.setArgb(
                                size * w + currentPixel % size,
                                size * h + currentPixel / size,
                                rgbToArgbInt(pixelData[i++], pixelData[i++], pixelData[i++])
                        );
                        currentPixel++;
                    }
                }
                spriteIndex++;
            }
        }
        return writableImage;
    }

    public void replacePicImageSprites(PicImage picImage, Image image) throws WrongImageDimensionsException {

        int width = (int) image.getWidth() / Sprite.DEFAULT_W_H;
        int height = (int) image.getHeight() / Sprite.DEFAULT_W_H;

        if (width != picImage.getWidth() || height != picImage.getHeight()) {
            String msg = "The image must have the same dimensions as the image it is replacing. (" +
                    picImage.getPixelWidth() + "x" + picImage.getPixelHeight() + " pixels)";
            log.warn("Replace image failed - " + "The image must have the same dimensions as the image it is replacing.");
            throw new WrongImageDimensionsException(msg);
        }

        PicColor bgColor = picImage.getBgColor();
        Sprite[] sprites = new Sprite[width * height];

        PixelReader pixelReader = image.getPixelReader();

        int nSpritePixels = Sprite.DEFAULT_W_H * Sprite.DEFAULT_W_H;
        int spriteIndex = 0;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                WritableDataBuffer buffer = new WritableDataBuffer(3584);
                int currentPixel = 0;
                int backgroundPixels = 0;
                int coloredPixels = 0;

                while (currentPixel < nSpritePixels) {
                    int backgroundPixelsPos = buffer.position();
                    buffer.skip(Short.BYTES);
                    int coloredPixelsPos = buffer.position();
                    buffer.skip(Short.BYTES);

                    while (currentPixel < nSpritePixels) {
                        int argb = pixelReader.getArgb(
                                Sprite.DEFAULT_W_H * w + currentPixel % Sprite.DEFAULT_W_H,
                                Sprite.DEFAULT_W_H * h + currentPixel / Sprite.DEFAULT_W_H
                        );

                        if (((argb >> 24) & 0xFF) == 0) {
                            currentPixel++;
                            break;
                        }

                        if (bgColor.equals(argbIntToPicColor(argb))) {
                            backgroundPixels++;
                        } else {break;}
                        currentPixel++;
                    }

                    while (currentPixel < nSpritePixels) {
                        int argb = pixelReader.getArgb(
                                Sprite.DEFAULT_W_H * w + currentPixel % Sprite.DEFAULT_W_H,
                                Sprite.DEFAULT_W_H * h + currentPixel / Sprite.DEFAULT_W_H
                        );

                        if (((argb >> 24) & 0xFF) == 0) {
                            currentPixel++;
                            break;
                        }

                        PicColor color = argbIntToPicColor(argb);

                        if (!bgColor.equals(color)) {
                            buffer.putByte(color.getR());
                            buffer.putByte(color.getG());
                            buffer.putByte(color.getB());
                            coloredPixels++;
                        } else {break;}
                        currentPixel++;
                    }

                    buffer.putU16(backgroundPixelsPos, backgroundPixels);
                    buffer.putU16(coloredPixelsPos, coloredPixels);
                    coloredPixels = 0;
                    backgroundPixels = 0;
                }

                sprites[spriteIndex] = new Sprite(buffer.array());
                spriteIndex++;
            }
        }

        picImage.setSprites(sprites);
        picImage.setWidth(width);
        picImage.setHeight(height);
    }

    private int rgbToArgbInt(byte r, byte g, byte b) {
        return 0xFF << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }

    private PicColor argbIntToPicColor(int argb) {
        return new PicColor((byte) ((argb >> 16) & 0xFF), (byte) ((argb >> 8) & 0xFF), (byte) (argb & 0xFF));
    }
}
