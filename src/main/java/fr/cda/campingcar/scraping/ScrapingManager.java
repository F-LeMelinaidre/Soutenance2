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
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Class permettant le scraping asynchrone d une liste de recherche de pages internet
 */
public class ScrapingManager
{
    protected static final Logger LOGGER = LoggerConfig.getLogger();
    protected static final Logger LOGGER_SCRAPING = LoggerConfig.getLoggerScraping();

    private static ScrapingManager _instance = null;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private ScrapingModel scrapingModel;
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
        List<Site>           sites   = recherche.getListSites();
        List<Future<String>> futures = new ArrayList<>();

        return new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {

                for ( Site site : sites ) {
                    LOGGER_SCRAPING.info("Recherche sur " + site.getNom());
                    futures.add(executor.submit(visiteSite(site)));
                }

                for ( Future<String> future : futures ) {
                    try {
                        String result = future.get();
                    } catch ( InterruptedException | ExecutionException e ) {
                        LOGGER_SCRAPING.error("Erreur lors du scraping: " + e.getMessage(), e);
                    }
                }

                executor.shutdown(); // Arrêter l'executor
                return null;
            }
        };
    }

    private Callable<String> visiteSite(Site site)
    {
        String url = site.getUrlRecherche();
        String nom = site.getNom();

        return () -> {
            String    result    = null;
            WebClient webClient = null;

            try {
                webClient = this.createWebClient();
                HtmlPage page = webClient.getPage(url);
                LOGGER_SCRAPING.info("Open page " + url);

                scrapCards(page, webClient, site);

                result = site.getNom();

            } catch ( MalformedURLException e ) {
                LOGGER_SCRAPING.error("Erreur lors de la visite du site " + nom + " : " + e.getMessage(), e);

            } catch ( FailingHttpStatusCodeException e ) {
                int    statusCode   = e.getStatusCode();
                String errorMessage = e.getMessage();
                LOGGER_SCRAPING.error("Erreur de statut pour l'URL " + url + ": " + statusCode + " - " + errorMessage, e);

            } catch ( Exception e ) {
                LOGGER_SCRAPING.error("Erreur lors de la visite du site " + nom + " : " + e.getMessage(), e);

            } finally {

                webClient.close();
                return result;
            }
        };
    }


    private void scrapCards(HtmlPage page, WebClient webClient, Site site)
    {

        Dom       domCard        = site.getDomMap().get("card");
        Dom       domPage        = site.getDomMap().get("page");
        List<Dom> domCardEnfants = domCard.getListEnfants();

        List<ScrapingModel> models  = new ArrayList<>();
        List<HtmlElement>   cards   = page.getByXPath(domCard.getXPath());
        List<Future<Void>>  futures = new ArrayList<>();
        LOGGER_SCRAPING.info("Scrap Cards " + site.getUrl() + " - nb Cards: " + cards.size());
        cards.forEach(card -> {
            ScrapingModel model = this.extractDatas(card, domCardEnfants);
            model.setDomainUrl(site.getUrlRoot());

            LOGGER_SCRAPING.info("Card " + model.toString());

            models.add(model);

            Future<Void> future = executor.submit(visiteCardPage(webClient, model, domPage)); // Appel correct ici
            futures.add(future);
        });


        for ( Future<Void> future : futures ) {
            try {
                future.get();
            } catch ( InterruptedException | ExecutionException e ) {
                LOGGER_SCRAPING.error("Erreur lors du scraping: " + e.getMessage(), e);
            }
        }

        System.out.println(models.toString());
    }

    private Callable<Void> visiteCardPage(WebClient webClient, ScrapingModel model, Dom domCardPage)
    {
        List<Dom> domCardPageEnfant = domCardPage.getListEnfants();
        String    url               = model.getUrl();
        LOGGER_SCRAPING.info("Visited page" + url);

        return () -> {

            try {
                HtmlPage    page      = webClient.getPage(url);
                HtmlElement conteneur = page.getFirstByXPath(domCardPage.getXPath());

                if ( conteneur != null ) {
                    extractDatas(model, conteneur, domCardPageEnfant);
                } else {
                    throw new HTMLElementException("Key: " + domCardPage.getNom() + " - xPath: " + domCardPage.getXPath());
                }

                LOGGER_SCRAPING.info("Open page " + url);
            } catch ( MalformedURLException e ) {
                LOGGER_SCRAPING.error("Erreur lors de la visite de CardPage " + url + " : " + e.getMessage(), e);

            } catch ( FailingHttpStatusCodeException e ) {
                int    statusCode   = e.getStatusCode();
                String errorMessage = e.getMessage();
                LOGGER_SCRAPING.error("Erreur de statut pour l'URL " + url + ": " + statusCode + " - " + errorMessage, e);

            } catch ( HTMLElementException e ) {
                LOGGER_SCRAPING.warn("Erreur de recupération de HTML ELement " + e.getMessage(), e);

            } catch ( Exception e ) {
                LOGGER_SCRAPING.error("Erreur lors de la visite  de CardPage " + url + " : " + e.getMessage(), e);

            }
            return null;
        };
    }

    /**
     * Parcours La liste des references dom pour extraire les valeur du HtmlConteneur
     *
     * @param conteneur Element Html principal de la recherche de valeur
     * @param doms      List des XPath des valeur recherché
     * @return {@code ScrapingModel} Retourne l'objet resultant de la recherche
     */
    private ScrapingModel extractDatas(HtmlElement conteneur, List<Dom> doms)
    {
        ScrapingModel model = this.recherche.createScrapingSupplier();
        return this.extractDatas(model, conteneur, doms);
    }

    private ScrapingModel extractDatas(ScrapingModel model, HtmlElement conteneur, List<Dom> doms)
    {
        for ( Dom dom : doms ) {
            try {
                HtmlElement element = conteneur.getFirstByXPath("." + dom.getXPath());

                if ( element != null ) {

                    String key   = dom.getNom();
                    String value = getValueElement(key, element);
                    model.setPropertieModel(key, value);

                } else {
                    throw new HTMLElementException("Key: " + dom.getNom() + " - xPath: " + dom.getXPath());
                }

            } catch ( HTMLElementException e ) {
                LOGGER_SCRAPING.warn("Erreur lors de l extraction de l'élémént HTML " + e.getMessage(), e);
            }
        }

        return model;
    }

    /**
     * Extrait la valeur de l element Html
     * La clé conditionne la forme d extraction
     *
     * @param key     Intitulé de la valeur recupéré
     * @param element Element Html ou recupérer la valeur recherché
     * @return {@code String} La valeur recherché
     */
    private String getValueElement(String key, HtmlElement element)
    {
        String result = "";
        if ( element != null ) {
            switch (key) {
                case "lien":
                    result = element.getAttribute("href").trim();
                    break;
                default:
                    result = element.getTextContent().trim();
                    break;
            }
        }
        return result;
    }

    private void shutdown()
    {
        this.executor.shutdown();
    }

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