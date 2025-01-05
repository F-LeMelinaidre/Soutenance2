package fr.cda.campingcar.util.file;

import fr.cda.campingcar.util.LoggerConfig;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WordFile
{
    private static final Logger LOGGER_FILE = LoggerConfig.getLoggerFile();

    private String file;

    public WordFile(String file)
    {
        this.file = file;
    }


    protected XWPFDocument newDocument()
    {
        XWPFDocument document  = null;
        try {
            document = new XWPFDocument();
            FileOutputStream fos = new FileOutputStream(file);
        } catch ( Exception e ) {
            LOGGER_FILE.warn("Échec lors de la création du XDOC : {}", e.getMessage(), e);
        }

        return document;
    }

    public void writeFile() throws IOException
    {
        try {
            XWPFDocument document = new XWPFDocument();
            FileOutputStream fos = new FileOutputStream(this.file);

            XWPFParagraph paragraph = document.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = paragraph.createRun();

            run.setText("Normally, both your asses would be dead as fucking fried chicken.");
            run.setFontFamily("Times New Roman");
            run.setColor("FF8833");
            run.setBold(true);
            run.setFontSize(18);

            run.addBreak();
            run.addBreak();

            String imgFile = "src/main/resources/poulet.jpg";

            FileInputStream fis = new FileInputStream(imgFile);
            run.addPicture(fis, XWPFDocument.PICTURE_TYPE_JPEG, imgFile, Units.toEMU(300), Units.toEMU(200));

            document.write(fos);
            fos.close();
        } catch ( InvalidFormatException e ) {
            e.printStackTrace();
        }
    }

    public void readFile() throws IOException
    {
        FileInputStream fis = new FileInputStream(this.file);
        XWPFDocument document = new XWPFDocument(fis);
        XWPFWordExtractor extractor = new XWPFWordExtractor(document);
        String text = extractor.getText();
        System.out.println(text);
        fis.close();
    }
}
