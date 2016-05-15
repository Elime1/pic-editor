package elime.piceditor.service.exceptions;

/**
 * Created by Elime on 15-09-04.
 */
public class WrongImageDimensionsException extends Exception {
    public WrongImageDimensionsException(String message) {
        super(message);
    }
    public WrongImageDimensionsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}