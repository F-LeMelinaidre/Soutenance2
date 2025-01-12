package fr.cda.campingcar.util.file.tamplate.word;

/*
 * Soutenance Scraping
 * 2025/janv.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.model.Rent;
import fr.cda.campingcar.model.Search;
import fr.cda.campingcar.model.Site;
import fr.cda.campingcar.model.VehicleType;
import fr.cda.campingcar.scraping.ScrapingModel;
import fr.cda.campingcar.util.file.WordFile;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;


public class SearchRentXDOC extends WordFile
{
    private Search<Rent> search;

    public SearchRentXDOC(Search<Rent> recherche, String filePath)
    {
        super(filePath);
        this.initDocument(recherche);
    }
    public SearchRentXDOC(Search<Rent> recherche) {
        super("");
        this.initDocument(recherche);
    }

    private void initDocument(Search<Rent> recherche)
    {

        if(recherche != null) {
            this.search = recherche;
            this.newDocument();
            this.header();
            this.addCriteria();
            this.addListRent();

        }
    }

    private void header()
    {
        XWPFParagraph paragraph = this.document.createParagraph();
        XWPFRun run = paragraph.createRun();

        run.setText("Recherche du " + this.search.getDateString());

    }

    private void createTitle(XWPFRun run, String title)
    {

        run.setCapitalized(true);
        run.setUnderline(UnderlinePatterns.SINGLE);
        run.setText(title);
        run.setUnderline(UnderlinePatterns.NONE);
        run.setCapitalized(false);
    }

    private void addCriteria()
    {
        XWPFParagraph paragraph = this.document.createParagraph();
        XWPFRun run = paragraph.createRun();
        this.createTitle(run, "Critères de la recherche :");
        run.addBreak();

        this.addListSites();
        this.addListVehicle();

        this.createTitle(run, "Budget journalier entre : ");

        run.setText(this.search.getBudgetMin() + " et " + this.search.getBudgetMax() );
        run.addBreak();

        this.createTitle(run, "Ville départ : ");
        run.setText(this.search.getDepartureCityString());
        run.addBreak();

        this.createTitle(run, "Ville arrivée : ");
        run.setText(this.search.getArrivalCityString());
        run.addBreak();

        run.setText("Période du : " + this.search.getPeriodStartString() + " au " + this.search.getPeriodEndString() );
        run.addBreak();


    }

    private void addListSites()
    {
        XWPFParagraph paragraph = this.document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        this.createTitle(run, "Liste des sites :");
        run.setBold(false);
        run.addBreak();

        for( Site site : this.search.getListSites()) {
            run.addTab();
            run.setText(site.getName().toUpperCase());
            run.setText(" - " + site.getUrlRoot());
            run.addBreak();
        }

    }

    private void addListVehicle()
    {
        XWPFParagraph paragraph = this.document.createParagraph();
        XWPFRun run = paragraph.createRun();
        this.createTitle(run, "Liste des vehicles :");
        run.addBreak();

        for ( VehicleType vehicleType : this.search.getVehiclesType() ) {
            run.addTab();
            run.setCapitalized(true);
            run.setText("- " + vehicleType.getType());
            run.addBreak();
        }
    }

    private void addListRent() {
        int hyperlinkCount = 1;

        for ( ScrapingModel<Object> model : this.search.getResults()) {
            Rent rent = (Rent) model;

            XWPFParagraph paragraph = this.document.createParagraph();
            XWPFRun run = paragraph.createRun();

            run.setCapitalized(true);
            this.createTitle(run, rent.getSite().getName());
            run.setCapitalized(false);
            run.addBreak();

            run.setText(rent.getTitle());
            run.addBreak();


            run.setText("Url : ");

            XWPFHyperlinkRun hyperlinkRun = paragraph.createHyperlinkRun(rent.getUrl());
            hyperlinkRun.setText("cliquer ici");
            hyperlinkRun.setUnderline(UnderlinePatterns.SINGLE);
            hyperlinkRun.setColor("0000FF");
            hyperlinkRun.setBold(true);

            run = paragraph.createRun();
            run.setText(" pour visiter la page de l'annonce");
            run.addBreak();

            run.setText("Ville : " + rent.getCity());
            run.addTab();
            run.setText("Tarif : " + rent.getPrice() + "€");
            run.addBreak();

            this.createTitle(run, "Caratéristiques : ");
            run.addBreak();

            run.setText("Nombre de places : ");
            run.setText(String.valueOf(rent.getSeat()));
            run.addTab();
            run.setText("Nombre de couchages : ");
            run.setText(String.valueOf(rent.getBed()));
            run.addBreak();
            run.setText("Carburant : ");
            run.setText(rent.getFuel());
            run.addTab();
            run.setText("Transmission : ");
            run.setText(rent.getGearBox());
            run.addBreak();

            this.createTitle(run, "Equipements : ");
            run.addBreak();

            run.setText("Douche : ");
            String shower = (rent.getShower()) ? "Oui" : "Non";
            run.setText(shower);
            run.addTab();
            run.setText("WC : ");
            String wc = (rent.getWc()) ? "Oui" : "Non";
            run.setText(wc);
        }
    }
}
