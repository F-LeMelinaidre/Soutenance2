package fr.cda.campingcar.scraping;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import org.htmlunit.BrowserVersion;

public class Setting
{
    public static final BrowserVersion BROWSER_VERSION = BrowserVersion.CHROME;
    public static final boolean SSL = true;
    public static final boolean ENABLE_CSS = false;
    public static final boolean ENABLE_JS = false;
    public static final int TIMEOUT = 15000;
    public static final int JS_TIMEOUT = 8000;
    public static final boolean THROW_EXCEPTION_ON_JS_ERROR = false;
    public static final boolean THROW_EXCEPTION_ON_STATUS_CODE = false;

    public static final String DOM_CARD = "card";
    public static final String DOM_PAGE = "page";
}
