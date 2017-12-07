package io.github.elime1.piceditor.models;

import lombok.Data;

@Data
public class Sprite {

    public static final int DEFAULT_W_H = 32;

    private byte[] pixelData;

    public Sprite(byte[] pixelData) {
        this.pixelData = pixelData;
    }
}
