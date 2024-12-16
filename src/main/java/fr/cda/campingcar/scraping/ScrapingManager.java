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

import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class permettant le scraping asynchrone d'une liste de recherche de pages internet.
 * <p>
 * L'objet {@link Recherche} passé en paramètre doit contenir une liste d'objet {@link Site} lui même contenant l'url de recherche, et un {@code HashMap}, représentant les xPaths des élément Html à scraper.
 * </p>
 * <p>
 * Le {@code HashMap<String, Dom>} est hiérarchise:
 * <ul>
 *      <li>La clé "card" représente le conteneur de l'annonce présente sur la première page du site.
 *          <ul>
 *              <li>Le xPath est accessible avec la méthode {@link Dom#getXPath()}</li>
 *              <li>Ce {@link Dom} contient une {@code List} représentant les éléments enfant de ce conteneur à scraper {@link Dom#getListEnfants()}.</li>
 *          </ul>
 *     </li>
 *     <li> La clé "page" représente la page lié à chaque annonce et est hiérarchisé de la même manière.</li>
 * </ul>
 * </p>
 * <p>
 * Chaque Annonce scrapé est stocké dans un objet qui implémente l'interface {@link ScrapingModelInt}.
 * La class implémentant {@link ScrapingModelInt}, est passer à l'objet {@link Recherche} à l'instanciation de celui-ci.
 * Cela nous permet:
 * <ul>
 *     <li>Créer des objets Annonce (ou autre si une autre class était passé en argument de Recherche)</li>
 *     <li>D'accéder à des méthodes pour setter les propriétés de l'objet</li>
 * </ul>
 * </p>
 * <p>
 * Une fois la tâche principale terminée, la méthode {@link ScrapingManager#scrapTask(Recherche)},<br>
 * retourne l'objet recherche contenant l'ensemble des objets Annonce scrapé sur le ou les sites.
 * </p>
 * <p>
 * Cette class est un Singleton, afin de pouvoir avoir accès au compteur des tâches en cours, terminées.
 * Il est essentiellement utile pour la vue du loader et son contrôleur.
 * </p>
 */
public class ScrapingManager
{
    private static final Logger LOGGER_SCRAPING = LoggerConfig.getLoggerScraping();

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
                List<Future<List<ScrapingModelInt>>> futures = new ArrayList<>();

                // PARCOURS LES SITES DE LA RECHERCHE
                for ( Site site : sites ) {
                    LOGGER_SCRAPING.info("Recherche sur " + site.getNom());
                    // CREE LA TACHE POUR OUVRIR LA PAGE DU SITE
                    futures.add(executor.submit(scrapingCards(site)));
                }

                for ( Future<List<ScrapingModelInt>> future : futures ) {
                    try {
                        List<ScrapingModelInt> resultats = future.get();

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
     * Prépare une tâche de scraping pour extraire plusieurs éléments Html à partir d'un site web.
     *
     * <p>
     * Cette méthode crée un appel asynchrone qui ouvre une page web à partir de l'URL. <br>
     * Dans un premier temps, on récupère chaque annonce de la page principale de la recherche. <br>
     * Dans un second temps une fois tous les annonces sont récupérées et stockées dans {@code List}, <br>
     * chaque annonce est soumise à l'exécuteur pour un traitement asynchrone, afin de scraper les pages de chaque annonce.
     * </p>
     *
     * @param site L'objet {@link Site} contenant les informations sur le site à scrapper.
     * @return {@link Callable} qui réalise le scraping des éléments.
     */
    private Callable<List<ScrapingModelInt>> scrapingCards(Site site)
    {
        debug("SCRAPING_CARDS", "Site", site.getNom(), site.getUrlRecherche(), true);
        List<Future<Void>>     futures = new ArrayList<>();
        List<ScrapingModelInt> models  = new ArrayList<>();
        String                 url     = site.getUrlRecherche();


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

                    ScrapingModelInt model = recherche.createScrapingSupplier();
                    model.setDomainUrl(site.getUrlRoot());

                    this.scrapElements(card, domCardEnfants, model);

                    models.add(model);
                });

            }

            for ( ScrapingModelInt model : models ) {

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
     * Prépare une tâche de scraping pour une page web.
     *
     * <p>Crée un appel asynchrone qui ouvre une page web à partir de l'URL fournie,
     * extrait un élément conteneur basé sur l'expression XPath fournie {@link Dom#getXPath()}, et scrappe les éléments
     * enfants en appelant la méthode {@link ScrapingManager#scrapElements(HtmlElement, List, ScrapingModelInt)}.</p>
     *
     * @param webClient Client web utilisé pour ouvrir la page
     * @param url       URL de la page à scrapper
     * @param model     Objet dans lequel les données extraites seront stockées
     * @param domPage   {@link Dom} représentant les XPath à scrapper
     * @return {@link Callable} qui réalise le scraping de la page
     */
    private Callable<Void> scrapPage(WebClient webClient, String url, ScrapingModelInt model, Dom domPage)
    {
        //debug("SCRAPING PAGE", "Page", String.valueOf(p.get()), null, true);
        AtomicReference<List<String>> pagesSecondaire = new AtomicReference<>(new ArrayList<>());
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
                        throw new HTMLElementException("Url: " + url +
                                                       " | Contneur: " + domPage.getNom() +
                                                       " | XPath: " + domPage.getXPath());

                    List<Dom>    domEnfants      = domPage.getListEnfants();
                    pagesSecondaire.set(this.scrapElements(conteneur, domEnfants, model));

                } catch ( HTMLElementException e ) {
                    debug("ERROR SCRAP PAGE HTML_ELEMENT", "HTMLElementException", domPage.getNom(),
                          page.getDocumentURI() + " " + domPage.getXPath(),
                          false);
                    LOGGER_SCRAPING.warn(
                            "ERROR SCRAP PAGE HTML_ELEMENT : " + e.getMessage(), e);
                }


                debug("SCRAPING PAGE", "Page Secondaire", String.valueOf(pagesSecondaire.get().size()), pagesSecondaire.toString(), true);
            }
            return null;
        };
    }

    /**
     * Extrait des informations à partir d'un élément HTML conteneur.<br>
     * {@link List<Dom>} liste des xPaths pour localiser les sous-éléments d'où extraire les valeurs.
     *
     * <p>Extrait soit un lien (attribut {@code href}), soit le contenu textuel de l'élément.</p>
     * <p>Les valeurs extraites sont enregistrées dans l'objet {@code ScrapingModel}.</p>
     *
     * @param conteneur      l'élément HTML conteneur d'où extraire les éléments
     * @param domCardEnfants la liste d'objets {@link Dom} contenant le xPath de l'éléments où extraire une valeur.
     * @param model          Objet où les valeurs extraites seront stockées
     * @return Liste de liens secondaires à visiter
     * @throws HTMLElementException Exception levé si l'élément n'est pas trouvé avec l'xPath
     */
    private List<String> scrapElements(HtmlElement conteneur, List<Dom> domCardEnfants, ScrapingModelInt model)
    {
        List<String> liensSecondaire = new ArrayList<>();

        domCardEnfants.forEach(dom -> {

            try {
                HtmlElement element = conteneur.getFirstByXPath("." + dom.getXPath());

                // TODO AMELIORER RENDRE PLUS GENERIQUE
                // AIGUILLER SUIVANT LE TAG, L ATTRIBUT, LE XPATH
                // VOIR SI LE XPATH A LA METHODE CONTAINS ATTRIBUER LA VALEUR TRUE
                if ( element != null ) {
                    String value = dom.getNom().contains("lien") ? element.getAttribute("href").trim() : element.getTextContent().trim();

                    if ( dom.getNom().equals("lien secondaire") ) {
                        // TODO IMPLEMENTER L OUVERTUR DES MODALS
                        liensSecondaire.add(value);
                    } else if ( dom.getNom().equals("image") ) {

                        value = this.getImage(element);
                    }
                    if(dom.getNom().equals("douche") || dom.getNom().equals("wc")){
                        System.out.println(element.asXml().trim());
                    }

                    if ( !value.isEmpty() ) {
                        model.setPropertieModel(dom.getNom(), value);
                    } else {
                        model.setPropertieModel(dom.getNom(), true);
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

    /**
     * Methode de réinitialisation de la class ScrapingManager.<br>
     * Arrête l'{@code Executors} actuel et créé un nouveau pool de threads mis en cache.<br>
     * <p>
     * Interrompt toutes les tâches actuellement en cours , entraînant l'arrêt des tâches.</p>
     */
    public void reset()
    {
        executor.shutdownNow();
        executor = Executors.newCachedThreadPool();
        LOGGER_SCRAPING.info("ScrapingManager reset.");
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

    /**
     * @param task    Nom de la Task
     * @param titre   Titre de l'événement
     * @param statut  Status de L'événement
     * @param details Détail de l'événement
     * @param valid   true invalide false (défini la couleur de la valeur du paramètre statut)
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