package io.github.elime1.piceditor.models;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PicDisplayInfo {
    private String version;
    private String signature;
    private String numberOfImages;
}
