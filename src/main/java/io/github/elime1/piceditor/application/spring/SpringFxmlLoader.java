package io.github.elime1.piceditor.application.spring;

import javafx.fxml.FXMLLoader;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SpringFxmlLoader {

    private ApplicationContext context;

    public SpringFxmlLoader(ApplicationContext appContext) {
        this.context = appContext;
    }

    public Object load(final String resource) throws IOException {
        try (InputStream fxmlStream = getClass().getResourceAsStream(resource)) {
            FXMLLoader loader = new FXMLLoader();
            URL location = getClass().getResource(resource);
            loader.setLocation(location);
            loader.setControllerFactory(context::getBean);
            return loader.load(fxmlStream);
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }
}
