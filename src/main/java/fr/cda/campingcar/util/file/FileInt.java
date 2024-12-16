package fr.cda.campingcar.util.file;

import java.io.IOException;
import java.util.Date;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public interface FileInt<T>
{
    void writeFile(T data) throws IOException;
    T readFile() throws IOException;
    Date getLastModified();
}
