import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.*;

public class Main {

    private static final String SCIEZKA_DO_PLIKU = "rownania.txt";

    public static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    public static final Lock readLock = rwl.readLock();
    public static final Lock writeLock = rwl.writeLock();
    private static final ReentrantLock lock = new ReentrantLock(); // Dodatkowy lock dla Condition
    private static final Condition allTasksDone = lock.newCondition(); // Condition dla zakończenia zadań

    private static final ExecutorService executorReader = Executors.newCachedThreadPool();
    private static final ExecutorService executorCalculator = Executors.newCachedThreadPool();

    private static int totalTasks = 0; // Liczba zadań do wykonania
    private static int completedTasks = 0; // Liczba ukończonych zadań

    public static void main(String[] args) {

        List<Future<String>> futureEquations = new ArrayList<>();

        int lineCount = countLinesInFile();
        System.out.println("[MAIN] Liczba linii w pliku: " + lineCount);

        totalTasks = lineCount; // Ustawiamy liczbę zadań

        for (int i = 0; i < lineCount; i++) {
            Future<String> futureEquation = executorReader.submit(new EquationReader(i, SCIEZKA_DO_PLIKU));
            futureEquations.add(futureEquation);
        }

        for (Future<String> futureEquation : futureEquations) {
            try {
                String equation = futureEquation.get();
                if (equation != null) {
                    FutureTask<Void> calculatorTask = new FutureTask<>(new EquationCalculatorTask(equation, SCIEZKA_DO_PLIKU)) {
                        @Override
                        protected void done() {
                            try {
                                get();
                                System.out.println("[MAIN] Zadanie kalkulacji zakończone");
                                markTaskAsCompleted(); // zaznaczamy zadanie jako zakonczone po oblicyeniu i zapisie
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    executorCalculator.submit(calculatorTask);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        awaitAllTasksCompletion(); // Czekamy na zakończenie wszystkich zadań

        executorReader.shutdown();
        executorCalculator.shutdown();
    }

    static void markTaskAsCompleted() {
        lock.lock();
        try {
            completedTasks++;
            if (completedTasks == totalTasks) {
                allTasksDone.signalAll(); // Powiadamiamy o zakończeniu wszystkich zadań
            }
        } finally {
            lock.unlock();
        }
    }

    private static void awaitAllTasksCompletion() {
        lock.lock();
        try {
            while (completedTasks < totalTasks) {
                allTasksDone.await(); // Czekamy, aż wszystkie zadania zostaną zakończone
            }
            System.out.println("[MAIN] Wszystkie zadania zakończone.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private static int countLinesInFile() {
        readLock.lock();
        try (BufferedReader reader = new BufferedReader(new FileReader(SCIEZKA_DO_PLIKU))) {
            int counter = 0;
            while (reader.readLine() != null) {
                counter++;
            }
            return counter;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } finally {
            readLock.unlock();
        }
    }
}
