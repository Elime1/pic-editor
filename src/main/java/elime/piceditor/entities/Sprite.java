package elime.piceditor.entities;

/**
 * Created by Elime on 15-08-16.
 */
public class Sprite {

    public static final int DEFAULT_W_H = 32;

    private byte[] pixelData;

    public Sprite(byte[] pixelData) {
        this.pixelData = pixelData;
    }

    public byte[] getPixelData() {
        return pixelData;
    }

    public void setPixelData(byte[] pixelData) {
        this.pixelData = pixelData;
    }

}
