package elime.piceditor.service.util;

import elime.piceditor.service.*;

import java.awt.*;

/**
 * Created by Elime on 15-09-07.
 */
public class Services {

    private PreferencesService preferencesService;
    private PicService picService;
    private ImageService imageService;
    private FileChooserService fileChooserService;
    private VersionService versionService;

    public Services() {
        this.preferencesService = new PreferencesService();
        this.picService = new PicService();
        this.imageService = new ImageService();
        this.fileChooserService = new FileChooserService(preferencesService);
        this.versionService = new VersionService();
    }

    public PreferencesService getPreferencesService() {
        return preferencesService;
    }

    public PicService getPicService() {
        return picService;
    }

    public ImageService getImageService() {
        return imageService;
    }

    public FileChooserService getFileChooserService() {
        return fileChooserService;
    }

    public VersionService getVersionService() {
        return versionService;
    }
}
