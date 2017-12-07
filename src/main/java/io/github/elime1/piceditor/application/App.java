package io.github.elime1.piceditor.application;

import io.github.elime1.piceditor.application.spring.SpringConfig;
import io.github.elime1.piceditor.application.spring.SpringFxmlLoader;
import io.github.elime1.piceditor.controllers.MainViewController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.List;

public class App extends Application {

    private static Logger log = LogManager.getLogger();
    private Stage window;

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage window) throws Exception {

        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        AppInfo appInfo = context.getBean(AppInfo.class);

        Thread.currentThread().setName("Main thread");

        log.info("Starting " + appInfo.getAppName());
        log.info("Version " + appInfo.getAppVersion());

        this.window = window;
        this.window.setTitle(appInfo.getAppName() + " " + appInfo.getAppVersion());
        this.window.getIcons().add(new Image(App.class.getResourceAsStream("/img/icon.png")));

        //onCloseRequest is not triggered when calling window.close() so we use onHiding instead
        window.setOnHiding(event -> {
            log.info("Exiting PicEditor");
        });

        log.debug("Setup uncaught exception handler");
        setupExceptionHandling();

        log.debug("Creating scene...");
        window.setScene(createScene(context));
        log.debug("Scene created");

        window.centerOnScreen();

        log.debug("Showing application window");
        window.show();

        //Check for command-line argument
        List<String> parameters = getParameters().getUnnamed();
        if (!parameters.isEmpty()) {
            context.getBean(MainViewController.class).openPic(parameters.get(0));
        }
    }

    private void setupExceptionHandling() {
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> showErrorAlert(throwable));
    }

    private Scene createScene(ApplicationContext context) throws IOException {
        String css = getClass().getResource("/css/style.css").toExternalForm();
        SpringFxmlLoader loader = new SpringFxmlLoader(context);
        Parent parent = (Parent) loader.load("/view/main.fxml");
        Scene scene = new Scene(parent, 790, 480);
        scene.getStylesheets().add(css);
        return scene;
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
