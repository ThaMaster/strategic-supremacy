package se.umu.cs.ads.sp.performance;

import java.io.IOException;
import java.nio.file.Path;

public interface ITest {
    public void start();
    public void finish();
    public void setTargetFile(Path path);
    public void output() throws IOException;

    public Long getId();
}
