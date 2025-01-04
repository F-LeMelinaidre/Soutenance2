package fr.cda.campingcar.scraping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class TaskCounter
{
    private static TaskCounter _instance;
    private static CounterListenerInt _listener;
    ;
    private List<String> mainTaskNames = new ArrayList<>();

    private Map<String, AtomicInteger> mainTaskCounter;
    private final Map<String, Map<String, AtomicInteger>> subCounters = new HashMap<>();
    private Map<String, AtomicInteger> progressCounter;

    public static TaskCounter getInstance()
    {
        if ( _instance == null ) {
            _instance = new TaskCounter();
        }
        return _instance;
    }

    public static void addListener(CounterListenerInt listener)
    {
        _listener = listener;
    }

    public void setMainCounterTotal(int total)
    {
        this.mainTaskCounter = new HashMap<>(Map.of("ended", new AtomicInteger(0),
                                                    "total", new AtomicInteger(total)));
        this.subCounters.clear();
        this.progressCounter = new HashMap<>(Map.of("ended", new AtomicInteger(0),
                                                    "total", new AtomicInteger(0)));

        this.notifyMainCounter();
        /*this.mainTaskNames = mainTaskNames;
        this.initMainTaskCounter(this.mainTaskNames.size());
        this.initSubTasksCounter(this.mainTaskNames);*/
    }


    public void incrementMainCounterEnded()
    {
        int count = this.mainTaskCounter.get("ended").incrementAndGet();
        this.notifyMainCounterEnded(count);
    }

    public void incrementMainCounterTotal()
    {
        int count = this.mainTaskCounter.get("total").incrementAndGet();
        this.notifyMainCounterTotal(count);

    }

    public void decrementMainCounterEnded()
    {
        int count = this.mainTaskCounter.get("ended").decrementAndGet();
        this.notifyMainCounterEnded(count);
    }

    public void decrementMainCounterTotal()
    {
        int count = this.mainTaskCounter.get("total").decrementAndGet();
        this.notifyMainCounterTotal(count);
    }

    private void checkAndIncrementMainCounter(String counterName) {
        int current = this.subCounters.get(counterName).get("ended").get();
        int total = this.subCounters.get(counterName).get("total").get();

        if (current == total) {
            this.incrementMainCounterEnded();
        }
    }

    private void notifyMainCounter()
    {
        _listener.onMainCounter(this.mainTaskCounter);
    }

    private void notifyMainCounterEnded(int count)
    {
        _listener.onMainCounterUpdateEnded(count);
    }

    private void notifyMainCounterTotal(int count)
    {
        _listener.onMainCounterUpdateTotal(count);
    }


    public void addSubCounter(String name, int size)
    {
        Map<String, AtomicInteger> subCounter = new HashMap<>();
        subCounter.put("ended", new AtomicInteger(0));
        subCounter.put("total", new AtomicInteger(size));

        this.subCounters.put(name, subCounter);
        this.notifySubProgressBloc(name, subCounter);

        this.setProgressCounter(size);
        this.notifyProgressBarState();
    }

    public void decrementSubCounterEnded(String counterName)
    {
        this.decrementSubCounter(counterName, "ended");
    }

    public void decrementSubContentTotal(String counterName)
    {
        this.decrementSubCounter(counterName, "total");
    }

    private void decrementSubCounter(String counterName, String target)
    {
        Map<String, AtomicInteger> counters = this.subCounters.get(counterName);

        if ( counters != null && counters.containsKey(target) ) {
            int counter = counters.get(target).decrementAndGet();
            this.progressCounter.get(target).decrementAndGet();

            if ( target.equals("ended") ) {
                this.notifySubCounterEnded(counterName, counter);
                this.checkAndIncrementMainCounter(counterName);
            } else if ( target.equals("total") ) {
                this.notifySubCounterTotal(counterName, counter);
            }
            this.notifyProgressBarState();
        }
    }

    public void incrementSubCounterEnded(String counterName)
    {
        this.incrementSubCounter(counterName, "ended");
    }

    public void incrementSubCounterTotal(String counterName)
    {
        this.incrementSubCounter(counterName, "total");
    }

    private void incrementSubCounter(String counterName, String target)
    {
        Map<String, AtomicInteger> counters = this.subCounters.get(counterName);

        if ( counters != null && counters.containsKey(target) ) {
            int counter = counters.get(target).incrementAndGet();
            this.progressCounter.get(target).incrementAndGet();



            if( target.equals("ended") ) {
                this.notifySubCounterEnded(counterName, counter);
                this.checkAndIncrementMainCounter(counterName);
            } else if ( target.equals("total") ) {
                this.notifySubCounterTotal(counterName, counter);
            }

            this.notifyProgressBarState();
        }
    }

    private void notifySubProgressBloc(String counterName, Map<String, AtomicInteger> counters)
    {
        _listener.onSubProgressBloc(counterName, counters);
    }

    private void notifySubCounterEnded(String counterName, int endedCount)
    {
        _listener.onSubCounterUpdateEnded(counterName, endedCount);
    }

    private void notifySubCounterTotal(String counterName, int totalCount)
    {
        _listener.onSubCounterUpdateTotal(counterName, totalCount);
    }

    public void setProgressCounter(int size) {
        this.progressCounter.get("total").addAndGet(size);
    }

    public void incrementGlobalCounterEnded()
    {
        this.incrementGlobalCounter("ended");
    }

    private void incrementGlobalCounter(String target)
    {
        Map<String, AtomicInteger> counter = this.progressCounter;

        if ( counter != null && counter.containsKey(target) ) {
            counter.get(target).incrementAndGet();
            this.notifyProgressBarState();
        }
    }

    public void decrementGlobalCounterEnded()
    {
        this.decrementGlobalCounter("ended");
    }

    public void decrementGlobalCounterTotal()
    {
        this.decrementGlobalCounter("total");
    }

    private void decrementGlobalCounter(String target)
    {
        Map<String, AtomicInteger> counter = this.progressCounter;

        if ( counter != null && counter.containsKey(target) ) {
            counter.get(target).decrementAndGet();
            this.notifyProgressBarState();
        }
    }

    private double getProgressState()
    {
        return ((double) 1 / this.progressCounter.get("total").get()) * this.progressCounter.get("ended").get();
    }

    private void notifyProgressBarState()
    {
        _listener.onProgressBarUpdated(this.getProgressState());
    }
}
