package fr.cda.campingcar.util.file;


import fr.cda.campingcar.settings.Config;
import fr.cda.campingcar.util.LoggerConfig;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Date;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public class BinarieFile<T> {
    private static final Logger LOGGER_FILE = LoggerConfig.getLoggerFile();
    private final String filePath;
    private final String fileName;
    private final File tempFile;

    public BinarieFile(String fileName) {
        this.filePath = Config.BINARIE_FOLDER_PATH; // Assurez-vous que le chemin est correctement configuré
        this.fileName = fileName + "." + Config.BINARIE_EXT; // Ajouter l'extension
        this.tempFile = new File(this.filePath, this.fileName);
        File directory = new File(filePath);

        if (!directory.exists()) {
            if (directory.mkdirs()) {
                LOGGER_FILE.info("Répertoire créé : " + filePath);
            } else {
                LOGGER_FILE.warn("Échec de la création du répertoire : " + filePath);
            }
        }
    }

    public void writeFile(T data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(tempFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(data);
            oos.flush();
            LOGGER_FILE.info("Succès de la sérialisation des données : " + tempFile.getAbsolutePath());

        } catch (IOException e) {
            LOGGER_FILE.warn("Échec d'écriture du fichier binaire : " + e.getMessage(), e);
            throw e; // Lancer l'exception pour le traitement en amont
        }
    }

    public T readFile() throws IOException {
        if (!tempFile.exists() || tempFile.length() == 0) {
            LOGGER_FILE.warn("Fichier binaire inexistant : " + tempFile.getAbsolutePath());
            return null;
        }

        try (FileInputStream fis = new FileInputStream(tempFile);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            @SuppressWarnings("unchecked") // Suppression d'avertissement pour le cast
            T data = (T) ois.readObject();
            LOGGER_FILE.info("Succès de la lecture du fichier binaire : " + tempFile.getAbsolutePath());
            return data;
        } catch (IOException e) {
            LOGGER_FILE.warn("Échec de la lecture du fichier binaire : " + e.getMessage(), e);
            throw e; // Relancer l'exception pour la gestion à un niveau supérieur
        } catch (ClassNotFoundException e) {
            LOGGER_FILE.warn("Échec de la lecture du fichier binaire (Class non trouvée) : " + e.getMessage(), e);
            throw new IOException("Classe non trouvée lors de la lecture du fichier binaire", e);
        }
    }

    public Date getLastModified() {
        return tempFile.exists() ? new Date(tempFile.lastModified()) : null;
    }

    public boolean fileExists() {
        return tempFile.exists();
    }
}