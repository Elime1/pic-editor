package io.github.elime1.piceditor.service.exceptions;

public class WrongImageDimensionsException extends Exception {
    public WrongImageDimensionsException(String message) {
        super(message);
    }
    public WrongImageDimensionsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}