package fr.cda.campingcar.util;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import javafx.concurrent.Task;

import java.util.LinkedList;
import java.util.List;

public class TaskManager {

    private final LinkedList<Task<?>> sequentialTasks = new LinkedList<>();
    private final List<Task<?>> parallelTasks = new LinkedList<>();

    /**
     * Execute une tâche en parallèle
     * @param task
     */
    public void executeParalleTask(Task<?> task) {
        new Thread(task).start();
        this.parallelTasks.add(task);
    }

    /**
     * Execute une tâche en séquence
     * @param task
     */
    public void executeSequentialTask(Task<?> task) {
        this.sequentialTasks.add(task);
        if (this.sequentialTasks.size() == 1) {
            this.runNextSequentialTask();
        }
    }

    /**
     * Execute la tâche suivante en séquence
     */
    private void runNextSequentialTask() {
        if(!sequentialTasks.isEmpty()) {
            Task<?> task = this.sequentialTasks.getFirst();
            task.setOnSucceeded(e -> {
                this.sequentialTasks.removeFirst();
                this.runNextSequentialTask();
            });

            new Thread(task).start();
        }
    }

    /**
     * Execute une série de tâches en séquence
     * @param tasks
     */
    public void executeTasksSequence(List<Task<?>> tasks) {
        for(Task<?> task : tasks) {
            this.executeSequentialTask(task);
        }
    }

    /**
     * Execute une liste de tâche en parallèle
     * @param tasks
     */
    public void executeTasksParallel(List<Task<?>> tasks) {
        for(Task<?> task : tasks) {
            this.executeParalleTask(task);
        }
    }
}
