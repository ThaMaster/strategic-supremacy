package se.umu.cs.ads.sp.performance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class ConsistencyTest extends ITest {

    private double errors;
    private double numClients;

    public ConsistencyTest(Long id) {
        super(id);
    }

    @Override
    public void start() {
        errors = 0;
        numClients = 1;
    }

    @Override
    public void finish() {

    }

    public void setNumClients(double clients) {
        this.numClients = clients;
    }

    public void addError() {
        errors++;
    }

    @Override
    public void output() throws IOException {
        Files.writeString(targetFile, "Total: " + errors + " clients " + numClients + ": " + errors / numClients + "," + System.lineSeparator(), StandardOpenOption.APPEND);
    }
}
