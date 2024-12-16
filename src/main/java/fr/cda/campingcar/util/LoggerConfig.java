package fr.cda.campingcar.util;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerConfig {
    private static final Logger LOGGER = LogManager.getLogger(LoggerConfig.class);
    private static final Logger LOGGER_FILE = LogManager.getLogger("FileLog");
    private static final Logger LOGGER_SCRAPING = LogManager.getLogger("ScrapingLog");
    private static final Logger LOGGER_USER_ACTION = LogManager.getLogger("UserActions");
    private static final Logger LOGGER_DAO_ERROR = LogManager.getLogger("DaoErrors");

    public static Logger getLogger() {
        return LOGGER;
    }

    public static Logger getLoggerFile() {return LOGGER_FILE;}

    public static Logger getLoggerScraping() { return LOGGER_SCRAPING; }

    public static Logger getLoggerUserAction() {
        return LOGGER_USER_ACTION;
    }

    public static Logger getLoggerDaoError() {
        return LOGGER_DAO_ERROR;
    }
}
