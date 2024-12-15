package fr.cda.campingcar.scraping;

import fr.cda.campingcar.model.Dom;
import fr.cda.campingcar.model.Recherche;
import fr.cda.campingcar.model.Site;
import fr.cda.campingcar.scraping.exception.HTMLElementException;
import fr.cda.campingcar.settings.Config;
import fr.cda.campingcar.util.LoggerConfig;
import javafx.concurrent.Task;
import org.apache.logging.log4j.Logger;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.WebClient;
import org.htmlunit.html.*;
import org.htmlunit.xpath.operations.Div;

import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Class permettant le scraping asynchrone d une liste de recherche de pages internet
 */
public class ScrapingManager
{
    protected static final Logger LOGGER_SCRAPING = LoggerConfig.getLoggerScraping();

    private static ScrapingManager _instance = null;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private Recherche recherche;

    public static synchronized ScrapingManager getInstance()
    {
        if ( _instance == null ) {
            _instance = new ScrapingManager();
        }
        return _instance;
    }

    /**
     * Crée une nouvelle tâche de scraping pour une liste de sites de l'objet {@link Recherche}.
     *
     * <p>Créé un objet {@link Future} pour chaque site, et l'ajoute à une liste {@code List<Future<String>> futures}, <br>
     * permettant le traitement des tâches de manière asynchrone.
     * </p>
     * Chaque objets {@link Future} de la liste permettent :
     * <ul>
     *     <li>D'attendre l'achèvement des tâches et sous tâches</li>
     *     <li>Gérer les résultats ou les exceptions qui peuvent survenir</li>
     * </ul>
     * <p>Cette méthode fermera le service d'exécution après que toutes les tâches soient terminées.</p>
     *
     * @param recherche L'objet {@link Recherche} ne doit pas être nul et doit contenir au moins un site.
     * @return Une {@link Task} Effectue l'opération de manière asynchrone, et renverra null à son achèvement.
     * @throws IllegalArgumentException si l'objet {@link Recherche} est nulle ou ne contient aucun site.
     */
    public Task<Void> scrapTask(Recherche recherche)
    {
        this.recherche = recherche;
        List<Site> sites = recherche.getListSites();

        return new Task<>()
        {
            @Override
            protected Void call()
            {
                debug("SCRAP_TASK", "Nombre de sites", String.valueOf(sites.size()), null, true);
                List<Future<List<ScrapingModel>>> futures = new ArrayList<>();

                // PARCOURS LES SITES DE LA RECHERCHE
                for ( Site site : sites ) {
                    LOGGER_SCRAPING.info("Recherche sur " + site.getNom());
                    // CREE LA TACHE POUR OUVRIR LA PAGE DU SITE
                    futures.add(executor.submit(scrapingCards(site)));
                }

                for ( Future<List<ScrapingModel>> future : futures ) {
                    try {
                        List<ScrapingModel> resultats = future.get();

                        recherche.addResultats(resultats);
                    } catch ( InterruptedException | ExecutionException e ) {
                        debug("SCRAP_TASK", "InterruptedException | ExecutionException", "FUTURE LIST", e.getMessage(), false);
                        LOGGER_SCRAPING.error("Erreur lors du scraping: " + e.getMessage(), e);
                    }
                }

                executor.shutdown();
                return null;
            }
        };
    }

