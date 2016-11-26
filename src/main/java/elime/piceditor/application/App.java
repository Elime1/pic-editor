package elime.piceditor.application;
import elime.piceditor.controllers.MainViewController;
import elime.piceditor.service.util.Services;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.util.List;

/**
 * Created by Elime on 15-08-16.
 */
public class App extends Application {

    private static Logger log = LogManager.getLogger();
    private Stage window;

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage window) throws Exception {
        Thread.currentThread().setName("Main thread");

        log.info("Starting PicEditor");

        this.window = window;
        this.window.setTitle(AppInfo.APP_NAME);
        this.window.getIcons().add(new Image(App.class.getResourceAsStream("/img/icon.png")));

        log.debug("Setup uncaught exception handler");
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> showErrorAlert(throwable));

        //onCloseRequest is not triggered when calling window.close() so we use onHiding instead
        window.setOnHiding(event -> {
            log.info("Exiting PicEditor");
        });

        log.debug("Creating scene...");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));

        Scene scene = new Scene(loader.load(), 790, 480);
        String css = getClass().getResource("/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        window.setScene(scene);

        MainViewController viewController = loader.getController();
        viewController.init(window, new Services());

        log.debug("Scene created");

        window.centerOnScreen();

        log.debug("Showing application window");
        window.show();

        //Check for command-line argument
        List<String> parameters = getParameters().getUnnamed();
        if (!parameters.isEmpty()) {
            viewController.openPic(parameters.get(0));
        }
    }

    private void showErrorAlert(Throwable throwable) {
        String message = throwable.getMessage();
        if (message == null) {
            message = throwable.toString();
        }
        log.error(message, throwable);
        showErrorAlert(message);
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(this.window);
        alert.show();
    }
}