import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;

public class EquationReader implements Callable<String> {
    private final int lineNumber;
    private final String filePath;

    public EquationReader(int lineNumber, String filePath) {
        this.lineNumber = lineNumber;
        this.filePath = filePath;
    }

    @Override
    public String call() {
        Main.readLock.lock();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int currentLine = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                if (currentLine == lineNumber) {
                    System.out.println("[READER] " + Thread.currentThread().getName() + " przeczytał linię " + lineNumber + ": " + line);
                    return line;
                }
                currentLine++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Main.readLock.unlock();
        }
        return null;
    }
}