    /**
     *
     * @param site
     * @return Callable
     */
    private Callable<List<ScrapingModel>> scrapingCards(Site site)
    {
        debug("SCRAPING_CARDS", "Site", site.getNom(), site.getUrlRecherche(), true);
        List<Future<Void>>  futures = new ArrayList<>();
        List<ScrapingModel> models  = new ArrayList<>();
        String              url     = site.getUrlRecherche();


        return () -> {
            WebClient webClient = createWebClient();
            HtmlPage  page      = openPage(webClient, url);

            if ( page != null ) {

                Dom       domCard        = site.getDomMap().get("card");
                List<Dom> domCardEnfants = domCard.getListEnfants();

                List<HtmlElement> cards = page.getByXPath(domCard.getXPath());
                debug("SCRAPING CARDS", "Nombre de cards", String.valueOf(cards.size()), null, true);

                cards.forEach(card -> {
                    //debug("SCRAPING CARDS", "Cards numéro", String.valueOf(c.get()), null, true);

                    ScrapingModel model = recherche.createScrapingSupplier();
                    model.setDomainUrl(site.getUrlRoot());

                    this.scrapElements(card, domCardEnfants, model);

                    models.add(model);
                });

            }

            for ( ScrapingModel model : models ) {

                futures.add(executor.submit(scrapPage(webClient, model.getUrl(), model, site.getDomMap().get("page"))));
            }

            for ( Future<Void> future : futures ) {
                try {
                    future.get();
                } catch ( InterruptedException | ExecutionException e ) {
                    debug("FUTURE ERROR", "InterruptedException | ExecutionException", "ERREUR", e.getMessage(), false);
                    LOGGER_SCRAPING.error("Erreur lors du scraping: " + e.getMessage(), e);
                }
            }
            return models;
        };
    }

    /**
     * @param webClient WebClient
     * @param url url
     * @param model {@link ScrapingModel}
     * @param domPage {@link Dom}
     * @return Callable
     */
    private Callable<Void> scrapPage(WebClient webClient, String url, ScrapingModel model, Dom domPage)
    {
        //debug("SCRAPING PAGE", "Page", String.valueOf(p.get()), null, true);

        try {
            Thread.sleep(new Random().nextInt(2000));
        } catch ( InterruptedException e ) {
            debug("SCRAPING PAGE", "InterruptedException", "TIME THREAD SLEEP", e.getMessage(), false);
        }

        return () -> {
            HtmlPage page = openPage(webClient, url);

            if ( (page != null) ) {

                try {
                    HtmlElement conteneur = page.getFirstByXPath(domPage.getXPath());

                    if ( conteneur == null )
                        throw new HTMLElementException(
                                "Url: " + url + " | Contneur: " + domPage.getNom() + " | XPath: " + domPage.getXPath());

                    List<Dom>    domEnfants      = domPage.getListEnfants();
                    List<String> pagesSecondaire = this.scrapElements(conteneur, domEnfants, model);

                } catch ( HTMLElementException e ) {
                    debug("ERROR SCRAP PAGE HTML_ELEMENT", "HTMLElementException", domPage.getNom(),
                          page.getDocumentURI() + " " + domPage.getXPath(),
                          false);
                    LOGGER_SCRAPING.warn(
                            "ERROR SCRAP PAGE HTML_ELEMENT : " + e.getMessage(), e);
                }


                //debug("SCRAPING PAGE", "Page Secondaire", String.valueOf(pageSecondaire.size()), pageSecondaire.toString(), true);
            }
            return null;
        };
    }

    /**
     * @param conteneur {@link HtmlElement}
     * @param domCardEnfants {@code List<String>}
     * @param model {@link ScrapingModel}
     * @return List<String>
     */
    private List<String> scrapElements(HtmlElement conteneur, List<Dom> domCardEnfants, ScrapingModel model)
    {
        List<String> liensSecondaire = new ArrayList<>();

        domCardEnfants.forEach(dom -> {

            try {
                HtmlElement element = conteneur.getFirstByXPath("." + dom.getXPath());

                if ( element != null ) {
                    String value = dom.getNom().contains("lien") ? element.getAttribute("href").trim() : element.getTextContent().trim();

                    if ( dom.getNom().equals("lien secondaire") ) {

                        liensSecondaire.add(value);
                    } else if ( dom.getNom().equals("image") ) {

                        value = this.getImage(element);
                    }
                    if ( !value.isEmpty() ) {
                        model.setPropertieModel(dom.getNom(), value);
                    }

                } else {
                    throw new HTMLElementException(" Element: " + dom.getNom() + " | " + dom.getXPath());
                }

            } catch ( HTMLElementException e ) {
                LOGGER_SCRAPING.warn("SCRAP ELEMENT : " + e.getMessage(), e);
                debug("SCRAP ELEMENT", "HTMLElementException", dom.getNom(), conteneur.getNodeName() + " " + dom.getXPath(), false);
            }
        });
        return liensSecondaire;
    }

