package fr.cda.campingcar.scraping.exception;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public class HTMLPageException extends RuntimeException
{
    public HTMLPageException(String message)
    {
        super(message);
    }

    public HTMLPageException(String message, Throwable cause) {
        super(message, cause);
    }
}
