package elime.piceditor.controllers;

import elime.piceditor.controllers.util.ViewHandler;
import elime.piceditor.service.util.Services;
import javafx.scene.Scene;

/**
 * Created by Elime on 15-08-04.
 */
public interface ControlledView {
    //This method will allow the injection of the Parent ScreenPane
    public void setViewParent(ViewHandler screenPage);

    //Inject services
    public void setServices(Services services);

    public void setupKeyListener(Scene scene);
}