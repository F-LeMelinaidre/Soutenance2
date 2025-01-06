package fr.cda.campingcar.scraping;

import fr.cda.campingcar.model.Dom;
import fr.cda.campingcar.model.Search;
import fr.cda.campingcar.model.Site;
import fr.cda.campingcar.scraping.exception.HTMLElementException;
import fr.cda.campingcar.util.LoggerConfig;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.Logger;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.WebClient;
import org.htmlunit.html.*;

import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;

import static fr.cda.campingcar.util.DebugHelper.debug;

/**
 * Class permettant le scraping asynchrone d'une liste de recherche de pages internet.
 * <p>
 * L'objet {@link Search} passé en paramètre doit contenir une liste d'objet {@link Site} lui même contenant l'url de recherche, et un {@code HashMap}, représentant les xPaths des élément Html à scraper.
 * </p>
 * <p>
 * Le {@code HashMap<String, Dom>} est hiérarchise:
 * <ul>
 *      <li>La clé "card" représente le conteneur de l'annonce présente sur la première page du site.
 *          <ul>
 *              <li>Le xPath est accessible avec la méthode {@link Dom#getXPath()}</li>
 *              <li>Ce {@link Dom} contient une {@code List} représentant les éléments enfant de ce conteneur à scraper {@link Dom#geChildrensList()}.</li>
 *          </ul>
 *     </li>
 *     <li> La clé "page" représente la page lié à chaque annonce et est hiérarchisé de la même manière.</li>
 * </ul>
 * </p>
 * <p>
 * Chaque Annonce scrapé est stocké dans un objet qui implémente l'interface {@link ScrapingModel}.
 * La class implémentant {@link ScrapingModel}, est passer à l'objet {@link Search} à l'instanciation de celui-ci.
 * Cela nous permet:
 * <ul>
 *     <li>Créer des objets Annonce (ou autre si une autre class était passé en argument de Recherche)</li>
 *     <li>D'accéder à des méthodes pour setter les propriétés de l'objet</li>
 * </ul>
 * </p>
 * <p>
 * Une fois la tâche principale terminée, la méthode {@link ScrapingManager#scrapTask()},<br>
 * retourne l'objet recherche contenant l'ensemble des objets Annonce scrapé sur le ou les sites.
 * </p>
 */
public class ScrapingManager
{
    // TODO COUNTERTASK : DECREMENTER EN CAS D ERREUR D'ERREUR
    private static final Logger LOGGER_SCRAPING = LoggerConfig.getLoggerScraping();

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Search<ScrapingModel<Object>> recherche;
    private final List<Site> sites;
    private TaskCounter counterTask;

    public ScrapingManager(Search<ScrapingModel<Object>> recherche)
    {
        this.recherche = recherche;
        this.sites     = this.recherche.getListSites();

        this.counterTask = TaskCounter.getInstance();
    }

    /**
     * Crée une nouvelle tâche de scraping pour une liste de sites de l'objet {@link Search}.<br>
     * <p>Initialise le compteur {@link TaskCounter#setMainCounterTotal(int)}
     * <p>Créé un objet {@link Future} pour chaque site, et l'ajoute à une liste {@code List<Future<String>> futures}, <br>
     * permettant le traitement des tâches {@link #scrapCards} de manière asynchrone.
     * </p>
     * Chaque objets {@link Future} de la liste permettent :
     * <ul>
     *     <li>D'attendre l'achèvement des tâches et sous tâches</li>
     *     <li>Gérer les résultats ou les exceptions</li>
     * </ul>
     * <p>Cette méthode fermera le service d'exécution après que toutes les tâches soient terminées.</p>
     *
     * @return Une {@link Task} Effectue l'opération de manière asynchrone, et renverra null à son achèvement.
     * @throws IllegalArgumentException si l'objet {@link Search} est nulle ou ne contient aucun site.
     */
    public Task<Void> scrapTask()
    {

        return new Task<>()
        {

            @Override
            protected Void call()
            {

                Platform.runLater(() -> {
                    counterTask.setMainCounterTotal(sites.size());
                });

                List<Future<Void>> futures = new ArrayList<>();
                sites.forEach(site -> {
                    LOGGER_SCRAPING.info("Recherche sur {}", site.getName());
                    futures.add(executor.submit(scrapCards(site)));
                });

                futures.forEach(future -> {
                    try {
                        future.get();
                    } catch ( InterruptedException | ExecutionException e ) {
                        LOGGER_SCRAPING.warn("ERROR FUTURE : {}", e.getMessage(), e);
                    }
                });

                pauseExecution(500);
                executor.shutdown();
                return null;
            }
        };
    }

