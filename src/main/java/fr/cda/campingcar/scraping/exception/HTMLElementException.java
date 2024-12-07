package fr.cda.campingcar.scraping.exception;

public class HTMLElementException extends Exception {
    public HTMLElementException(String message) {
        super(message);
    }

    public HTMLElementException(String message, Throwable cause) {
        super(message, cause);
    }
}