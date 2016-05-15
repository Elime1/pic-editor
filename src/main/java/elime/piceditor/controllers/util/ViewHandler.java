package elime.piceditor.controllers.util;

import elime.piceditor.controllers.ControlledView;
import elime.piceditor.service.util.Services;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Elime on 15-08-04.
 */

public class ViewHandler extends StackPane {

    private static Logger log = LogManager.getLogger();

    private HashMap<String, Node> views = new HashMap<>();

    private Services services;

    public ViewHandler() {
        //Holds all the services
        services = new Services();
    }

    public void addView(String name, Node screen) {
        views.put(name, screen);
    }

    //Returns the Node with the appropriate name
    public Node getView(String name) {
        return views.get(name);
    }

    public void loadView(String name, String resource) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent view = loader.load();
            ControlledView viewController = loader.getController();
            viewController.setViewParent(this);
            viewController.setupKeyListener(this.getScene());
            viewController.setServices(services);
            addView(name, view);
            log.debug("View '" + resource + "' was loaded");
        } catch (IOException e) {
            log.error("Failde to load view: " + resource);
            throw new RuntimeException(e);
        }
    }

    public boolean unloadView(String name) {
        if (views.remove(name) == null) {
            log.warn("View '" + name + "' does not exist");
            return false;
        } else {
            return true;
        }
    }

    public boolean setView(final String name) {
        if (views.get(name) != null) {   //check if view is loaded
            final DoubleProperty opacity = opacityProperty();

            if (!getChildren().isEmpty()) { //if there is more than one view
                Timeline fade = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                    new KeyFrame(new Duration(1000), e -> {
                        getChildren().remove(0); //remove the view that is displayed
                        getChildren().add(0, views.get(name));
                        Timeline fadeIn = new Timeline(
                                new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                                new KeyFrame(new Duration(400), new KeyValue(opacity, 1.0)));
                        fadeIn.play();
                    }, new KeyValue(opacity, 0.0)));
                fade.play();
            } else {
                setOpacity(0.0);
                getChildren().add(views.get(name));
                Timeline fadeIn = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                        new KeyFrame(new Duration(1000), new KeyValue(opacity, 1.0)));
                fadeIn.play();
            }
            return true;
        } else {
            log.warn("View '" + name + "' needs to be loaded first!");
            return false;
        }
    }
}