    /**
     * Met en pause l'exécution.
     */
    private void pauseExecution(int time)
    {
        try {
            Thread.sleep(time);
        } catch ( InterruptedException e ) {
            debug("ScrapingManager", "pauseExecution", "InterruptedException", e.getMessage(), false);
            LOGGER_SCRAPING.error("ERROR pauseExecution : {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Prépare une tâche de scraping pour extraire plusieurs éléments Html à partir d'un site web.<br>
     *
     * <p>
     * Cette méthode crée un appel asynchrone qui ouvre une page web à partir de l'URL. <br>
     * Dans un premier temps, on récupère chaque bloc Html représentant une annonce sur la page principale de la recherche. <br>
     * Dans un second temps une fois tous les annonces sont récupérées et stockées dans {@code List<HtmlElement> cards}:
     * <ul>
     *     <li>On crée un objet {@link ScrapingModel} {@code model}.</li>
     *     <li>On incrémente le total des annonces de {@link TaskCounter}.</li>
     *     <li>On execute {@link #scrapElements}.</li>
     * </ul>
     * Chaque objet {@code model} est soumise à l'exécuteur pour un traitement asynchrone, de {@link #scrapPage} pour scraper chacune des pages annonces.<br>
     * La boucle while parcours {@code List<Future<Void>} afin d'incrémenter le {@link TaskCounter} des pages annonces parcours, si la tâche est terminée.
     * </p>
     *
     * @param site L'objet {@link Site} contenant les informations sur le site à scrapper.
     * @return {@link Callable} et {@code List<ScrapingModelInt>} models.
     */
    private Callable<Void> scrapCards(Site site)
    {
        List<Future<Void>> futures = new ArrayList<>();
        String url = site.getSearchUrl();

        return () -> {
            WebClient webClient = createWebClient();
            HtmlPage page = openPage(webClient, url);

            if ( page != null ) {

                Dom domCard = site.getDomMap().get("card");
                List<Dom> childrensDom = domCard.geChildrensList();
                List<HtmlElement> cards = page.getByXPath(domCard.getXPath());


                this.counterTask.addSubCounter(site.getName(), cards.size());

                cards.forEach(card -> {
                    ScrapingModel<Object> model = recherche.createAndAddScrapingSupplier();
                    model.setSite(site);

                    this.scrapElements(card, childrensDom, model);

                    futures.add(executor.submit(
                                        scrapPage(webClient,
                                                  model.getUrl(),
                                                  model,
                                                  site.getDomMap().get("page")))
                               );
                });

            } else {
                this.counterTask.incrementMainCounterTotal();
                this.counterTask.addSubCounter(site.getName(), 0);
                debug("ScrapingManager", "ScrapCards", "openPage = null", null, false);
            }

            futures.forEach(future -> {
                try {
                    future.get();
                } catch ( InterruptedException | ExecutionException e ) {
                    LOGGER_SCRAPING.warn("ERROR FUTURE : {}", e.getMessage(), e);
                }
            });

            return null;
        };

    }

    /**
     * Prépare une tâche de scraping pour une page web.
     *
     * <p>Crée un appel asynchrone qui ouvre une page web à partir de l'URL fournie,
     * extrait un élément conteneur basé sur l'expression XPath fournie {@link Dom#getXPath()}, et scrappe les éléments
     * enfants en appelant la méthode {@link ScrapingManager#scrapElements(HtmlElement, List, ScrapingModel)}.</p>
     *
     * @param webClient Client web utilisé pour ouvrir la page
     * @param url       URL de la page à scrapper
     * @param model     Objet dans lequel les données extraites seront stockées
     * @param domPage   {@link Dom} représentant les XPath à scrapper
     * @return {@link Callable} qui réalise le scraping de la page
     */
    private Callable<Void> scrapPage(WebClient webClient, String url, ScrapingModel<Object> model, Dom domPage)
    {
        this.pauseExecution(new Random().nextInt(2000));

        return () -> {
            HtmlPage page = openPage(webClient, url);

            if ( (page != null) ) {

                try {
                    HtmlElement conteneur = page.getFirstByXPath(domPage.getXPath());

                    if ( conteneur == null )
                        throw new HTMLElementException(
                                "Url: " + url + " | Conteneur: " + domPage.getName() + " | XPath: " + domPage.getXPath());

                    List<Dom> childrensDom = domPage.geChildrensList();

                    this.scrapElements(conteneur, childrensDom, model);


                    this.counterTask.incrementSubCounterEnded(model.getSite().getName());


                } catch ( HTMLElementException e ) {
                    this.counterTask.decrementSubContentTotal(model.getSite().getName());
                    this.recherche.removeResult(model);

                    debug("ScrapingManager", "ScrapPage", "HTMLElementException", e.getMessage(), false);
                    LOGGER_SCRAPING.warn("ERROR SCRAP PAGE HTML_ELEMENT : {}", e.getMessage(), e);
                }

            } else {
                this.recherche.removeResult(model);
                this.counterTask.decrementSubCounterEnded(model.getSite().getName());
                this.counterTask.decrementSubContentTotal(model.getSite().getName());
                debug("ScrapingManager", "ScrapCards", "openPage = null", null, false);
            }

            return null;
        };
    }

    /**
     * Extrait des informations à partir d'un élément HTML conteneur.<br>
     * {@code List<Dom>childrenDom} liste des xPaths pour localiser les sous-éléments d'où extraire les valeurs.
     *
     * <p>Extrait soit un lien (attribut {@code href}), soit le contenu textuel de l'élément.</p>
     * <p>Les valeurs extraites sont enregistrées dans l'objet {@code ScrapingModel}.</p>
     *
     * @param conteneur    l'élément HTML conteneur d'où extraire les éléments
     * @param childrensDom la liste d'objets {@link Dom} contenant le xPath de l'éléments où extraire une valeur.
     * @param model        Objet où les valeurs extraites seront stockées
     * @return Liste de liens secondaires à visiter
     */
    private List<String> scrapElements(HtmlElement conteneur, List<Dom> childrensDom, ScrapingModel<Object> model)
    {
        List<String> liens = new ArrayList<>();

        childrensDom.forEach(dom -> {
            String value;

            //System.out.println(Config.YELLOW + "Element: " + Config.GREEN + dom.getNom() + Config.RESET);
            //System.out.println(Config.YELLOW + "Path: " + Config.CYAN + dom.getXPath() + Config.RESET);
            try {
                HtmlElement element = conteneur.getFirstByXPath("." + dom.getXPath());

                if ( element != null ) {
                    value = dom.getName().contains("lien") ? element.getAttribute("href").trim() : element.getTextContent().trim();
                    String domName = dom.getName();

                    switch (domName) {
                        case "lien":
                            value = element.getAttribute("href").trim();

                            model.setPropertieModel(dom.getName(), value);
                            break;
                        case "description":
                            value = element.asXml()
                                           .replaceAll("\\s*(class|id)=\"[^\"]*\"", "")
                                           .replaceAll("(?i)</?(strong|b|i|u|em|mark|small|big)[^>]*>", "");

                            model.setPropertieModel(dom.getName(), value);
                            break;
                        case "image":
                            value = this.getImage(element);
                            model.setPropertieModel(dom.getName(), value);
                            break;
                        default:
                            value = element.getTextContent().trim();
                            model.setPropertieModel(dom.getName(), value);
                            break;
                    }
                } else {
                    throw new HTMLElementException(" Element: " + dom.getName() + " | " + dom.getXPath() + "\n" + model.getUrl());
                }

            } catch ( HTMLElementException e ) {
                LOGGER_SCRAPING.warn("SCRAP ELEMENT : {}", e.getMessage(), e);
                debug("ScrapingManager", "ScrapElement", "HTMLElementException", e.getMessage(), false);
            }
        });
        return liens;
    }

    /**
     * Methode récupèrant l'URL d'un image à partir d'un élément HTML.
     *
     * <p>l'url recherché doit se trouver dans un Tag:
     * <ul>
     *     <li>{@code img} et contenir l'attribut {@code src} </li>
     *     <li>{@code source} et contenir l'attribut {@code srcset}</li>
     *     <li>{@code div} et contenir l'attribut {@code data-src}</li>
     * </ul>
     * approprié pour obtenir l'URL de l'image.</p>
     *
     * @param element l'élément HTML où extraire l'URL de l'image
     * @return l'URL de l'image, ou une chaîne vide si aucun URL n'est trouvé
     */
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

    /**
     * Ouvre une page web à partir de l'URL passée en paramètre.
     *
     * @param webClient le client web utilisé pour la requête
     * @param url       l'URL de la page à ouvrir
     * @return la page web ouverte, ou {@code null} en cas d'erreur
     */
    private HtmlPage openPage(WebClient webClient, String url)
    {
        HtmlPage page = null;
        try {
            page = webClient.getPage(url);
        } catch ( MalformedURLException e ) {
            debug("ScrapingManager", "OpenPage", "MalformedURLException", e.getMessage(), false);
            LOGGER_SCRAPING.error("Erreur Format url {} : {}", url, e.getMessage(), e);
        } catch ( FailingHttpStatusCodeException e ) {
            debug("ScrapingManager", "OpenPage", "FailingHttpStatusCode", e.getMessage(), false);
            int statusCode = e.getStatusCode();
            String errorMessage = e.getMessage();
            LOGGER_SCRAPING.error("Erreur Statut {} : {} - {}", url, statusCode, errorMessage, e);
        } catch ( Exception e ) {
            debug("ScrapingManager", "OpenPage", "Exception", e.getMessage(), false);
            LOGGER_SCRAPING.error("Erreur OpenPage {} : {}", url, e.getMessage(), e);
        }
        return page;
    }

    /**
     * Crée et configure un nouveau {@link WebClient}.
     * <p>
     * Les options du client Web configurées:</p>
     * <ul>
     *     <li>SSL non sécurisé {@link WebClient#getOptions()#setUseInsecureSSL(boolean)}</li>
     *     <li>Activation/Désactivation du CSS {@link WebClient#getOptions()#setCssEnabled(boolean)}</li>
     *     <li>Activation/Désactivation du Javascript {@link WebClient#getOptions()#setJavascriptEnabled(boolean)}</li>
     *     <li>Délai d'attente pour le Javascript {@link WebClient#getOptions()#waitForBackgroundJavaScript(long)}</li>
     *     <li>Délai d'attente pour la requete HTTP avant abandon {@link WebClient#getOptions()#setTimeOut(int)} </li>
     *     <li>Activation/Désactivation des cas d'erreurs de script {@link WebClient#getOptions()#setThrowExceptionOnScriptError(boolean)}</li>
     *     <li>Activation/Désactivation des codes de statuts échoués {@link WebClient#getOptions()#setThrowExceptionOnFailingStatusCode(boolean)}</li>
     * </ul>
     * Les valeurs sont definies dans {@link Setting}
     *
     * @return {@link WebClient} configuré
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

}