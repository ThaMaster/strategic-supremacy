package se.umu.cs.ads.sp.PerformanceTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class PerformanceTest {
    private Long id;
    private Long startTime;
    private Long endTime;
    private Path targetFile;
    public PerformanceTest(Long id){
        this.id = id;
        startTime = System.currentTimeMillis();
    }

    public void finish(){
        endTime = System.currentTimeMillis();
    }

    public Long getId(){
        return this.id;
    }

    public void setPath(Path path){
        this.targetFile = path;
    }

    public void output() throws IOException {
        Files.writeString(targetFile, endTime-startTime + System.lineSeparator(), StandardOpenOption.APPEND);
    }
}
