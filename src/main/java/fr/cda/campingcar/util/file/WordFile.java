package fr.cda.campingcar.util.file;

import fr.cda.campingcar.util.LoggerConfig;
import javafx.concurrent.Task;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class WordFile
{
    protected static final Logger LOGGER_FILE = LoggerConfig.getLoggerFile();
    protected String file;
    protected XWPFDocument document = new XWPFDocument();
    protected FileOutputStream fos;

    public WordFile(String file)
    {
        this.file = file;
    }

    protected void newDocument()
    {
        try {
            this.fos = new FileOutputStream(this.file);
        } catch ( Exception e ) {
            LOGGER_FILE.warn("Échec lors de la création du XDOC : {}", e.getMessage(), e);
        }
    }

    private void writeFile() throws IOException
    {
            this.document.write(this.fos);
            this.fos.close();
    }

    public byte[] getTempFile() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            this.document.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            LOGGER_FILE.error("Erreur lors de la création du fichier temporaire XDOC : {}", e.getMessage(), e);
            return new byte[0];
        }
    }


    public Task<Void> save() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception
            {
                writeFile();
                return null;
            }
        };
    }
}
