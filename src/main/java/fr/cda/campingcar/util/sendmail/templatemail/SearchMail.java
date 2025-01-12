package fr.cda.campingcar.util.sendmail.templatemail;

import fr.cda.campingcar.model.Site;
import fr.cda.campingcar.util.sendmail.Mail;

import java.util.List;

/*
 * Soutenance Scraping
 * 2025/janv.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public class SearchMail implements MailTemplate
{

    private StringBuilder template = new StringBuilder("<html><body>");
    private StringBuilder listSite = new StringBuilder("<ul>");

    private void create()
    {
        this.template.append("<h1>Votre recherche</h1>");
        this.template.append("<p>Trouvez ci-joint votre dernière recherche sur :.</p>");
        this.template.append(this.listSite);
        this.template.append("</body></html>");
    }

    public void setSiteList(List<Site> siteList)
    {
        for ( Site site : siteList ) {
            this.listSite.append("<li>" + site.getName() + "</li>");
        }
        this.listSite.append("</ul>");
    }

    @Override
    public String getTemplate()
    {
        this.create();
        return this.template.toString();
    }
}
