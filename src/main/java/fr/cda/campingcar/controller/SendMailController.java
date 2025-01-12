package fr.cda.campingcar.controller;

import fr.cda.campingcar.model.Search;
import fr.cda.campingcar.util.Validator;
import fr.cda.campingcar.util.file.tamplate.word.SearchRentXDOC;
import fr.cda.campingcar.util.render.FXMLRender;
import fr.cda.campingcar.util.render.FXMLWindow;
import fr.cda.campingcar.util.sendmail.Mail;
import fr.cda.campingcar.util.sendmail.templatemail.SearchMail;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

public class SendMailController extends FXMLWindow implements Initializable
{

    @FXML
    private VBox sendMailPane;

    @FXML
    private TextField emailField;

    @FXML
    private Label hintLabel;

    @FXML
    private Button validButton;

    @FXML
    private Button cancelButton;

    private String template;
    private byte[] tempFile;
    private String subject;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

        this.mainPane = this.sendMailPane;
        super.initialize(url, resourceBundle);
        this.validButton.setDisable(true);
    }

    @Override
    public void closeWindow()
    {
        super.closeWindow();
    }

    public void setSearchMail(Search search)
    {
        SearchMail searchMailTemplate = new SearchMail();
        searchMailTemplate.setSiteList(search.getListSites());
        this.template = searchMailTemplate.getTemplate();

        SearchRentXDOC attachmentDoc = new SearchRentXDOC(search);
        this.tempFile = attachmentDoc.getTempFile();
        this.subject = "Votre Recherche";
    }


    @FXML
    private void submitForm(MouseEvent event)
    {
        String email = emailField.getText();
        Mail mail = new Mail();
        mail.setMailTo(email);
        mail.setTemplate(this.template);
        mail.setSubject(this.subject);
        mail.addAttachment("recherche-van.docx",this.tempFile);

        Task<Void> sendTask = mail.send();

        AlertMessageController alertMessageController = FXMLRender.openNewWindow("window/alertMessage.fxml", "Envoi d'E-mail");
        sendTask.setOnScheduled(e -> {
            this.closeWindow();
            alertMessageController.setMessage("Envoi en cours!\n Destinataire : " + email, "load");
        });

        sendTask.setOnSucceeded(e -> {
            alertMessageController.setMessage("Succès de l'envoi à:\n" + email, "valid");
        });

        sendTask.setOnFailed(e -> {
            alertMessageController.setMessage("Échec  de l'envoi à:\n" + email, "error");
        });

        Thread saveThread = new Thread(sendTask);
        saveThread.setDaemon(true);
        saveThread.start();
    }

    @FXML
    public void validTextField(KeyEvent event)
    {
        String    value     =  this.emailField.getText();

        Validator.clearClass(this.emailField);
        Validator.clearClass(this.hintLabel);

        boolean isValid = Validator.isNotEmpty(value) && Validator.isMail(value);

        validButton.setDisable(!isValid);

        String style = Validator.getValidatorStyle();
        this.emailField.getStyleClass().add(style);
        this.hintLabel.getStyleClass().add(style);
        this.hintLabel.setText(Validator.getValidatorMessage());
    }
}
