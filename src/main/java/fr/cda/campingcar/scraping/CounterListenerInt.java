package fr.cda.campingcar.scraping;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public interface CounterListenerInt
{
    void onMainCounter(Map<String, AtomicInteger> counter);
    void onMainCounterUpdateEnded(int endedCount);
    void onMainCounterUpdateTotal(int totalCount);
    void onSubProgressBloc(String counterName, Map<String, AtomicInteger> subCounter);
    void onSubCounter(String counterName, Map<String, AtomicInteger> subCounter);
    void onSubCounterUpdateEnded(String key, int endedCount);
    void onSubCounterUpdateTotal(String key, int total);
    void onProgressBarUpdated(double value);
}
