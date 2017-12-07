package io.github.elime1.piceditor.models;

import lombok.Data;

@Data
public class Pic {
    private PicImage[] picImages;
    private int signature;
    private int numberOfImages;
    private int numberOfBytes;
}
