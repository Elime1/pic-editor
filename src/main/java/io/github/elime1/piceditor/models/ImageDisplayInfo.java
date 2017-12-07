package io.github.elime1.piceditor.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageDisplayInfo {
    private String imageCount;
    private String imageDimensions;
    private String bgHex;
    private String bgRgb;
}
