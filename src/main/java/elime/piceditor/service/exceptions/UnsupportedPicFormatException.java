package elime.piceditor.service.exceptions;

/**
 * Created by Elime on 15-08-17.
 */
public class UnsupportedPicFormatException extends Exception {
    public UnsupportedPicFormatException(String message) {
        super(message);
    }
    public UnsupportedPicFormatException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
