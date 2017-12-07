package io.github.elime1.piceditor.utils;

import io.github.elime1.piceditor.models.Pic;
import io.github.elime1.piceditor.models.PicColor;
import io.github.elime1.piceditor.models.PicImage;
import io.github.elime1.piceditor.models.Sprite;
import io.github.elime1.piceditor.service.exceptions.UnsupportedPicFormatException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.File;

@Log4j2
@Component
public class PicIO {

    private static final int OLD_TIBIA_SIGNATURE = 0x1fd0302; //Before 7.0

    public Pic readPic(File picFile) throws UnsupportedPicFormatException {

        log.info("Opening pic from file: " + picFile.getPath());

        ReadableDataBuffer buffer = new ReadableDataBuffer(picFile);

        Pic pic = new Pic();

        pic.setNumberOfBytes(buffer.getNumberOfBytes());

        log.debug("Start reading pic...");

        pic.setSignature((int) buffer.getU32());

        if (pic.getSignature() == OLD_TIBIA_SIGNATURE) {
            log.debug("User tried to open an old pic file.");
            throw new UnsupportedPicFormatException("The Tibia.pic file is of old unsupported format. (Older than Tibia 7.0)");
        }

        pic.setNumberOfImages(buffer.getU16());

        log.debug("Signature: " + Integer.toHexString(pic.getSignature()));
        log.debug("Number of images: " + pic.getNumberOfImages());

        PicImage[] picImages = new PicImage[pic.getNumberOfImages()];

        for (int i = 0; i < picImages.length; i++) {
            log.debug("Extracting image " + (i + 1));
            PicImage picImage = new PicImage(pic);

            //Number of sprites wide
            picImage.setWidth(buffer.getU8());
            //Number of sprites high
            picImage.setHeight(buffer.getU8());

            //Transparent color pixels
            PicColor bgColor = new PicColor(buffer.getByte(), buffer.getByte(), buffer.getByte());
            picImage.setBgColor(bgColor);

            int numberOfSprites = picImage.getWidth() * picImage.getHeight();

            Sprite[] sprites = new Sprite[numberOfSprites];

            for (int j = 0; j < numberOfSprites; j++) {

                int spritePos = (int) buffer.getU32(); //Read the sprites position
                int storedPos = buffer.position(); //Store our current position
                buffer.position(spritePos); //Jump to where the sprite is stored

                //Save sprite data
                sprites[j] = new Sprite(buffer.getBytes(buffer.getU16()));

                buffer.position(storedPos); //Jump back to previous position
            }

            picImage.setSprites(sprites);
            picImages[i] = picImage;
        }

        pic.setPicImages(picImages);

        log.debug("Done reading pic");

        return pic;
    }

    public void writePic(File picFile, Pic pic) {

        WritableDataBuffer buffer = new WritableDataBuffer(pic.getNumberOfBytes());

        log.debug("Start compiling pic...");

        buffer.putU32(pic.getSignature());

        buffer.putU16(pic.getNumberOfImages());

        PicImage[] picImages = pic.getPicImages();

        //Calculate where we can put the first sprite
        int spritePos = buffer.position();
        for (PicImage picImage : picImages) {
            spritePos += 5 + picImage.getWidth() * picImage.getHeight() * Integer.BYTES;
        }

        for (int i = 0; i < picImages.length; i++) {
            log.debug("Compiling image " + (i + 1));

            buffer.putU8(picImages[i].getWidth());
            buffer.putU8(picImages[i].getHeight());
            PicColor bgColor = picImages[i].getBgColor();
            buffer.putByte(bgColor.getR());
            buffer.putByte(bgColor.getG());
            buffer.putByte(bgColor.getB());

            Sprite[] sprites = picImages[i].getSprites();
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
}
