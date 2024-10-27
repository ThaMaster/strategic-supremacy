package se.umu.cs.ads.sp.performance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class PerformanceTest extends ITest {

    protected Long startTime;
    protected Long endTime;

    public PerformanceTest(Long id) {
        super(id);
    }

    public void setTargetFile(Path path) {
        this.targetFile = path;
    }

    @Override
    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void finish() {
        endTime = System.currentTimeMillis();
    }

    public void output() throws IOException {
        Files.writeString(targetFile, endTime - startTime + "," + System.lineSeparator(), StandardOpenOption.APPEND);
    }
}
