package io.github.elime1.piceditor.service.exceptions;

public class UnsupportedPicFormatException extends Exception {
    public UnsupportedPicFormatException(String message) {
        super(message);
    }
    public UnsupportedPicFormatException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
