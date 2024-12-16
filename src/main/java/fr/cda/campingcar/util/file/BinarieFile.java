package fr.cda.campingcar.util.file;

import fr.cda.campingcar.settings.Config;
import fr.cda.campingcar.util.LoggerConfig;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public class BinarieFile<T>  extends FileTemp<Map<String, T>>
{
    private static final Logger LOGGER_FILE = LoggerConfig.getLoggerFile();

    public BinarieFile(String fileName) {

        super(Config.BINARIE_FOLDER_PATH, fileName);
        this.tempFile = new File(this.directory, this.fileName);

    }

    @Override
    public void writeFile(Map<String, T> data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(tempFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(data);
            oos.flush();
            LOGGER_FILE.info("Succès de la sérialisation des données : " + tempFile.getAbsolutePath());

        } catch (IOException e) {
            LOGGER_FILE.warn("Échec d'écriture du fichier binaire : " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, T> readFile() throws IOException {
        if (!tempFile.exists()) {
            LOGGER_FILE.warn("Fichier binaire inexistant : " + tempFile.getAbsolutePath());
            return new HashMap<>();
        }

        try (FileInputStream fis = new FileInputStream(tempFile);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            Map<String, T> data = (Map<String, T>) ois.readObject();
            LOGGER_FILE.info("Succès de la lecture du fichier binaire : " + tempFile.getAbsolutePath());
            return data;

        } catch (IOException e) {
            LOGGER_FILE.warn("Échec de la lecture du fichier binaire : " + e.getMessage(), e);
            return new HashMap<>();
        } catch (ClassNotFoundException e) {
            LOGGER_FILE.warn("Échec de la lecture du fichier binaire (Class non trouvée) : " + e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Date getLastModified() {
        return tempFile.exists() ? new Date(tempFile.lastModified()) : null;
    }

    @Override
    protected String getExtension() {
        return Config.BINARIE_EXT;
    }

}
