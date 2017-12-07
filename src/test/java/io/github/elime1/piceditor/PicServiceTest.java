package io.github.elime1.piceditor;

import io.github.elime1.piceditor.application.spring.SpringConfig;
import io.github.elime1.piceditor.models.Pic;
import io.github.elime1.piceditor.service.ImageService;
import io.github.elime1.piceditor.service.PicService;
import io.github.elime1.piceditor.service.VersionService;
import io.github.elime1.piceditor.service.exceptions.UnsupportedPicFormatException;
import io.github.elime1.piceditor.utils.PicIO;
import io.github.elime1.piceditor.utils.PicImageConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfig.class)
public class PicServiceTest {

    @Mock
    private PicIO picIO;
    @Mock
    private PicImageConverter picImageConverter;
    @Mock
    private VersionService versionService;
    @Mock
    private ImageService imageService;

    @InjectMocks
    @Autowired
    private PicService picService;

    @Before
    public void setup() throws UnsupportedPicFormatException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void currentlyDisplayedImageNumberTest() throws UnsupportedPicFormatException {

        Pic pic = new Pic();
        pic.setNumberOfImages(5);

        when(picIO.readPic(null)).thenReturn(pic);

        picService.loadPic(null);
        picService.previousImage();
        picService.previousImage();

        assertEquals("Wrong image number", 4, picService.getCurrentImageNumber());

        picService.nextImage();
        picService.nextImage();
        picService.nextImage();

        assertEquals("Wrong image number", 2, picService.getCurrentImageNumber());
    }
}
