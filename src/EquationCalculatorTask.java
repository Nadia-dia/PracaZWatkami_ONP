import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

public class EquationCalculatorTask implements Callable<Void> {
    private final String equation;
    private final String filePath;

    public EquationCalculatorTask(String equation, String filePath) {
        this.equation = equation;
        this.filePath = filePath;
    }

    @Override
    public Void call() {
        if (equation == null || equation.isEmpty()) {
            return null;
        }

        System.out.println("[CALCULATOR] " + Thread.currentThread().getName() + " zaczyna przetwarzanie: " + equation);

        ONP onp = new ONP();
        String onpExpression = onp.przeksztalcNaOnp(equation);
        String resultStr = onp.obliczOnp(onpExpression);

        System.out.println("[CALCULATOR] " + Thread.currentThread().getName() + " obliczył wynik: " + resultStr +
                " dla równania: " + equation);

        Main.writeLock.lock();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).trim().equals(equation.trim())) {
                    lines.set(i, equation + " " + resultStr);
                    System.out.println("[WRITER] " + Thread.currentThread().getName() + " zapisuje: " + lines.get(i));
                }
            }
            Files.write(Paths.get(filePath), lines);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Main.writeLock.unlock();
        }

        return null;
    }
}


