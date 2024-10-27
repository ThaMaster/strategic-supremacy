package se.umu.cs.ads.sp.performance;

import java.io.IOException;
import java.nio.file.Path;

public abstract class ITest {
    protected Long id;
    protected Path targetFile;

    public ITest(Long id) {
        this.id = id;
        start();
    }

    public abstract void start();

    public abstract void finish();

    public abstract void output() throws IOException;

    public void setTargetFile(Path path) {
        this.targetFile = path;
    }

    public Long getId() {
        return this.id;
    }
}
