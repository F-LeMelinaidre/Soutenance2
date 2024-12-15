/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

module fr.cda.campingcar {
    requires com.fasterxml.jackson.databind;
    requires htmlunit;
    requires java.net.http;
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires org.controlsfx.controls;
    requires org.apache.commons.text;
    requires org.apache.commons.lang3;
    requires net.bytebuddy;
    requires htmlunit.xpath;

    opens fr.cda.campingcar to javafx.fxml;
    exports fr.cda.campingcar;
    exports fr.cda.campingcar.controller;
    exports fr.cda.campingcar.scraping;
    exports fr.cda.campingcar.model;
    opens fr.cda.campingcar.controller to javafx.fxml;
}