package io.github.elime1.piceditor.application;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AppInfo {
    private final String appName = "Elime's Pic Editor";
    private final String appVersion = "1.2.0";
}
