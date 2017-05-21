package elime.piceditor.entities;

import lombok.Data;

/**
 * Created by Elime on 15-08-16.
 */

@Data
public class Sprite {

    public static final int DEFAULT_W_H = 32;

    private byte[] pixelData;

    public Sprite(byte[] pixelData) {
        this.pixelData = pixelData;
    }
}
