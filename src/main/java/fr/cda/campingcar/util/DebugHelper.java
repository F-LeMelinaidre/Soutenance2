package fr.cda.campingcar.util;

import fr.cda.campingcar.settings.Config;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public class DebugHelper
{
    public static void debug(String titre, String message, Boolean valid)
    {
        String color = (valid == null) ? Config.WHITE : (valid)? Config.GREEN : Config.RED;
        StringBuilder sb = new StringBuilder(Config.PURPLE);
        sb.append(Config.YELLOW).append(String.format("%3s", "")).append(titre).append(":").append("\n");
        sb.append(color).append(String.format("%4s", "")).append(message).append("\n");

        sb.append(Config.RESET);
        System.out.println(sb);
        sb.setLength(0);
    }

    public static void debug(String className, String methode, String status, String message, Boolean valid) {
        String color = (valid == null) ? Config.WHITE : (valid)? Config.GREEN : Config.RED;

        StringBuilder sb = new StringBuilder(Config.YELLOW);
        sb.append("Class:").append(String.format("%2s", "")).append(Config.PURPLE).append(className).append(Config.WHITE).append(" - ").append(Config.YELLOW);
        sb.append("Method:").append(String.format("%2s", "")).append(Config.PURPLE).append(methode).append("\n");
        sb.append(String.format("%4s", "")).append(Config.YELLOW).append("Status:").append(String.format("%2s", "")).append(color).append(status).append("\n");
        sb.append(String.format("%4s", "")).append(Config.WHITE).append(message).append("\n");
        sb.append(Config.RESET);
        System.out.println(sb);
        sb.setLength(0);
    }

}