    private String getImage(HtmlElement element)
    {
        String value = "";
        if ( element instanceof HtmlImage imgElement ) {
            value = imgElement.getAttribute("src");
        } else if ( element instanceof HtmlSource sourceElement ) {
            value = sourceElement.getAttribute("srcset");
            String[] values = value.trim().split(" ");
            value = values[0];
        } else if ( "div".equals(element.getTagName()) ) {
            value = element.getAttribute("data-src");
        }
        return value;
    }

    private HtmlPage openPage(WebClient webClient, String url)
    {
        HtmlPage page = null;
        //System.out.println(url);
        try {
            page = webClient.getPage(url);

        } catch ( MalformedURLException e ) {
            debug("OPEN PAGE", "MalformedURLException", "ERREUR OUVERTURE", e.getMessage(), false);
            System.out.println("ERREUR OUVERTURE PAGE : " + e.getMessage());
            LOGGER_SCRAPING.error("Erreur Format url " + url + " : " + e.getMessage(), e);

        } catch ( FailingHttpStatusCodeException e ) {
            debug("OPEN PAGE", "FailingHttpStatusCode", "ERREUR OUVERTURE", e.getMessage(), false);
            int    statusCode   = e.getStatusCode();
            String errorMessage = e.getMessage();
            LOGGER_SCRAPING.error("Erreur Statut " + url + ": " + statusCode + " - " + errorMessage, e);

        } catch ( Exception e ) {
            debug("OPEN PAGE", "Exception", "ERREUR OUVERTURE", e.getMessage(), false);
            LOGGER_SCRAPING.error("Erreur OpenPage " + url + " : " + e.getMessage(), e);

        }
        return page;
    }

    public void reset() {
        executor.shutdownNow();
        executor = Executors.newCachedThreadPool();
        LOGGER_SCRAPING.info("ScrapingManager reset.");
    }

    /**
     * @return {@link WebClient}
     */
    private WebClient createWebClient()
    {
        WebClient webClient = new WebClient();

        webClient.getOptions().setUseInsecureSSL(Setting.SSL);
        webClient.getOptions().setCssEnabled(Setting.ENABLE_CSS);
        webClient.getOptions().setJavaScriptEnabled(Setting.ENABLE_JS);

        webClient.waitForBackgroundJavaScript(Setting.JS_TIMEOUT);
        webClient.getOptions().setTimeout(Setting.TIMEOUT);

        webClient.getOptions().setThrowExceptionOnScriptError(Setting.THROW_EXCEPTION_ON_JS_ERROR);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(Setting.THROW_EXCEPTION_ON_STATUS_CODE);


        return webClient;
    }

    /**
     * @param task Nom de la Task
     * @param titre Titre de l'événement
     * @param statut Status de L'événement
     * @param details Détail de l'événement
     * @param valid true invalide false (défini la couleur de la valeur du paramètre statut)
     */
    private void debug(String task, String titre, String statut, String details, Boolean valid)
    {
        String color = (valid) ? Config.GREEN : Config.RED;
        titre  = (titre != null ? titre : "");
        statut = (statut != null) ? statut : "";
        StringBuilder sb = new StringBuilder(Config.PURPLE);
        sb.append(task).append("\n")
          .append(Config.YELLOW).append(String.format("%3s-", "")).append(titre).append(" :")
          .append(color).append(String.format("%2s", "")).append(statut).append("\n");
        if ( details != null ) {
            sb.append(Config.CYAN).append(String.format("%4s-", "")).append(details);
        }
        sb.append(Config.RESET);
        System.out.println(sb);
        sb.setLength(0);
    }
}