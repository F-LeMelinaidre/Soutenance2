package fr.cda.campingcar.util.sendmail;


import fr.cda.campingcar.util.LoggerConfig;
import org.apache.logging.log4j.Logger;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import javafx.concurrent.Task;

/*
 * Soutenance Scraping
 * 2025/janv.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public class Mail
{
    private static final Logger LOGGER_MAIL = LoggerConfig.getLoggerMail();
    private String template;
    private String sender = "";
    private String senderName = "A Thousand Miles";
    private String mailTo;
    private String subject;
    private byte[] attachment;
    private List<SendSmtpEmailAttachment> attachmentList = new ArrayList<SendSmtpEmailAttachment>();

    public Mail()
    {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey("");

    }


    public void setMailTo(String mailTo)
    {
        this.mailTo = mailTo;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public void setTemplate(String template)
    {
        this.template = template;
    }

    public void addAttachment(String name, byte[] file)
    {
        SendSmtpEmailAttachment attachment = new SendSmtpEmailAttachment();
        attachment.setName(name);
        attachment.setContent(file);
        this.attachmentList.add(attachment);
    }

    private void buildAndSendMail() throws Exception
    {

        try {
            TransactionalEmailsApi api = new TransactionalEmailsApi();
            SendSmtpEmailSender sender = new SendSmtpEmailSender();

            sender.setEmail(this.sender);
            sender.setName(this.senderName);

            List<SendSmtpEmailTo> toList = new ArrayList<SendSmtpEmailTo>();
            SendSmtpEmailTo to = new SendSmtpEmailTo();
            to.setEmail(this.mailTo);
            /*to.setName("John Doe");*/
            toList.add(to);


            Properties params = new Properties();
            params.setProperty("subject", this.subject);
            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
            sendSmtpEmail.setSender(sender);
            sendSmtpEmail.setTo(toList);

            sendSmtpEmail.setHtmlContent(this.template);
            sendSmtpEmail.setSubject("{{params.subject}}");


            if(this.attachmentList != null) {
                sendSmtpEmail.setAttachment(this.attachmentList);
            }

            sendSmtpEmail.setParams(params);

            api.sendTransacEmail(sendSmtpEmail);
            LOGGER_MAIL.info("L'e-mail a été envoyé avec succès.");
        } catch ( Exception e ) {
            LOGGER_MAIL.error("Erreur lors de l'envoi de l'e-mail : {}", e.getMessage(), e);
            throw e;
        }
    }

    public Task<Void> send()
    {
        return new Task<>()
        {
            @Override
            protected Void call() throws Exception
            {
                buildAndSendMail();
                return null;
            }
        };
    }
}
