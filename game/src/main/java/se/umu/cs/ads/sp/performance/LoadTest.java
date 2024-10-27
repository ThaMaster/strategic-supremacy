package se.umu.cs.ads.sp.performance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class LoadTest extends ITest {

    private int totalSize;

    public LoadTest(Long id) {
        super(id);
    }

    @Override
    public void start() {
        totalSize = 0;
    }

    @Override
    public void finish() {

    }

    @Override
    public void output() throws IOException {
        Files.writeString(targetFile, totalSize + "," + System.lineSeparator(), StandardOpenOption.APPEND);
    }

    public void addMessageSize(byte[] bytes) {
        this.totalSize += bytes.length;
    }
}
