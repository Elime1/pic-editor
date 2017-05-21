package elime.piceditor.service;

import elime.piceditor.application.AppInfo;
import elime.piceditor.entities.Pic;
import elime.piceditor.entities.PicColor;
import elime.piceditor.entities.Sprite;
import elime.piceditor.entities.Thing;
import elime.piceditor.service.exceptions.UnsupportedPicFormatException;
import elime.piceditor.service.exceptions.WrongImageDimensionsException;
import elime.piceditor.service.util.ReadableDataBuffer;
import elime.piceditor.service.util.WritableDataBuffer;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Created by Elime on 15-08-28
 */
public class PicService {

    private static Logger log = LogManager.getLogger();

    /////////////////////////////////////////////////////
    //public

    public Pic readPic(File picFile) throws UnsupportedPicFormatException {
        return doReadPic(picFile);
    }

    public void writePic(File picFile, Pic pic) {
        doWritePic(picFile, pic);
    }

    public Image getImageFromThing(Thing thing) {
        return doGetImageFromThing(thing);
    }

    public void replaceThingSprites(Thing thing, Image image) throws WrongImageDimensionsException {
            doReplaceThingSprites(thing, image);
    }


    /////////////////////////////////////////////////////
    //private

    private Pic doReadPic(File picFile) throws UnsupportedPicFormatException {

        log.info("Opening pic from file: " + picFile.getPath());

        ReadableDataBuffer buffer = new ReadableDataBuffer(picFile);

        Pic pic = new Pic();

        pic.setNumberOfBytes(buffer.getNumberOfBytes());

        log.debug("Start reading pic...");

        pic.setSignature((int) buffer.getU32());

        if (pic.getSignature() == AppInfo.OLD_TIBIA_PIC_SIGNATURE) {
            log.debug("User tried to open an old pic file.");
            throw new UnsupportedPicFormatException("The Tibia.pic file is of old unsupported format. (Older than Tibia 7.0)");
        }

        pic.setNumberOfImages(buffer.getU16());

        log.debug("Signature: " + Integer.toHexString(pic.getSignature()));
        log.debug("Number of images: " + pic.getNumberOfImages());

        Thing[] things = new Thing[pic.getNumberOfImages()];

        for (int i = 0; i < things.length; i++) {
            log.debug("Extracting image " + (i + 1));
            Thing thing = new Thing(pic);

            //Number of sprites wide
            thing.setWidth(buffer.getU8());
            //Number of sprites high
            thing.setHeight(buffer.getU8());

            //Transparent color pixels
            PicColor bgColor = new PicColor(buffer.getByte(), buffer.getByte(), buffer.getByte());
            thing.setBgColor(bgColor);

            int numberOfSprites = thing.getWidth() * thing.getHeight();

            Sprite[] sprites = new Sprite[numberOfSprites];

            for (int j = 0; j < numberOfSprites; j++) {

                int spritePos = (int) buffer.getU32(); //Read the sprites position
                int storedPos = buffer.position(); //Store our current position
                buffer.position(spritePos); //Jump to where the sprite is stored

                //Save sprite data
                sprites[j] = new Sprite(buffer.getBytes(buffer.getU16()));

                buffer.position(storedPos); //Jump back to previous position
            }

            thing.setSprites(sprites);
            things[i] = thing;
        }

        pic.setThings(things);

        log.debug("Done reading pic");

        return pic;
    }

    private void doWritePic(File picFile, Pic pic) {

        WritableDataBuffer buffer = new WritableDataBuffer(pic.getNumberOfBytes());

        log.debug("Start compiling pic...");

        buffer.putU32(pic.getSignature());

        buffer.putU16(pic.getNumberOfImages());

        Thing[] things = pic.getThings();

        //Calculate where we can put the first sprite
        int spritePos = buffer.position();
        for (Thing thing : things) {
            spritePos += 5 + thing.getWidth() * thing.getHeight() * Integer.BYTES;
        }

        for (int i = 0; i < things.length; i++) {
            log.debug("Compiling image " + (i + 1));

            buffer.putU8(things[i].getWidth());
            buffer.putU8(things[i].getHeight());
            PicColor bgColor = things[i].getBgColor();
            buffer.putByte(bgColor.getR());
            buffer.putByte(bgColor.getG());
            buffer.putByte(bgColor.getB());

            Sprite[] sprites = things[i].getSprites();
            log.debug("The image consists of " + sprites.length + " sprites");
            for (Sprite sprite : sprites) {
                buffer.putU32(spritePos);

                int pos = buffer.position();
                buffer.position(spritePos);

                int spriteOffsetPos = buffer.position();
                buffer.skip(Short.BYTES);

                buffer.putBytes(sprite.getPixelData());

                buffer.putU16(spriteOffsetPos, buffer.position() - spritePos - Short.BYTES);
                spritePos = buffer.position();
                buffer.position(pos);
            }
        }

        log.info("Saving pic to file: " + picFile.getPath());
        buffer.writeFile(picFile);

        log.debug("Done compiling pic");
    }

    private Image doGetImageFromThing(Thing thing) {
        Sprite[] sprites = thing.getSprites();

        int width = thing.getWidth();
        int height = thing.getHeight();
        int size = Sprite.DEFAULT_W_H;

        WritableImage writableImage = new WritableImage(size * width, size * height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        PicColor bgColor = thing.getBgColor();

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
                                0xFF << 24 |
                                        (bgColor.getR() & 0xFF) << 16 |
                                        (bgColor.getG() & 0xFF) << 8 |
                                        bgColor.getB() & 0xFF
                        );
                        currentPixel++;
                    }
                    for (int j = 0; j < coloredPixels; j++) {
                        pixelWriter.setArgb(
                                size * w + currentPixel % size,
                                size * h + currentPixel / size,
                                0xFF << 24 |
                                        (pixelData[i++] & 0xFF) << 16 |
                                        (pixelData[i++] & 0xFF) << 8 |
                                        pixelData[i++] & 0xFF
                        );
                        currentPixel++;
                    }
                }
                spriteIndex++;
            }
        }
        return writableImage;
    }

    private void doReplaceThingSprites(Thing thing, Image image) throws WrongImageDimensionsException {

        int width = (int) image.getWidth() / Sprite.DEFAULT_W_H;
        int height = (int) image.getHeight() / Sprite.DEFAULT_W_H;

        if (width != thing.getWidth() || height != thing.getHeight()) {
            String msg = "The image must have the same dimensions as the image it is replacing.";
            log.warn("Replace image failed - " + "The image must have the same dimensions as the image it is replacing.");
            throw new WrongImageDimensionsException(msg);
        }

        PicColor bgColor = thing.getBgColor();
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

                        byte r = (byte) ((argb >> 16) & 0xFF);
                        byte g = (byte) ((argb >> 8) & 0xFF);
                        byte b = (byte) (argb & 0xFF);

                        if (r == bgColor.getR() && g == bgColor.getG() && b == bgColor.getB()) {
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

                        byte r = (byte) ((argb >> 16) & 0xFF);
                        byte g = (byte) ((argb >> 8) & 0xFF);
                        byte b = (byte) (argb & 0xFF);

                        if (!(r == bgColor.getR() && g == bgColor.getG() && b == bgColor.getB())) {
                            buffer.putByte(r);
                            buffer.putByte(g);
                            buffer.putByte(b);
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

        //Set the new thing values
        thing.setSprites(sprites);
        thing.setWidth(width);
        thing.setHeight(height);
    }
}
