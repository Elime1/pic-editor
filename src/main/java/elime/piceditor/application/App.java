package elime.piceditor.application;
import elime.piceditor.controllers.util.ViewHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;

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
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> showErrorAlert(thread, throwable));

        //onCloseRequest is not triggered when calling window.close() so we use onHiding instead
        window.setOnHiding(event -> {
            log.info("Exiting PicEditor");
        });

        log.debug("Creating scene...");
        ViewHandler viewHandler = new ViewHandler();
        AnchorPane root = new AnchorPane();
        root.getChildren().add(viewHandler);
        root.setBottomAnchor(viewHandler, 0.0);
        root.setTopAnchor(viewHandler, 0.0);
        root.setLeftAnchor(viewHandler, 0.0);
        root.setRightAnchor(viewHandler, 0.0);

        Scene scene = new Scene(root, 790, 480);
        String css = getClass().getResource("/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        window.setScene(scene);
        log.debug("Scene created");

        log.debug("Loading views...");
        //Load views after scene has been created
        viewHandler.loadView("main", "/view/main.fxml");
        viewHandler.setView("main");
        log.debug("Views loaded");

        window.centerOnScreen();

        log.debug("Showing application window");
        window.show();
    }

    private void showErrorAlert(Thread thread, Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        String message = throwable.getMessage();
        if (message == null) {
            alert.setContentText(throwable.toString());
        } else {
            alert.setContentText(message);
        }
        alert.initOwner(this.window);
        alert.show();
    }
}